package gov.nysenate.ess.travel.delegate;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.EmployeeSearchView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DelegationView implements ViewObject {

    private static final DateTimeFormatter DATEPICKER_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    int id;
    EmployeeSearchView principal;
    EmployeeSearchView delegate;
    @JsonProperty("useStartDate")
    boolean useStartDate;
    String startDate;
    @JsonProperty("useEndDate")
    boolean useEndDate;
    String endDate;

    @JsonProperty("isActive")
    boolean isActive;
    @JsonProperty("isScheduled")
    boolean isScheduled;
    @JsonProperty("isExpired")
    boolean isExpired;

    public DelegationView() {
    }

    public DelegationView(Delegation delegation) {
        this.id = delegation.id;
        this.principal = new EmployeeSearchView(delegation.principal);
        this.delegate = new EmployeeSearchView(delegation.delegate);
        this.useStartDate = true;
        this.startDate = delegation.startDate.format(DATEPICKER_FORMAT);
        this.useEndDate = true;
        this.endDate = delegation.endDate.format(DATEPICKER_FORMAT);

        this.isActive = delegation.isActive();
        this.isScheduled = delegation.isScheduled();
        this.isExpired = delegation.isExpired();
    }

    public LocalDate startDate() {
        return LocalDate.parse(startDate, DATEPICKER_FORMAT);
    }

    public LocalDate endDate() {
        return LocalDate.parse(endDate, DATEPICKER_FORMAT);
    }

    public int getId() {
        return id;
    }

    public EmployeeSearchView getPrincipal() {
        return principal;
    }

    public EmployeeSearchView getDelegate() {
        return delegate;
    }

    public boolean isUseStartDate() {
        return useStartDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public boolean isUseEndDate() {
        return useEndDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public boolean isExpired() {
        return isExpired;
    }

    @Override
    public String getViewType() {
        return "delegation";
    }
}
