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
     * Executes every action in order and returns a result per action. {@code uploadListLabel} is only
     * needed when at least one CREATE will run live; pass null otherwise.
     */
    List<SyncResult> executeAll(List<PlannedAction> actions,
                                       @Nullable EverfiCategoryLabel uploadListLabel,
                                       boolean dryRun) {
        List<SyncResult> results = new ArrayList<>();
        for (PlannedAction action : actions) {
            results.add(switch (action.action()) {
                case CREATE     -> executeCreate(action, uploadListLabel, dryRun);
                case UPDATE     -> executeUpdate(action, dryRun);
                case REACTIVATE -> executeReactivate(action, dryRun);
                case DEACTIVATE -> executeDeactivate(action, dryRun);
                case SKIP       -> SyncResult.skipped(action);
                case FLAG       -> SyncResult.flagged(action);
            });
        }
        return results;
    }

    private SyncResult executeCreate(PlannedAction action,
                                     @Nullable EverfiCategoryLabel uploadListLabel,
                                     boolean dryRun) {
        var desired = action.requireDesired();
        return executeSafely(action, dryRun, () -> {
            EverfiUser everfiUser = everfiUserClient.addUser(new EverfiAddUserCommand(
                    desired.employeeId(),
                    desired.firstName(),
                    desired.lastName(),
                    desired.email(),
                    categoryLabelsForCreate(desired.categoryLabels(), uploadListLabel)
            ));
            everfiEmployeeMappingDao.insert(new EverfiEmployeeMapping(
                    desired.employeeId(),
                    requireEverfiUuid(everfiUser)
            ));
        });
    }

    private SyncResult executeUpdate(PlannedAction action, boolean dryRun) {
        var desired = action.requireDesired();
        var remote = action.requireRemote();
        var mapping = action.requireRemoteMapping();
        return executeSafely(action, dryRun, () ->
                requireEverfiResponse(everfiUserClient.updateUser(EverfiUpdateUserCommand.builder()
                        .uuid(mapping.everfiUuid())
                        .employeeId(mapping.employeeId())
                        .firstName(desired.firstName())
                        .lastName(desired.lastName())
                        .email(resolveUpdateEmail(desired, remote))
                        .active(true)
                        .categoryLabels(categoryLabelsForUpdate(desired.categoryLabels(), remote))
                        .build())));
    }

    private SyncResult executeReactivate(PlannedAction action, boolean dryRun) {
        var desired = action.requireDesired();
        var remote = action.requireRemote();
        var mapping = action.requireRemoteMapping();
        if (desired.email() == null) {
            return SyncResult.error(action, "Cannot reactivate user without email");
        }
        return executeSafely(action, dryRun, () ->
                requireEverfiResponse(everfiUserClient.updateUser(EverfiUpdateUserCommand.builder()
                        .uuid(mapping.everfiUuid())
                        .employeeId(mapping.employeeId())
                        .firstName(desired.firstName())
                        .lastName(desired.lastName())
                        .email(desired.email())
                        .active(true)
                        .categoryLabels(categoryLabelsForUpdate(desired.categoryLabels(), remote))
                        .build())));
    }

    private SyncResult executeDeactivate(PlannedAction action, boolean dryRun) {
        var remote = action.requireRemote();
        var mapping = action.requireRemoteMapping();
        String email = deactivatedEmail(mapping.employeeId());
        return executeSafely(action, dryRun, () ->
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

    private List<EverfiCategoryLabel> categoryLabelsForCreate(List<EverfiCategoryLabel> desiredLabels,
                                                               @Nullable EverfiCategoryLabel uploadListLabel) {
        List<EverfiCategoryLabel> labels = new ArrayList<>(desiredLabels);
        if (uploadListLabel != null) {
            labels.add(uploadListLabel);
        }
        return labels;
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
