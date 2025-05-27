package gov.nysenate.ess.core.model.personnel;

/**
 * Gender enumeration
 */
public enum Gender
{
    M("Male"),
    F("Female");

    private final String desc;

    Gender(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
