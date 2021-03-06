package gov.nysenate.ess.core.model.personnel;

/**
 * Represents a senate agency.
 */
public class Agency
{
    public static final String SENATOR_AGENCY_CODE = "04210";

    protected String code;
    protected boolean active;
    protected String shortName;
    protected String name;

    public Agency() {}

    public Agency(Agency other) {
        this.code = other.code;
        this.active = other.active;
        this.shortName = other.shortName;
        this.name = other.name;
    }

    /* --- Functional Getters --- */

    public boolean isSenator() {
        return SENATOR_AGENCY_CODE.equals(code);
    }

    /* --- Getters / Setters --- */

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}