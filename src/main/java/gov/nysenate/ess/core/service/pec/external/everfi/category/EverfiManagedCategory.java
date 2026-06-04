package gov.nysenate.ess.core.service.pec.external.everfi.category;

/**
 * Everfi categories whose labels are managed by ESS sync.
 */
public enum EverfiManagedCategory {
    UPLOAD_LIST("Upload List"),
    DEPARTMENT("Department"),
    ATTENDED_LIVE("Attended Live"),
    ROLE("Role");

    private final String categoryName;

    EverfiManagedCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public String categoryName() {
        return categoryName;
    }
}
