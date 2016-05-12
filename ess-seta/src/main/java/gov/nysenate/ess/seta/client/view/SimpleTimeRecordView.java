package gov.nysenate.ess.seta.client.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.ess.core.client.view.PayPeriodView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.seta.model.attendance.TimeRecord;
import gov.nysenate.ess.seta.model.attendance.TimeRecordStatus;

import javax.xml.bind.annotation.XmlElement;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A simplified time record view that provides only time record metadata,
 * time entries and detailed employee and supervisor data is excluded
 */
public class SimpleTimeRecordView implements ViewObject {

    protected String timeRecordId;
    protected Integer employeeId;
    protected Integer supervisorId;
    protected String scope;
    protected String lastUpdater;
    protected String respHeadCode;
    protected boolean active;
    protected PayPeriodView payPeriod;
    protected LocalDate beginDate;
    protected LocalDate endDate;
    protected String remarks;
    protected String exceptionDetails;
    protected LocalDate processedDate;
    protected String recordStatus;
    protected String originalUserId;
    protected String updateUserId;
    protected LocalDateTime originalDate;
    protected LocalDateTime updateDate;

    protected SimpleTimeRecordView() {}

    public SimpleTimeRecordView(TimeRecord record) {
        if (record != null) {
            this.timeRecordId = String.valueOf(record.getTimeRecordId());
            this.employeeId = record.getEmployeeId();
            this.supervisorId = record.getSupervisorId();
            this.lastUpdater = record.getLastUpdater();
            this.respHeadCode = record.getRespHeadCode();
            this.scope = (record.getRecordStatus() != null) ? record.getRecordStatus().getScope().getCode() : null;
            this.active = record.isActive();
            this.payPeriod = new PayPeriodView(record.getPayPeriod());
            this.beginDate = record.getBeginDate();
            this.endDate = record.getEndDate();
            this.remarks = record.getRemarks();
            this.exceptionDetails = record.getExceptionDetails();
            this.processedDate = record.getProcessedDate();
            this.recordStatus = record.getRecordStatus() != null ? record.getRecordStatus().name() : null;
            this.originalUserId = record.getOriginalUserId();
            this.updateUserId = record.getUpdateUserId();
            this.originalDate = record.getCreatedDate();
            this.updateDate = record.getUpdateDate();
        }
    }

    @JsonIgnore
    public TimeRecord toTimeRecord() {
        TimeRecord record = new TimeRecord();
        record.setTimeRecordId(timeRecordId != null && timeRecordId.matches("\\d+") ? new BigInteger(timeRecordId) : null);
        record.setEmployeeId(employeeId);
        record.setSupervisorId(supervisorId);
        record.setLastUpdater(lastUpdater);
        record.setRespHeadCode(respHeadCode);
        record.setActive(active);
        record.setPayPeriod(payPeriod.toPayPeriod());
        record.setBeginDate(beginDate);
        record.setEndDate(endDate);
        record.setRemarks(remarks);
        record.setExceptionDetails(exceptionDetails);
        record.setProcessedDate(processedDate);
        record.setRecordStatus(recordStatus != null ? TimeRecordStatus.valueOf(recordStatus) : null);
        record.setOriginalUserId(originalUserId);
        record.setUpdateUserId(updateUserId);
        record.setCreatedDate(originalDate);
        record.setUpdateDate(updateDate);
        return record;
    }

    @XmlElement
    public String getTimeRecordId() {
        return timeRecordId;
    }

    @XmlElement
    public Integer getEmployeeId() {
        return employeeId;
    }

    @XmlElement
    public Integer getSupervisorId() {
        return supervisorId;
    }

    @XmlElement
    public String getScope() {
        return scope;
    }

    @XmlElement
    public String getLastUpdater() {
        return lastUpdater;
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
    public String getRemarks() {
        return remarks;
    }

    @XmlElement
    public String getExceptionDetails() {
        return exceptionDetails;
    }

    @XmlElement
    public LocalDate getProcessedDate() {
        return processedDate;
    }

    @XmlElement
    public String getRecordStatus() {
        return recordStatus;
    }

    @XmlElement
    public String getOriginalUserId() {
        return originalUserId;
    }

    @XmlElement
    public String getUpdateUserId() {
        return updateUserId;
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
    public String getRespHeadCode() {
        return respHeadCode;
    }

    @XmlElement
    public PayPeriodView getPayPeriod() {
        return payPeriod;
    }

    @Override
    public String getViewType() {
        return "time-record-simple";
    }
}
