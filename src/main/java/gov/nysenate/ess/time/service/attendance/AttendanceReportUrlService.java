package gov.nysenate.ess.time.service.attendance;

import gov.nysenate.ess.core.model.period.PayPeriod;
import org.springframework.web.util.UriComponents;

public interface AttendanceReportUrlService {

    /**
     * Gets a url that will provide the attendance report for the given employee on the given pay period
     * @param empId int
     * @param payPeriod {@link PayPeriod}
     * @return URL - attendance report url
     */
    UriComponents getAttendanceReportUri(int empId, PayPeriod payPeriod);
}
