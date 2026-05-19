package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import gov.nysenate.ess.core.dao.pec.everfi.EverfiEmployeeMappingDao;
import gov.nysenate.ess.core.model.pec.everfi.EverfiEmployeeMapping;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiAddUserCommand;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUpdateUserCommand;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUser;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUserClient;
import org.jetbrains.annotations.Nullable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Carries out the actions chosen by the planner. Writes to the Everfi API and the local mapping
 * table; never makes a planning decision of its own. SKIP and FLAG actions don't touch any system,
 * but still produce a {@link SyncResult} so they appear in the report.
 *
 * <p>A few behaviours are worth calling out:
 * <ul>
 *   <li><b>CREATE</b> provisions the Everfi user first, then inserts the mapping row using the UUID
 *       returned by Everfi. Newly created users carry today's <i>Upload List</i> label so admins can
 *       trace the cohort.</li>
 *   <li><b>UPDATE</b> preserves a personal-email override (a non-{@code @nysenate.gov} address set
 *       directly in Everfi) and replaces only labels in categories the sync manages — admin-added
 *       labels and external-system labels are left untouched.</li>
 *   <li><b>DEACTIVATE</b> rewrites the email to {@code deactivated-{empId}@nysenate.invalid}. This
 *       frees the original address so the same employee can be re-provisioned later, and prevents
 *       Everfi from sending mail to a stale address.</li>
 * </ul>
 *
 * <p>{@code dryRun} suppresses all writes but still produces a SUCCESS result, so reports describe
 * what would have been done.
 */
@Service
public class EverfiUserSyncExecutor {

    private static final String DEACTIVATED_EMAIL_DOMAIN = "@nysenate.invalid";

    private final EverfiUserClient everfiUserClient;
    private final EverfiEmployeeMappingDao everfiEmployeeMappingDao;

    public EverfiUserSyncExecutor(
            EverfiUserClient everfiUserClient,
            EverfiEmployeeMappingDao everfiEmployeeMappingDao
    ) {
        this.everfiUserClient = everfiUserClient;
        this.everfiEmployeeMappingDao = everfiEmployeeMappingDao;
    }

    /**
     * Executes every action in order and returns a result per action.
     * All label resolution and creation has already occurred before this method is called.
     */
    List<SyncResult> executeAll(List<ExecutableAction> actions, boolean dryRun) {
        List<SyncResult> results = new ArrayList<>();
        for (ExecutableAction action : actions) {
            results.add(switch (action.action()) {
                case CREATE -> executeCreate(action, dryRun);
                case UPDATE -> executeUpdate(action, dryRun);
                case REACTIVATE -> executeReactivate(action, dryRun);
                case DEACTIVATE -> executeDeactivate(action, dryRun);
                case SKIP -> SyncResult.skipped(action.plannedAction());
                case FLAG -> SyncResult.flagged(action.plannedAction());
            });
        }
        return results;
    }

    private SyncResult executeCreate(ExecutableAction action, boolean dryRun) {
        var desired = action.requireDesired();
        return executeSafely(action.plannedAction(), dryRun, () -> {
            List<EverfiCategoryLabel> labels = new ArrayList<>(action.desiredLabels());
            labels.add(action.requireUploadListLabel());
            EverfiUser everfiUser = everfiUserClient.addUser(new EverfiAddUserCommand(
                    desired.employeeId(),
                    desired.firstName(),
                    desired.lastName(),
                    desired.email(),
                    labels
            ));
            everfiEmployeeMappingDao.insert(new EverfiEmployeeMapping(
                    desired.employeeId(),
                    requireEverfiUuid(everfiUser)
            ));
        });
    }

