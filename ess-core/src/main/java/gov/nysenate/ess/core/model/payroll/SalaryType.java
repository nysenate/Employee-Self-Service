package gov.nysenate.ess.core.model.payroll;

/**
 * The different salary types (Hourly, Biweekly, Yearly) used at the Senate are enumerated here.
 * Refer to the Senate's Time and Attendance Plan for more information about
 * the details for each pay type.
 */
public enum SalaryType
{
    HOURLY("Hourly Salary", "TE", 3600000l),
    BIWEEKLY("Biweekly Salary", "RA", 1209600000l),
    BIWEEKLYSA("Biweekly Salary", "SA", 1209600000l),
    YEARLY("Yearly Salary", "RA", 31536000000l),
    YEARLYSA("Yearly Salary", "SA", 31536000000l);

    private String desc;
    private String payType;
    private long time;

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
