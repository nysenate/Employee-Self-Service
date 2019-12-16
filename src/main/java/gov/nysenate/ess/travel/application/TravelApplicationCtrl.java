package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.report.pdf.TravelAppPdfGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel")
public class TravelApplicationCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(TravelApplicationCtrl.class);
    @Autowired private TravelApplicationService appService;
    @Autowired private EmployeeInfoService employeeInfoService;

    @RequestMapping(value = "/application/{appId}", method = RequestMethod.GET)
    public BaseResponse getTravelAppById(@PathVariable int appId) {
        TravelApplication app = appService.getTravelApplication(appId);
        checkTravelAppPermission(app, RequestMethod.GET);
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    @RequestMapping(value = "/application/{appId}.pdf", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getAppPdf(@PathVariable int appId) throws IOException {
        TravelApplication app = appService.getTravelApplication(appId);
        checkTravelAppPermission(app, RequestMethod.GET);

        ByteArrayOutputStream pdfBytes = new ByteArrayOutputStream();
        TravelAppPdfGenerator pdfGenerator = new TravelAppPdfGenerator(app);
        try {
            pdfGenerator.write(pdfBytes);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            return new ResponseEntity<>(pdfBytes.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException ex) {
            logger.error("Error generating pdf for appId: " + appId, ex);
            throw ex;
        }
    }

    @RequestMapping(value = "/application/{appId}", method = RequestMethod.POST)
    public void saveTravelApp(@PathVariable int appId, @RequestBody TravelApplicationView appView) {
        TravelApplication app = appService.getTravelApplication(appId);
        checkTravelAppPermission(app, RequestMethod.POST);

        Employee user = employeeInfoService.getEmployee(getSubjectEmployeeId());
        appService.saveTravelApplication(appView.toTravelApplication(), user);
    }

    @RequestMapping(value = "/applications")
    public BaseResponse getActiveTravelApps() {
        List<TravelApplication> apps = appService.selectTravelApplications(getSubjectEmployeeId());
        List<TravelApplicationView> appViews = apps.stream()
                .map(TravelApplicationView::new)
                .collect(Collectors.toList());
        return ListViewResponse.of(appViews);
    }
}
