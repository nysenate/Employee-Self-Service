package gov.nysenate.ess.core.service.personnel;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
    private Boolean isSenator;

    /**
     * When true, {@link #name} is treated as a free-text term: it is tokenized and every token must
     * match the employee's full name or uid/email (order-insensitive), with results ranked by match
     * quality. When false (the default), name matching uses the legacy "last first mid" prefix match
     * relied on by the PEC training search.
     */
    private boolean freeTextNameMatch = false;

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

    public EmployeeSearchBuilder setIsSenator(Boolean isSenator) {
        this.isSenator = isSenator;
        return this;
    }

    public EmployeeSearchBuilder setRespCtrHeadCodes(Collection<String> respCtrHeadCodes) {
        this.respCtrHeadCodes = new HashSet<>(respCtrHeadCodes);
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

    public EmployeeSearchBuilder setFreeTextNameMatch(boolean freeTextNameMatch) {
        this.freeTextNameMatch = freeTextNameMatch;
        return this;
    }

    /* --- Getters --- */

    public String getName() {
        return name;
    }

    public Boolean getActive() {
        return active;
    }

    public Boolean getIsSenator() {
        return isSenator;
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

    public boolean isFreeTextNameMatch() {
        return freeTextNameMatch;
    }

    /**
     * Normalizes a free-text search term (uppercase, keep only letters/digits/spaces, collapse
     * whitespace) and splits it into tokens. Digits are retained so uid/email fragments still match.
     *
     * This is the single source of truth for how {@link #freeTextNameMatch} tokenizes a term: the DAO
     * uses it to build the query, and API layers use it to report which terms were matched (for result
     * highlighting) so the two never drift.
     */
    public static List<String> tokenizeSearchTerm(String term) {
        if (term == null) {
            return Collections.emptyList();
        }
        String normalized = term.trim().toUpperCase()
                .replaceAll("[^A-Z0-9 ]", "")
                .replaceAll(" +", " ")
                .trim();
        if (normalized.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(normalized.split(" "));
    }
}
