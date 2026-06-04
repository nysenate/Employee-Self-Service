package gov.nysenate.ess.core.service.pec.external.everfi.category;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Pure normalization and naming rules for ESS-managed Everfi categories.
 */
public final class EverfiCategoryRules {

    private static final DateTimeFormatter UPLOAD_LIST_LABEL_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");

    private EverfiCategoryRules() {
    }

    public static String normalizeLabel(EverfiManagedCategory category, String rawLabelValue) {
        return switch (category) {
            case UPLOAD_LIST -> throw new IllegalArgumentException(
                    "Upload List labels are date-derived and cannot be normalized from raw text.");
            case DEPARTMENT -> trimToNull(rawLabelValue);
            case ATTENDED_LIVE -> normalizeStrictEnumValue(category, rawLabelValue, "Yes", "No");
            case ROLE -> normalizeStrictEnumValue(category, rawLabelValue, "Senator", "Employee");
        };
    }

    public static String uploadListLabelName(LocalDate date) {
        return date.format(UPLOAD_LIST_LABEL_FORMAT);
    }

    private static String normalizeStrictEnumValue(EverfiManagedCategory category, String rawLabelValue,
                                                   String firstAllowed, String secondAllowed) {
        String normalized = trimToNull(rawLabelValue);
        if (normalized == null) {
            throw new IllegalArgumentException(
                    "Everfi label value is required for category: " + category.categoryName());
        }
        if (firstAllowed.equalsIgnoreCase(normalized)) {
            return firstAllowed;
        }
        if (secondAllowed.equalsIgnoreCase(normalized)) {
            return secondAllowed;
        }
        throw new IllegalArgumentException(
                "Invalid Everfi label value '" + rawLabelValue + "' for category: " + category.categoryName());
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
