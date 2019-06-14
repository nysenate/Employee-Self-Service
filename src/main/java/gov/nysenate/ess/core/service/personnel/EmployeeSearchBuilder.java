package gov.nysenate.ess.core.service.personnel;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a query to search for employees.
 *
 * Currently only contains parameters useful for PEC training search.
 * TODO: Add more parameters as needed.
 */
public class EmployeeSearchBuilder {

    private String name;
    private Boolean active;
    private Set<String> respCtrHeadCodes = new HashSet<>();

    private LocalDate continuousServiceFrom;
    private LocalDate continuousServiceTo;

    /* --- Builder Style Setters --- */

    public EmployeeSearchBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public EmployeeSearchBuilder setActive(Boolean active) {
        this.active = active;
        return this;
    }

    public EmployeeSearchBuilder setRespCtrHeadCodes(Set<String> respCtrHeadCodes) {
        this.respCtrHeadCodes = respCtrHeadCodes;
        return this;
    }

    public EmployeeSearchBuilder setContinuousServiceFrom(LocalDate continuousServiceFrom) {
        this.continuousServiceFrom = continuousServiceFrom;
        return this;
    }

    public EmployeeSearchBuilder setContinuousServiceTo(LocalDate continuousServiceTo) {
        this.continuousServiceTo = continuousServiceTo;
        return this;
    }

    /* --- Getters --- */

    public String getName() {
        return name;
    }

    public Boolean getActive() {
        return active;
    }

    public Set<String> getRespCtrHeadCodes() {
        return respCtrHeadCodes;
    }

    public LocalDate getContinuousServiceFrom() {
        return continuousServiceFrom;
    }

    public LocalDate getContinuousServiceTo() {
        return continuousServiceTo;
    }
}
