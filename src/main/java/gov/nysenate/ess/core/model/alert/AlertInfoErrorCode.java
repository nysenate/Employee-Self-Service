package gov.nysenate.ess.core.model.alert;

public enum AlertInfoErrorCode {
    INVALID_EMP_ID("Invalid employee id"),
    NULL_MOBILE_CONTACT_OPTIONS("Null mobile contact options"),
    INVALID_PHONE_NUMBER("Invalid phone number"),
    DUPLICATE_PHONE_NUMVER("Duplicate phone number"),
    INVALID_EMAIL("Invalid email"),
    DUPLICATE_EMAIL("Duplicate email"),
    ;

    private String desc;

    AlertInfoErrorCode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
