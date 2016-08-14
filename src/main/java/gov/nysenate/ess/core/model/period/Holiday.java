package gov.nysenate.ess.core.model.period;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A simple model for storing holiday details.
 */
public class Holiday
{
    protected LocalDate date;
    protected String name;
    protected boolean active;
    protected BigDecimal hours;

    /** Questionable holidays are declared at the discretion of the senate. */
    protected boolean questionable;

    public Holiday() {}

    /** --- Basic Getters/Setters --- */

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isQuestionable() {
        return questionable;
    }

    public void setQuestionable(boolean questionable) {
        this.questionable = questionable;
    }

    public BigDecimal getHours() {
        return hours;
    }

    public void setHours(BigDecimal hours) {
        this.hours = hours;
    }
}
