package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import gov.nysenate.ess.core.model.pec.everfi.EverfiEmployeeMapping;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A snapshot of an Everfi user, optionally enriched with our local mapping. Identity confidence
 * (authoritative / candidate / unidentifiable) is not modeled on the record itself — it's derived
 * from the presence of {@code mapping} and {@code remoteEmployeeId} when the user is indexed by
 * {@link RemoteUserIndex}.
 */
@Builder
public record RemoteUser(
        @Nullable EverfiEmployeeMapping mapping,
        @NotNull String remoteUuid,
        Integer remoteEmployeeId,
        boolean remoteActive,
        @Nullable String remoteFirstName,
        @Nullable String remoteLastName,
        @NotNull String remoteEmail,
        List<EverfiCategoryLabel> categoryLabels
) {

    private static final String UPLOAD_LIST_CATEGORY = "Upload List";

    public RemoteUser {
        remoteUuid = Objects.requireNonNull(remoteUuid, "remoteUuid must not be null");
        remoteEmail = Objects.requireNonNull(remoteEmail, "remoteEmail must not be null");
        categoryLabels = categoryLabels != null ? List.copyOf(categoryLabels) : List.of();
    }

    /**
     * Returns the Upload List label assigned at provisioning time, if any. Used to identify the
     * cohort the user was created in.
     */
    public Optional<EverfiCategoryLabel> uploadListLabel() {
        return categoryLabels.stream()
                .filter(l -> UPLOAD_LIST_CATEGORY.equals(l.getCategoryName()))
                .findFirst();
    }

    /**
     * True when the remote email looks like a user-set personal address rather than the
     * SFMS-provided {@code @nysenate.gov} one. We treat that as an explicit override and preserve
     * it on UPDATE instead of overwriting with the desired email.
     */
    public boolean hasPersonalEmailOverride() {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(remoteEmail) && !StringUtils.containsIgnoreCase(remoteEmail, "@nysenate.gov");
    }

    /**
     * True when remote state diverges from desired and the difference is something the sync owns.
     * Drives the UPDATE-vs-SKIP decision in the planner.
     */
    public boolean isStale(DesiredUser desiredUser) {
        if (desiredUser == null) {
            return true;
        }
        return !Objects.equals(remoteEmployeeId, desiredUser.employeeId())
                || !remoteActive
                || !Objects.equals(remoteFirstName, desiredUser.firstName())
                || !Objects.equals(remoteLastName, desiredUser.lastName())
                || hasEmailDrift(desiredUser)
                || hasLabelDrift(desiredUser.categoryLabels());
    }

    private boolean hasEmailDrift(DesiredUser desiredUser) {
        return !hasPersonalEmailOverride() && !Objects.equals(remoteEmail, desiredUser.email());
    }

    /**
     * Sync manages a bounded set of categories on the desired user (currently Attend Live,
     * Department, and Role). A remote user is considered stale when any managed category does
     * not exactly match the desired set of labels for that category, including the presence of
     * extra stale labels such as an old Department value alongside the current one.
     *
     * Extra labels in categories not present on the desired user are treated as unmanaged and
     * do not make the user stale; those are preserved during update/reactivate writes.
     */
    private boolean hasLabelDrift(List<EverfiCategoryLabel> desiredLabels) {
        if (desiredLabels.isEmpty()) {
            return false;
        }

        Set<Integer> remoteLabelIds = categoryLabels.stream()
                .map(EverfiCategoryLabel::getLabelId)
                .collect(Collectors.toSet());
        boolean missingDesiredLabelWithoutCategory = desiredLabels.stream()
                .filter(label -> label.getCategoryName() == null)
                .anyMatch(label -> !remoteLabelIds.contains(label.getLabelId()));
        if (missingDesiredLabelWithoutCategory) {
            return true;
        }

        Map<String, Set<Integer>> desiredByCategory = labelsByCategory(desiredLabels);
        Map<String, Set<Integer>> remoteByCategory = labelsByCategory(categoryLabels);
        return desiredByCategory.entrySet().stream()
                .anyMatch(entry -> !entry.getValue().equals(remoteByCategory.getOrDefault(entry.getKey(), Set.of())));
    }

    private Map<String, Set<Integer>> labelsByCategory(List<EverfiCategoryLabel> labels) {
        return labels.stream()
                .filter(label -> label.getCategoryName() != null)
                .collect(Collectors.groupingBy(
                        EverfiCategoryLabel::getCategoryName,
                        Collectors.mapping(EverfiCategoryLabel::getLabelId, Collectors.toSet())
                ));
    }
}
