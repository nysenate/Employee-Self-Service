package gov.nysenate.ess.time.service.attendance;

import gov.nysenate.ess.core.model.personnel.Employee;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.time.LocalDateTime;

/**
 * Encapsulates an exception thrown in the {@link TimeRecordManager}
 */
public class TimeRecordManagerError {

    /** The employee whose records were being managed when the ex. occurred */
    private Employee employee;

    /** The time that the exception occurred */
    private LocalDateTime timestamp;

    /** The exception */
    private String stackTrace;

    public TimeRecordManagerError(Employee employee, LocalDateTime timestamp, Throwable ex) {
        this.employee = employee;
        this.timestamp = timestamp;
        this.stackTrace = ExceptionUtils.getStackTrace(ex);
    }

    public Employee getEmployee() {
        return employee;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getStackTrace() {
        return stackTrace;
    }
}
