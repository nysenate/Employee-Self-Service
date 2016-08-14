package gov.nysenate.ess.core.client.response.error;

public enum ErrorCode
{
    /** Core Errors */
    APPLICATION_ERROR(1, "An error occurred while processing your request"),
    INVALID_ARGUMENTS(2, "The necessary arguments were not provided in the correct format."),
    MISSING_PARAMETERS(3, "The necessary parameters were not provided."),

    /** Time Errors */
    INVALID_TIME_RECORD(101, "The provided time record contained invalid data"),
    EMPLOYEE_NOT_SUPERVISOR(102, "The given employee is not a supervisor"),
    TIME_RECORD_NOT_FOUND(103, "The requested time record was not found"),

    /** Supply Errors */
    REQUISITION_UPDATE_CONFLICT(201, "The provided requisition was out of date."),
    ;

    /** Unique ID for error code */
    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
