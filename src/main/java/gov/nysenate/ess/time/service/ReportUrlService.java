package gov.nysenate.ess.time.service;

import gov.nysenate.ess.core.model.period.PayPeriod;

import java.net.MalformedURLException;
import java.net.URL;

public interface ReportUrlService {
    /**
     * Gets a URL that will provide the accrual report for the given employee on the given pay period.
     * @param empId int
     * @param payPeriod {@link PayPeriod}
     * @return URI - accrual report URL.
     */
    URL getAccrualReportUrl(int empId, PayPeriod payPeriod) throws MalformedURLException;

    /**
     * Will provide the attendance report on the given time record.
     * @param timeRecordId to retrieve.
     * @return URI - attendance report URL.
     */
    URL getAttendanceReportUrl(String timeRecordId) throws MalformedURLException;
}
