package gov.nysenate.ess.core.model.personnel;

/**
 * Enumerates all possible codes for martial status
 */
public enum MaritalStatus
{
    SINGLE("S", "Single"),
    DIVORCED("D", "Divorced"),
    MARRIED("M", "Married"),
    WIDOWED("W", "Widowed");

    String code;
    String desc;

    MaritalStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MaritalStatus valueOfCode(String code) {
        if (code != null) {
            for (MaritalStatus status : MaritalStatus.values()) {
                if (status.code.equalsIgnoreCase(code)) {
                    return status;
                }
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
