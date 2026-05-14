package gov.nysenate.ess.core.service.pec.external.everfi.category;

import gov.nysenate.ess.core.model.personnel.Employee;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable point-in-time view of Everfi categories and their labels.
 * <p>
 * Snapshots are produced by {@link EverfiCategoryService#fetchSnapshot()} and passed explicitly
 * through any code path that needs to look up categories or labels. Holding a snapshot for the
 * duration of a process (e.g. a single sync run) avoids both repeated remote fetches and the
 * staleness hazards of long-lived shared caches.
 */
public final class EverfiCategorySnapshot {

    public static final String UPLOAD_LIST = "Upload List";
    public static final String DEPARTMENT = "Department";
    public static final String ATTENDED_LIVE = "Attended Live";
    public static final String ROLE = "Role";

    public static final DateTimeFormatter UPLOAD_LIST_LABEL_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");

    private final List<EverfiCategory> categories;

    public EverfiCategorySnapshot(List<EverfiCategory> categories) {
        this.categories = Collections.unmodifiableList(new ArrayList<>(categories));
    }

    public List<EverfiCategory> getCategories() {
        return categories;
    }

    public EverfiCategory getCategory(String name) {
        for (EverfiCategory category : categories) {
            if (category.getName().equals(name)) {
                return category;
            }
        }
        return null;
    }

    public EverfiCategoryLabel getCategoryLabel(EverfiCategory category, String labelName) {
        for (EverfiCategoryLabel label : category.getLabels()) {
            if (label.getLabelName().equals(labelName)) {
                return label;
            }
        }
        return null;
    }

    public EverfiCategoryLabel getCategoryLabel(String categoryName, String labelName) {
        EverfiCategory cat = getCategory(categoryName);
        return cat == null ? null : getCategoryLabel(cat, labelName);
    }

    public EverfiCategoryLabel findLabel(int labelId) {
        for (EverfiCategory category : categories) {
            for (EverfiCategoryLabel label : category.getLabels()) {
                if (label.getLabelId() == labelId) {
                    return label;
                }
            }
        }
        return null;
    }

    public EverfiCategoryLabel getUploadListLabel(LocalDate date) {
        return getCategoryLabel(UPLOAD_LIST, date.format(UPLOAD_LIST_LABEL_FORMAT));
    }

    /**
     * Find the "Upload List" label in a list of category labels.
     *
     * @param labels the labels to search; typically the hydrated labels on an Everfi user
     * @return the matching label, or null if none of the labels belong to the Upload List category
     */
    public EverfiCategoryLabel findUploadListLabel(List<EverfiCategoryLabel> labels) {
        if (labels == null) {
            return null;
        }
        for (EverfiCategoryLabel label : labels) {
            if (UPLOAD_LIST.equals(label.getCategoryName())) {
                return label;
            }
        }
        return null;
    }

    /**
     * Get the given employee's CategoryLabel for the "Attended Live" Category.
     * Currently, this is always "No".
     */
    public EverfiCategoryLabel getAttendLiveLabel(Employee emp) {
        return getCategoryLabel(ATTENDED_LIVE, "No");
    }

    /**
     * Get the given employee's CategoryLabel for the "Department" Category.
     */
    public EverfiCategoryLabel getDepartmentLabel(Employee emp) {
        return getCategoryLabel(DEPARTMENT, emp.getRespCenterHeadCode());
    }

    /**
     * Get the given employee's CategoryLabel for the "Role" Category.
     */
    public EverfiCategoryLabel getRoleLabel(Employee emp) {
        return emp.isSenator()
                ? getCategoryLabel(ROLE, "Senator")
                : getCategoryLabel(ROLE, "Employee");
    }

    /**
     * Replaces sparse label references (typically those parsed from an Everfi user response,
     * which carry only the label id) with fully populated labels by joining against this snapshot.
     * Labels whose ids are not present in the snapshot are dropped.
     *
     * @return the hydrated labels, or null if no input label could be hydrated
     */
    public List<EverfiCategoryLabel> hydrateLabels(List<EverfiCategoryLabel> sparseLabels) {
        if (sparseLabels == null) {
            return null;
        }
        List<EverfiCategoryLabel> hydrated = new ArrayList<>();
        for (EverfiCategoryLabel sparseLabel : sparseLabels) {
            EverfiCategoryLabel cachedLabel = findLabel(sparseLabel.getLabelId());
            if (cachedLabel != null) {
                sparseLabel.setCategoryId(cachedLabel.getCategoryId());
                sparseLabel.setCategoryName(cachedLabel.getCategoryName());
                sparseLabel.setLabelName(cachedLabel.getLabelName());
                hydrated.add(sparseLabel);
            }
        }
        return hydrated.isEmpty() ? null : hydrated;
    }
}
