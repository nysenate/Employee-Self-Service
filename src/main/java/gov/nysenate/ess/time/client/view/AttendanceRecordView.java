package gov.nysenate.ess.time.client.view;

import gov.nysenate.ess.time.model.attendance.AttendanceRecord;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@XmlRootElement
public class AttendanceRecordView extends AttendanceHoursView {

    protected Integer employeeId;
    protected boolean active;
    protected LocalDate beginDate;
    protected LocalDate endDate;
    protected Integer year;
    protected Integer payPeriodNum;
    protected LocalDateTime postDate;
    protected LocalDateTime originalDate;
    protected LocalDateTime updateDate;
    protected String transactionNote;
    protected List<String> timesheetIds;
    protected boolean paperTimesheet;
    protected Integer expectedDays;

    public AttendanceRecordView(AttendanceRecord record) {
        super(record);
        if (record != null) {
            this.employeeId = record.getEmployeeId();
            this.active = record.isActive();
            this.beginDate = record.getBeginDate();
            this.endDate = record.getEndDate();
            this.year = Optional.ofNullable(record.getYear())
                    .map(Year::getValue).orElse(null);
            this.payPeriodNum = record.getPayPeriodNum();
            this.postDate = record.getPostDate().orElse(null);
            this.originalDate = record.getCreatedDate();
            this.updateDate = record.getUpdatedDate();
            this.transactionNote = record.getTransactionNote();
            this.timesheetIds = record.getTimesheetIds().stream()
                    .map(BigInteger::toString)
                    .collect(Collectors.toList());
            this.paperTimesheet = record.isPaperTimesheet();
            this.expectedDays = record.getExpectedDays();
        }
    }

    @XmlElement
    public Integer getEmployeeId() {
        return employeeId;
    }

    @XmlElement
    public boolean isActive() {
        return active;
    }

    @XmlElement
    public LocalDate getBeginDate() {
        return beginDate;
    }

    @XmlElement
    public LocalDate getEndDate() {
        return endDate;
    }

    @XmlElement
    public Integer getYear() {
        return year;
    }

    @XmlElement
    public Integer getPayPeriodNum() {
        return payPeriodNum;
    }

    @XmlElement
    public LocalDateTime getPostDate() {
        return postDate;
    }

    @XmlElement
    public LocalDateTime getOriginalDate() {
        return originalDate;
    }

    @XmlElement
    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    @XmlElement
    public String getTransactionNote() {
        return transactionNote;
    }

    @XmlElement
    public List<String> getTimesheetIds() {
        return timesheetIds;
    }

    @XmlElement
    public boolean isPaperTimesheet() {
        return paperTimesheet;
    }
    @XmlElement
    public Integer getExpectedDays() {
        return expectedDays;
    }

    @Override
    public String getViewType() {
        return "attendance-record";
    }
}
