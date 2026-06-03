package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategory;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryRules;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryService;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiManagedCategory;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Resolves all Everfi category labels needed for executable user writes.
 *
 * <p>The provisioner resolves every category label required by the planned actions:
 * <ul>
 *   <li><b>Desired labels</b> — labels from each desired user's target state.</li>
 *   <li><b>Upload List label</b> — when the run contains at least one CREATE action, today's
 *       date-stamped Upload List label is resolved so it can be attached to new users.</li>
 * </ul>
 *
 * <p>Missing labels are created in live mode and represented by dry-run in-memory labels in dry
 * runs. That keeps downstream execution/reporting aligned across modes while suppressing Everfi
 * writes when {@code dryRun} is true.
 */
@Service
class EverfiLabelProvisioner {

    private static final int DRY_RUN_LABEL_ID_BASE = -1;

    private final EverfiCategoryService categoryService;
    private final Clock clock;

    @Autowired
    EverfiLabelProvisioner(EverfiCategoryService categoryService) {
        this(categoryService, Clock.systemDefaultZone());
    }

    EverfiLabelProvisioner(EverfiCategoryService categoryService, Clock clock) {
        this.categoryService = Objects.requireNonNull(categoryService);
        this.clock = Objects.requireNonNull(clock);
    }

    /**
     * Resolves all labels needed by the executable actions.
     * Dry runs still resolve the labels implied by the plan, but missing labels are represented
     * with dry-run in-memory placeholders instead of being created in Everfi.
     */
    ResolvedLabels resolve(LabelRequirements requirements, boolean dryRun) {
        DryRunLabelIds dryRunLabelIds = new DryRunLabelIds();
        Map<DesiredLabel, EverfiCategoryLabel> resolvedLabels =
                resolveDesiredLabels(requirements, dryRun, dryRunLabelIds);
        EverfiCategoryLabel uploadListLabel = resolveUploadListLabel(requirements, dryRun, dryRunLabelIds);
        return new ResolvedLabels(resolvedLabels, uploadListLabel);
    }

    private Map<DesiredLabel, EverfiCategoryLabel> resolveDesiredLabels(LabelRequirements requirements,
                                                                        boolean dryRun,
                                                                        DryRunLabelIds dryRunLabelIds) {
        Map<DesiredLabel, EverfiCategoryLabel> resolved = new LinkedHashMap<>();
        for (DesiredLabel desiredLabel : requirements.desiredLabels()) {
            try {
                EverfiCategory category = requireCategory(
                        desiredLabel.categoryName(),
                        "Everfi category not found for desired label: " + desiredLabel.categoryName()
                );
                EverfiCategoryLabel label = resolveOrProvisionLabel(
                        category,
                        desiredLabel.labelName(),
                        dryRun,
                        dryRunLabelIds,
                        everfiCategory -> categoryService.createLabel(everfiCategory, desiredLabel.labelName())
                );
                resolved.put(desiredLabel, label);
            } catch (IOException e) {
                throw new IllegalStateException(
                        "Failed to create Everfi label '" + desiredLabel.labelName() + "': " + e.getMessage(), e);
            }
        }
        return resolved;
    }

    private @Nullable EverfiCategoryLabel resolveUploadListLabel(LabelRequirements requirements,
                                                                 boolean dryRun,
                                                                 DryRunLabelIds dryRunLabelIds) {
        if (!requirements.uploadListRequired()) return null;

        LocalDate today = LocalDate.now(clock);
        String labelName = EverfiCategoryRules.uploadListLabelName(today);
        try {
            EverfiCategory category = requireCategory(
                    EverfiManagedCategory.UPLOAD_LIST.categoryName(),
                    "\"Upload List\" category not found in Everfi."
            );
            return resolveOrProvisionLabel(
                    category,
                    labelName,
                    dryRun,
                    dryRunLabelIds,
                    uploadListCategory -> categoryService.createUploadListLabel(uploadListCategory, today)
            );
        } catch (IOException | RuntimeException ex) {
            throw new EverfiUserSyncLoadException("Failed to ensure today's Upload List label.", ex);
        }
    }

    private EverfiCategoryLabel resolveOrProvisionLabel(EverfiCategory category,
                                                        String labelName,
                                                        boolean dryRun,
                                                        DryRunLabelIds dryRunLabelIds,
                                                        LabelCreator creator) throws IOException {
        EverfiCategoryLabel existing = categoryService.getCategoryLabel(category, labelName);
        if (existing != null) {
            return existing;
        }
        if (dryRun) {
            return dryRunLabel(category, labelName, dryRunLabelIds.next());
        }
        return requireCreatedLabel(creator.create(category), labelName);
    }

    private EverfiCategory requireCategory(String categoryName, String missingCategoryMessage) {
        EverfiCategory category = categoryService.getCategory(categoryName);
        if (category == null) {
            throw new IllegalStateException(missingCategoryMessage);
        }
        return category;
    }

    private EverfiCategoryLabel dryRunLabel(EverfiCategory category, String labelName, int labelId) {
        EverfiCategoryLabel label = new EverfiCategoryLabel(labelId, labelName);
        label.setCategoryId(category.getId());
        label.setCategoryName(category.getName());
        return label;
    }

    private EverfiCategoryLabel requireCreatedLabel(@Nullable EverfiCategoryLabel created, String labelName) {
        if (created == null || created.getLabelId() <= 0) {
            throw new IllegalStateException(
                    "Everfi returned a label with no ID after creating: " + labelName);
        }
        return created;
    }

    private static class DryRunLabelIds {
        private int next = DRY_RUN_LABEL_ID_BASE;

        int next() {
            return next--;
        }
    }

    @FunctionalInterface
    private interface LabelCreator {
        EverfiCategoryLabel create(EverfiCategory category) throws IOException;
    }
}
