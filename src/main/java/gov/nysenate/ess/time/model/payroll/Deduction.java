package gov.nysenate.ess.time.model.payroll;

import java.math.BigDecimal;

/**
 * Any deduction that can be applied to a pay check.
 * e.g. Federal taxes, Health insurance, etc.
 */
public class Deduction
{
    private int order;
    private String code;
    private String description;
    private BigDecimal amount;

    public Deduction(String code, int order, String description, BigDecimal amount) {
        this.order = order;
        this.code = code;
        this.description = description;
        this.amount = amount;
    }

    public int getOrder() {
        return order;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
