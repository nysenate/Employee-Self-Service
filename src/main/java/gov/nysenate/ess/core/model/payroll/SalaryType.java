package gov.nysenate.ess.core.model.payroll;

/**
 * The different salary types (Hourly, Biweekly, Yearly) used at the Senate are enumerated here.
 * Refer to the Senate's Time and Attendance Plan for more information about
 * the details for each pay type.
 */
public enum SalaryType
{
    HOURLY("Hourly Salary", "TE", 3600000L),
    BIWEEKLY("Biweekly Salary", "RA", 1209600000L),
    BIWEEKLYSA("Biweekly Salary", "SA", 1209600000L),
    YEARLY("Yearly Salary", "RA", 31536000000L),
    YEARLYSA("Yearly Salary", "SA", 31536000000L);

    private final String desc;
    private final String payType;
    private final long time;

    SalaryType(String desc, String payType, long time) {
        this.desc = desc;
        this.time = time;
        this.payType = payType;
    }

    /** --- Getters --- */

    public String getDesc() {
        return desc;
    }

    public String getPayType() {
        return payType;
    }

    public long getTime() {
        return time;
    }
}
