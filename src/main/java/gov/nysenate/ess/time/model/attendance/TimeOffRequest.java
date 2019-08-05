package gov.nysenate.ess.time.model.attendance;

import java.util.Comparator;
import java.util.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class TimeOffRequest implements Comparable<TimeOffRequest> {

    public static final Comparator<TimeOffRequest> defaultComparator =
            Comparator.comparing(TimeOffRequest::getTimestamp);

    protected int requestId = -1;
    protected int employeeId;
    protected int supervisorId;
    protected TimeOffStatus status;
    protected Timestamp updateTimestamp;
    protected Date startDate;
    protected Date endDate;
    protected List<TimeOffRequestComment> comments;
    protected List<TimeOffRequestDay> days;

    public TimeOffRequest() {}

    /**
     * Main Constructor
     * - No requestId or timestamp required in params
     */
    public TimeOffRequest(int employeeId, int supervisorId, TimeOffStatus status,
                          Date startDate, Date endDate, List<TimeOffRequestComment> comments,
                          List<TimeOffRequestDay> days) {
        this.employeeId = employeeId;
        this.supervisorId = supervisorId;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.comments = comments;
        this.days = days;
        Date today = new Date();
        this.updateTimestamp = new Timestamp(today.getTime());
    }

    /**
     * Secondary constructor
     * - Used for mapping TimeOffRequests after SQL queries
     */
    public TimeOffRequest(int requestId, int employeeId, int supervisorId, TimeOffStatus status,
                          Timestamp updateTimestamp, Date startDate, Date endDate,
                          List<TimeOffRequestComment> comments, List<TimeOffRequestDay> days) {
        this.requestId = requestId;
        this.employeeId = employeeId;
        this.supervisorId = supervisorId;
        this.status = status;
        this.updateTimestamp = updateTimestamp;
        this.startDate = startDate;
        this.endDate = endDate;
        this.comments = comments;
        this.days = days;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeOffRequest that = (TimeOffRequest) o;
        return requestId == that.requestId &&
                employeeId == that.employeeId &&
                supervisorId == that.supervisorId &&
                status == that.status &&
                updateTimestamp.equals(that.updateTimestamp) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(comments, that.comments) &&
                Objects.equals(days, that.days);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, employeeId, supervisorId, status, updateTimestamp, startDate, endDate, comments, days);
    }

    @Override
    public int compareTo(TimeOffRequest t2) {
        return defaultComparator.compare(this, t2);
    }

    /*Basic Getters and Setters*/

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(int supervisorId) {
        this.supervisorId = supervisorId;
    }

    public TimeOffStatus getStatus() {
        return status;
    }

    public void setStatus(TimeOffStatus status) {
        this.status = status;
    }

    public Timestamp getTimestamp() {
        return updateTimestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.updateTimestamp = timestamp;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<TimeOffRequestComment> getComments() {
        return comments;
    }

    public void setComments(List<TimeOffRequestComment> comments) {
        this.comments = comments;
    }

    public List<TimeOffRequestDay> getDays() {
        return days;
    }

    public void setDays(List<TimeOffRequestDay> days) {
        this.days = days;
    }
}

