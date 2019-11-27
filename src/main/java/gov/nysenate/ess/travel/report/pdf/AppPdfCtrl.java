package gov.nysenate.ess.travel.report.pdf;

import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.application.TravelApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/pdf/application")
public class AppPdfCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(AppPdfCtrl.class);

    @Autowired private TravelApplicationService appService;

    @RequestMapping(value = "/{appId}", method = RequestMethod.GET)
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
            logger.error("Error creating pdf for appId: " + appId, ex);
            throw ex;
        }
    }
}
