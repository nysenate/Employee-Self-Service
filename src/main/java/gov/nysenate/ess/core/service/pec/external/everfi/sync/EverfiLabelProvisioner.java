package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategory;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryRules;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryService;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiManagedCategory;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

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
 * <p>Missing labels are created in live mode and represented by transient in-memory labels in dry
 * runs. That keeps downstream execution/reporting aligned across modes while suppressing Everfi
 * writes when {@code dryRun} is true.
 */
@Service
class EverfiLabelProvisioner {

    private static final int DRY_RUN_LABEL_ID_BASE = -1;

    private final EverfiCategoryService categoryService;

    EverfiLabelProvisioner(EverfiCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Resolves all labels needed by the executable actions.
     * Dry runs still resolve the labels implied by the plan, but missing labels are represented
     * with transient in-memory placeholders instead of being created in Everfi.
     */
    ResolvedLabels resolve(LabelRequirements requirements, boolean dryRun) {
        TransientLabelIds transientLabelIds = new TransientLabelIds();
        Map<DesiredLabel, EverfiCategoryLabel> resolvedLabels =
                resolveDesiredLabels(requirements, dryRun, transientLabelIds);
        EverfiCategoryLabel uploadListLabel = resolveUploadListLabel(requirements, dryRun, transientLabelIds);
        return new ResolvedLabels(resolvedLabels, uploadListLabel);
    }

    private Map<DesiredLabel, EverfiCategoryLabel> resolveDesiredLabels(LabelRequirements requirements,
                                                                        boolean dryRun,
                                                                        TransientLabelIds transientLabelIds) {
        Map<DesiredLabel, EverfiCategoryLabel> resolved = new LinkedHashMap<>();
        for (DesiredLabel desiredLabel : requirements.desiredLabels()) {
            try {
                resolved.put(desiredLabel, resolveRequiredLabel(
                        desiredLabel.categoryName(),
                        desiredLabel.labelName(),
                        dryRun,
                        transientLabelIds,
                        category -> categoryService.createLabel(category, desiredLabel.labelName())
                ));
            } catch (IOException e) {
                throw new IllegalStateException(
                        "Failed to create Everfi label '" + desiredLabel.labelName() + "': " + e.getMessage(), e);
            }
        }
        return resolved;
    }

    private @Nullable EverfiCategoryLabel resolveUploadListLabel(LabelRequirements requirements,
                                                                 boolean dryRun,
                                                                 TransientLabelIds transientLabelIds) {
        if (!requirements.uploadListRequired()) return null;

        LocalDate today = LocalDate.now();
        String labelName = EverfiCategoryRules.uploadListLabelName(today);
        try {
            EverfiCategory category = requireUploadListCategory();
            return resolveRequiredLabel(
                    labelName,
                    category,
                    dryRun,
                    transientLabelIds,
                    uploadListCategory -> categoryService.createUploadListLabel(uploadListCategory, today)
            );
        } catch (IOException | RuntimeException ex) {
            throw new EverfiUserSyncLoadException("Failed to ensure today's Upload List label.", ex);
        }
    }

    private EverfiCategoryLabel resolveRequiredLabel(String categoryName,
                                                     String labelName,
                                                     boolean dryRun,
                                                     TransientLabelIds transientLabelIds,
                                                     LabelCreator creator) throws IOException {
        EverfiCategory category = requireCategory(categoryName);
        return resolveRequiredLabel(labelName, category, dryRun, transientLabelIds, creator);
    }

    private EverfiCategoryLabel resolveRequiredLabel(String labelName,
                                                     EverfiCategory category,
                                                     boolean dryRun,
                                                     TransientLabelIds transientLabelIds,
                                                     LabelCreator creator) throws IOException {
        EverfiCategoryLabel existing = categoryService.getCategoryLabel(category, labelName);
        if (existing != null) {
            return existing;
        }
        if (dryRun) {
            return transientLabel(category, labelName, transientLabelIds.next());
        }
        return requireCreatedLabel(creator.create(category), labelName);
    }

    private EverfiCategory requireCategory(String categoryName) {
        EverfiCategory category = categoryService.getCategory(categoryName);
        if (category == null) {
            throw new IllegalArgumentException("Everfi category not found for desired label: " + categoryName);
        }
        return category;
    }

    private EverfiCategory requireUploadListCategory() {
        EverfiCategory category = categoryService.getCategory(EverfiManagedCategory.UPLOAD_LIST.categoryName());
        if (category == null) {
            throw new IllegalStateException("\"Upload List\" category not found in Everfi.");
        }
        return category;
    }

    private EverfiCategoryLabel transientLabel(EverfiCategory category, String labelName, int labelId) {
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

    private static class TransientLabelIds {
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
