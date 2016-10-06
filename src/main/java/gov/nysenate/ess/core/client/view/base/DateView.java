package gov.nysenate.ess.core.client.view.base;

import java.time.LocalDate;

public class DateView implements ViewObject {

    private LocalDate date;

    public DateView(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String getViewType() {
        return "date";
    }
}
