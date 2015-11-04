package gov.nysenate.ess.core.model.payroll;

/**
 * The different pay types used at the Senate are enumerated here.
 * Refer to the Senate's Time and Attendance Plan for more information about
 * the details for each pay type.
 */
public enum PayType
{
    RA("Regular Annual", 1820, true),
    SA("Special Annual", 0, true),
    SE("Session", 910, true),
    TE("Temporary", 0, false);

    private String desc;
    private int minHours;
    private boolean biweekly;

    private PayType(String desc, int minHours, boolean biweekly) {
        this.desc = desc;
        this.minHours = minHours;
        this.biweekly = biweekly;
    }

    public String getDesc() {
        return desc;
    }

    public int getMinHours() {
        return minHours;
    }

    public boolean isBiweekly() {
        return biweekly;
    }
}