package gov.nysenate.ess.core.dao.base;

public enum DbVendor
{
    ORACLE_10g("10g", false),
    POSTGRES("9.3", true);

    String version;
    boolean supportsLimitOffset;

    DbVendor(String version, boolean supportsLimitOffset) {
        this.version = version;
        this.supportsLimitOffset = supportsLimitOffset;
    }

    public String getVersion() {
        return version;
    }

    public boolean supportsLimitOffset() {
        return supportsLimitOffset;
    }
}