package gov.nysenate.ess.web.model.payroll;

import java.math.BigDecimal;

/**
 * Any deduction that can be applied to a pay check.
 * e.g. Federal taxes, Health insurance, etc.
 */
public class Deduction
{
    private String code;
    private String description;
    private BigDecimal amount;

    public Deduction(String code, String description, BigDecimal amount) {
        this.code = code;
        this.description = description;
        this.amount = amount;
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
