package gov.nysenate.ess.travel.request.app;

import java.util.Arrays;

public enum TravelApplicationSortField {
    START_DATE("startDate", "start_date"),
    STATUS("status", "status"),
    SUBMITTED_DATE("submittedDate", "created_date_time"),
    ID("id", "app_id");

    private final String parameterName;
    private final String sqlColumn;

    TravelApplicationSortField(String parameterName, String sqlColumn) {
        this.parameterName = parameterName;
        this.sqlColumn = sqlColumn;
    }

    public String parameterName() {
        return parameterName;
    }

    public String sqlColumn() {
        return sqlColumn;
    }

    public static TravelApplicationSortField fromParameter(String value) {
        return Arrays.stream(values())
                .filter(field -> field.parameterName.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
