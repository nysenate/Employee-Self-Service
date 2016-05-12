package gov.nysenate.ess.seta.client.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.seta.model.attendance.TimeRecord;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement(name = "timeRecord")
public class TimeRecordView extends SimpleTimeRecordView
{
    protected EmployeeView employee;
    protected EmployeeView supervisor;
    protected List<TimeEntryView> timeEntries;

    protected TimeRecordView() {}

    public TimeRecordView(TimeRecord record, Employee emp, Employee sup) {
        super(record);
        if (record != null) {
            this.employee = new EmployeeView(emp);
            this.supervisor = new EmployeeView(sup);
            this.timeEntries = record.getTimeEntries().stream()
                .map(TimeEntryView::new)
                .collect(Collectors.toList());
        }
    }

    @JsonIgnore
    public TimeRecord toTimeRecord() {
        TimeRecord record = new TimeRecord();
        record.addTimeEntries(timeEntries.stream()
            .map(TimeEntryView::toTimeEntry)
            .collect(Collectors.toList()));
        return record;
    }

    @XmlElement
    public EmployeeView getEmployee() {
        return employee;
    }

    @XmlElement
    public List<TimeEntryView> getTimeEntries() {
        return timeEntries;
    }

    @XmlElement
    public EmployeeView getSupervisor() {
        return supervisor;
    }

    @Override
    @XmlElement
    public String getViewType() {
        return "time record";
    }
}
