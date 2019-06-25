package gov.nysenate.ess.travel.delegate;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.EmployeeSearchView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DelegateView implements ViewObject {

    private static final DateTimeFormatter DATEPICKER_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    int id;
    EmployeeSearchView principal;
    EmployeeSearchView delegate;
    boolean useStartDate;
    String startDate;
    boolean useEndDate;
    String endDate;

    public DelegateView() {
    }

    public DelegateView(Delegate delegate) {
        this.id = delegate.id;
        this.principal = new EmployeeSearchView(delegate.principal);
        this.delegate = new EmployeeSearchView(delegate.delegate);
        this.useStartDate = true;
        this.startDate = delegate.startDate.format(DATEPICKER_FORMAT);
        this.useEndDate = true;
        this.endDate = delegate.endDate.format(DATEPICKER_FORMAT);
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

    @JsonProperty("useStartDate")
    public boolean isUseStartDate() {
        return useStartDate;
    }

    public String getStartDate() {
        return startDate;
    }

    @JsonProperty("useEndDate")
    public boolean isUseEndDate() {
        return useEndDate;
    }

    public String getEndDate() {
        return endDate;
    }

    @Override
    public String getViewType() {
        return "delegate";
    }
}
