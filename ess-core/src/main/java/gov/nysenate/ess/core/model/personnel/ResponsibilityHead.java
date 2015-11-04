package gov.nysenate.ess.core.model.personnel;

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
}
