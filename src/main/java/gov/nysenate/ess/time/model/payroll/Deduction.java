package gov.nysenate.ess.time.model.payroll;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Any deduction that can be applied to a pay check.
 * e.g. Federal taxes, Health insurance, etc.
 */
public class Deduction implements Comparable<Deduction>
{
    private Integer order;
    private Integer code;
    private String description;
    private BigDecimal amount;

    public Deduction(String code, int order, String description, BigDecimal amount) {
        this(Integer.valueOf(code), order, description, amount);
    }

    public Deduction(Integer code, Integer order, String description, BigDecimal amount) {
        this.order = order;
        this.code = code;
        this.description = description;
        this.amount = amount;
    }

    public Integer getOrder() {
        return order;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Deduction{" +
                "order=" + order +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deduction deduction = (Deduction) o;
        return Objects.equals(order, deduction.order) && Objects.equals(code, deduction.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, code);
    }

    @Override
    public int compareTo(Deduction o) {
        int order = this.getOrder().compareTo(o.getOrder());
        return order == 0 ? this.getCode().compareTo(o.getCode()) : order;
    }
}
