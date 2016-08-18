package gov.nysenate.ess.core.client.view;

import com.google.common.collect.RangeSet;
import gov.nysenate.ess.core.client.view.base.DateRangeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeActiveDatesView implements ViewObject {

    protected Integer empId;
    protected List<DateRangeView> activeDates;

    public EmployeeActiveDatesView(int empId, RangeSet<LocalDate> activeDatesRangeSet) {
        this.empId = empId;
        if (activeDatesRangeSet != null) {
            this.activeDates = activeDatesRangeSet.asRanges().stream()
                    .map(DateRangeView::new)
                    .collect(Collectors.toList());
        }
    }

    public Integer getEmpId() {
        return empId;
    }

    public List<DateRangeView> getActiveDates() {
        return activeDates;
    }

    @Override
    public String getViewType() {
        return "employee-active-dates";
    }
}
