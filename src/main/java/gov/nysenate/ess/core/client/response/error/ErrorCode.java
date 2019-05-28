package gov.nysenate.ess.core.client.response.error;

public enum ErrorCode
{
    /** Core Errors */
    APPLICATION_ERROR(1, "An error occurred while processing your request"),
    INVALID_ARGUMENTS(2, "The necessary arguments were not provided in the correct format."),
    MISSING_PARAMETERS(3, "The necessary parameters were not provided."),
    EMPLOYEE_NOT_FOUND(4, "The requested employee was not found"),
    INVALID_ALERT_INFO(5, "The submitted alert info contains invalid data"),
    EMPLOYEE_INACTIVE(6, "Attempt to take action on one or more employees that are inactive."),
    ACK_DOC_NOT_FOUND(7, "The requested acknowledged document was not found."),
    DUPLICATE_ACK(8, "The requested document has already been acknowledged"),
    INVALID_PARAM_TYPE(9, "One of the provided parameters could not be converted to the correct type"),
    PERSONNEL_ASSIGNED_TASK_NOT_FOUND(10, "The requested personnel assigned task could not be found."),
    INVALID_PEC_VIDEO_CODE(11, "The submitted video code(s) are incorrect."),

    /** Errors with Personnel Records */
    INVALID_RC_ERROR(50, "There is an issue with your responsibility center in your personnel records"),
    INVALID_RCH_ERROR(51, "There is an issue with your responsibility center head in your personnel records"),
    INVALID_WORK_LOC_ERROR(52, "There is an issue with your work location in your personnel records"),

    /** Time Errors */
    INVALID_TIME_RECORD(101, "The provided time record contained invalid data"),
    EMPLOYEE_NOT_SUPERVISOR(102, "The given employee is not a supervisor"),
    TIME_RECORD_NOT_FOUND(103, "The requested time record was not found"),
    CANNOT_CREATE_NEW_RECORD(104, "Time record creation is not allowed for given pay period"),

    /** Supply Errors */
    REQUISITION_UPDATE_CONFLICT(201, "The provided requisition was out of date."),
    SUPPLY_PERMISSON_DENIED(550, "Permission Denied"),

    /** Travel Errors */
    DATA_PROVIDER_ERROR(301, "An error occurred while communicating with 3rd party data providers."),
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
