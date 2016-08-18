package gov.nysenate.ess.core.client.view.base;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.DateUtils;

import java.time.LocalDate;

public class DateRangeView implements ViewObject {

    protected LocalDate beginDate;
    protected LocalDate endDate;

    public DateRangeView(Range<LocalDate> dateRange) {
        if (dateRange != null && !dateRange.isEmpty()) {
            this.beginDate = DateUtils.startOfDateRange(dateRange);
            this.endDate = DateUtils.endOfDateRange(dateRange);
        }
    }

    public LocalDate getBeginDate() {
        return beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public String getViewType() {
        return "date-range";
    }
}