    private SyncResult executeUpdate(ExecutableAction action, boolean dryRun) {
        var desired = action.requireDesired();
        var remote = action.requireRemote();
        var mapping = action.plannedAction().requireRemoteMapping();
        return executeSafely(action.plannedAction(), dryRun, () ->
                requireEverfiResponse(everfiUserClient.updateUser(EverfiUpdateUserCommand.builder()
                        .uuid(mapping.everfiUuid())
                        .employeeId(mapping.employeeId())
                        .firstName(desired.firstName())
                        .lastName(desired.lastName())
                        .email(resolveUpdateEmail(desired, remote))
                        .active(true)
                        .categoryLabels(categoryLabelsForUpdate(action.desiredLabels(), remote))
                        .build())));
    }

    private SyncResult executeReactivate(ExecutableAction action, boolean dryRun) {
        var desired = action.requireDesired();
        var remote = action.requireRemote();
        var mapping = action.plannedAction().requireRemoteMapping();
        if (desired.email() == null) {
            return SyncResult.error(action.plannedAction(), "Cannot reactivate user without email");
        }
        return executeSafely(action.plannedAction(), dryRun, () ->
                requireEverfiResponse(everfiUserClient.updateUser(EverfiUpdateUserCommand.builder()
                        .uuid(mapping.everfiUuid())
                        .employeeId(mapping.employeeId())
                        .firstName(desired.firstName())
                        .lastName(desired.lastName())
                        .email(desired.email())
                        .active(true)
                        .categoryLabels(categoryLabelsForUpdate(action.desiredLabels(), remote))
                        .build())));
    }

    private SyncResult executeDeactivate(ExecutableAction action, boolean dryRun) {
        var remote = action.requireRemote();
        var mapping = action.plannedAction().requireRemoteMapping();
        String email = deactivatedEmail(mapping.employeeId());
        return executeSafely(action.plannedAction(), dryRun, () ->
                requireEverfiResponse(everfiUserClient.updateUser(EverfiUpdateUserCommand.builder()
                        .uuid(mapping.everfiUuid())
                        .employeeId(mapping.employeeId())
                        .firstName(remote.remoteFirstName())
                        .lastName(remote.remoteLastName())
                        .email(email)
                        .active(false)
                        .categoryLabels(remote.categoryLabels())
                        .build())));
    }

    private List<EverfiCategoryLabel> categoryLabelsForUpdate(List<EverfiCategoryLabel> desiredLabels,
                                                              RemoteUser remote) {
        // Desired labels replace any remote labels in the same category.
        // All other remote labels (Upload List, admin-added, external-system labels) are preserved.
        Set<String> managedCategoryNames = desiredLabels.stream()
                .map(EverfiCategoryLabel::getCategoryName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<EverfiCategoryLabel> labels = new ArrayList<>(desiredLabels);
        for (EverfiCategoryLabel remoteLabel : remote.categoryLabels()) {
            if (!managedCategoryNames.contains(remoteLabel.getCategoryName())) {
                labels.add(remoteLabel);
            }
        }
        return labels;
    }

    private String resolveUpdateEmail(DesiredUser desired, RemoteUser remote) {
        return remote.hasPersonalEmailOverride() ? remote.remoteEmail() : desired.email();
    }

    private String deactivatedEmail(int employeeId) {
        return "deactivated-" + employeeId + DEACTIVATED_EMAIL_DOMAIN;
    }

    private SyncResult executeSafely(PlannedAction action, boolean dryRun, SyncWrite syncWrite) {
        try {
            if (!dryRun) {
                syncWrite.run();
            }
            return SyncResult.success(action);
        } catch (IOException | IllegalArgumentException | NullPointerException | DataAccessException e) {
            return SyncResult.error(action, e.getMessage());
        }
    }

    private EverfiUser requireEverfiResponse(@Nullable EverfiUser everfiUser) {
        Assert.notNull(everfiUser, "Everfi API returned null user");
        return everfiUser;
    }

    private String requireEverfiUuid(@Nullable EverfiUser everfiUser) {
        String uuid = requireEverfiResponse(everfiUser).getUuid();
        Assert.hasText(uuid, "Everfi API returned user without uuid");
        return uuid;
    }

    @FunctionalInterface
    private interface SyncWrite {
        void run() throws IOException;
    }
}
