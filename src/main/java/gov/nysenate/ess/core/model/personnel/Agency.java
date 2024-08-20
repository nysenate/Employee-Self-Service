package gov.nysenate.ess.core.model.personnel;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agency agency = (Agency) o;
        return active == agency.active && Objects.equals(code, agency.code) && Objects.equals(shortName, agency.shortName) && Objects.equals(name, agency.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, active, shortName, name);
    }
}