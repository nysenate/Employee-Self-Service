package gov.nysenate.ess.core.model.alert;

public enum AlertInfoErrorCode {
    INVALID_EMP_ID("Invalid employee id"),
    NULL_CONTACT_OPTIONS("Null contact options"),
    INVALID_PHONE_NUMBER("Invalid phone number"),
    DUPLICATE_PHONE_NUMVER("Duplicate phone number"),
    INVALID_EMAIL("Invalid email"),
    DUPLICATE_EMAIL("Duplicate email"),
    ;

    private final String desc;

    AlertInfoErrorCode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
