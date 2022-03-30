package gov.nysenate.ess.time.service;

import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.time.model.accrual.PeriodAccSummary;
import gov.nysenate.ess.time.model.accrual.PeriodAccUsage;
import gov.nysenate.ess.time.service.ReportUrlService;
import gov.nysenate.ess.time.service.accrual.AccrualComputeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.format.DateTimeFormatter;

@Service("sfmsurlservice")
public class SfmsReportUrlService implements ReportUrlService {
    private static final DateTimeFormatter paramDateFmt = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private final String sfmsBaseUrl;
    private final AccrualComputeService accrualComputeService;

    @Autowired
    public SfmsReportUrlService(@Value("${sfms.report.base.url}") String accrualReportBaseUrl,
                                AccrualComputeService accrualComputeService) {
        this.sfmsBaseUrl = accrualReportBaseUrl.trim();
        this.accrualComputeService = accrualComputeService;
    }

    /** {@inheritDoc} */
    @Override
    public URL getAccrualReportUrl(int empId, PayPeriod payPeriod) throws MalformedURLException {
        UriComponentsBuilder builder = getBaseAccrualUrlBuilder(empId, payPeriod);
        PeriodAccSummary accruals = accrualComputeService.getAccruals(empId, payPeriod);
        if (accruals.isComputed()) {
            builder = withComputedParams(builder, accruals);
        }
        return builder.build().toUri().toURL();
    }

    @Override
    public URL getAttendanceReportUrl(String timeRecordId) throws MalformedURLException {
        return UriComponentsBuilder.fromHttpUrl(sfmsBaseUrl)
                .queryParam("report", "PRTIMESHEET23")
                .queryParam("cmdkey", "tsuser")
                .queryParam("p_stamp", "N")
                .queryParam("p_nuxrtimesheet", timeRecordId).build().toUri().toURL();
    }

    private UriComponentsBuilder getBaseAccrualUrlBuilder(int empId, PayPeriod payPeriod) {
        return UriComponentsBuilder.fromHttpUrl(sfmsBaseUrl)
                .queryParam("report", "PRBSTS23")
                .queryParam("cmdkey", "tsuser")
                .queryParam("p_nuxrefem", empId)
                .queryParam("p_dtend", paramDateFmt.format(payPeriod.getEndDate()));
    }

    private UriComponentsBuilder withComputedParams(UriComponentsBuilder builder, PeriodAccSummary accruals) {
        PeriodAccUsage perAccUsag = accruals.getPeriodAccUsage();
        return builder
                .queryParam("p_datafrom", "AUTO")
                .queryParam("p_proj", accruals.isSubmitted() ? "N" : "Y")
                .queryParam("p_dtbegin", paramDateFmt.format(accruals.getPayPeriod().getStartDate()))

                // Total Hours YTD was passed instead, set to 0 since these hours are added in the report
                .queryParam("p_nutothrslast", 0)

                .queryParam("p_nubiwvacrate", accruals.getVacRate().toPlainString())
                .queryParam("p_nubiwsicrate", accruals.getSickRate().toPlainString())

                .queryParam("p_nuvachrsbsd", accruals.getVacHoursBanked().toPlainString())
                .queryParam("p_nuemphrsbsd", accruals.getEmpHoursBanked().toPlainString())

                .queryParam("p_nuperhrsacc", accruals.getPerHoursAccrued().toPlainString())
                .queryParam("p_nuvachrsacc", accruals.getVacHoursAccrued().toPlainString())
                .queryParam("p_nuemphrsacc", accruals.getEmpHoursAccrued().toPlainString())

                .queryParam("p_nuperhrsuse", accruals.getPerHoursUsed().toPlainString())
                .queryParam("p_nuvachrsuse", accruals.getVacHoursUsed().toPlainString())
                .queryParam("p_nuemphrsuse", accruals.getEmpHoursUsed().toPlainString())
                .queryParam("p_nufamhrsuse", accruals.getFamHoursUsed().toPlainString())

                .queryParam("p_nuworkhrs", perAccUsag.getWorkHours().toPlainString())
                .queryParam("p_nuperhrs", perAccUsag.getPerHoursUsed().toPlainString())
                .queryParam("p_nuvachrs", perAccUsag.getVacHoursUsed().toPlainString())
                .queryParam("p_nuemphrs", perAccUsag.getEmpHoursUsed().toPlainString())
                .queryParam("p_nufamhrs", perAccUsag.getFamHoursUsed().toPlainString())
                .queryParam("p_nuholhrs", perAccUsag.getHolHoursUsed().toPlainString())
                .queryParam("p_nutrvhrs", perAccUsag.getTravelHoursUsed().toPlainString())

                .queryParam("p_nuhrsexpect", accruals.getExpectedTotalHours().toPlainString())
                .queryParam("p_nutotalhrs", accruals.getTotalHoursYtd().toPlainString())
                ;
    }

    public String getAccrualReportBaseUrl() {
        return sfmsBaseUrl;
    }
}
