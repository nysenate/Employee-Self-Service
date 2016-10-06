package gov.nysenate.ess.time.client.view;

import gov.nysenate.ess.core.client.view.base.InvalidParameterView;
import gov.nysenate.ess.time.model.attendance.TimeEntry;

import java.time.LocalDate;
import java.util.Objects;

public class InvalidTimeEntryParameterView extends InvalidParameterView {

    private LocalDate entryDate;
//    private String entryId;

    public InvalidTimeEntryParameterView(TimeEntry entry, String paramName, String paramType,
                                         String constraint, Object receivedValue) {
        super(paramName, paramType, constraint, receivedValue);
        if (entry != null) {
            this.entryDate = entry.getDate();
//            this.entryId = Objects.toString(entry.getEntryId());
        }
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }
//
//    public String getEntryId() {
//        return entryId;
//    }

    @Override
    public String getViewType() {
        return "invalid-entry-parameter";
    }
}
