package gov.nysenate.ess.core.model.period;

/**
 * Maps the various pay period codes used by the Senate. For time and attendance
 * purposes, the Attendance Fiscal Period (AF) type is primarily used.
 */
public enum PayPeriodType
{
    AF("AF", "Attendance Fiscal Period"),
    PF("PF", "Payroll's Fiscal Period"),
    PA("PA", "Payroll Allotment Period"),
    SF("SF", "Salary Ledger Fiscal Period"),
    FF("FF", "Fiscal's Fiscal Period"),
    SE("SE", "Session Duration Period"),
    FC("FC", "Fiscal's Calendar Period"),
    PC("PC", "Payroll's Calendar Period");

    String code;
    String desc;

    PayPeriodType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
