package gov.nysenate.ess.core.model.transaction;

/**
 * Simple enumeration of the different types of transactions.
 */
public enum TransactionType
{
    PER("Personnel"),
    PAY("Payroll");

    private final String desc;

    TransactionType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
