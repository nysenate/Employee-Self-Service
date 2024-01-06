package gov.nysenate.ess.core.model.personnel;

import java.util.Objects;

/**
 * Represents a Responsibility Center Head. Various responsibility centers can
 * share a common organizational head.
 * @see ResponsibilityCenter
 */
public class ResponsibilityHead
{
    protected boolean active;
    protected String code;
    protected String shortName;
    protected String name;
    protected String affiliateCode;

    public ResponsibilityHead() {}

    public ResponsibilityHead(ResponsibilityHead other) {
        this.active = other.active;
        this.code = other.code;
        this.shortName = other.shortName;
        this.name = other.name;
        this.affiliateCode = other.affiliateCode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getAffiliateCode() {
        return affiliateCode;
    }

    public void setAffiliateCode(String affiliateCode) {
        this.affiliateCode = affiliateCode;
    }

    @Override
    public String toString() {
        return "ResponsibilityHead{" +
               "active=" + active +
               ", code='" + code + '\'' +
               ", shortName='" + shortName + '\'' +
               ", name='" + name + '\'' +
               ", affiliateCode='" + affiliateCode + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResponsibilityHead that = (ResponsibilityHead) o;
        return active == that.active && Objects.equals(code, that.code) &&
                Objects.equals(shortName, that.shortName) && Objects.equals(name, that.name) &&
                Objects.equals(affiliateCode, that.affiliateCode);

    }

    @Override
    public int hashCode() {
        int result = (active ? 1 : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (shortName != null ? shortName.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (affiliateCode != null ? affiliateCode.hashCode() : 0);
        return result;
    }
}
