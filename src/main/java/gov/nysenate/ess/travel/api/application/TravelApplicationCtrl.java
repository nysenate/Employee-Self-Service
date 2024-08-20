package gov.nysenate.ess.travel.api.application;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.request.attachment.Attachment;
import gov.nysenate.ess.travel.request.attachment.SqlAttachmentDao;
import gov.nysenate.ess.travel.request.app.TravelApplication;
import gov.nysenate.ess.travel.request.app.TravelApplicationService;
import gov.nysenate.ess.travel.report.pdf.TravelAppPdfGenerator;
import gov.nysenate.ess.travel.review.ApplicationReview;
import gov.nysenate.ess.travel.review.ApplicationReviewService;
import gov.nysenate.ess.travel.utils.AttachmentService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel")
public class TravelApplicationCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(TravelApplicationCtrl.class);

    @Autowired private TravelApplicationService appService;
    @Autowired private ApplicationReviewService appReviewService;
    @Autowired private AttachmentService attachmentService;
    @Autowired private SqlAttachmentDao attachmentDao;

    @RequestMapping(value = "/application/{appId}", method = RequestMethod.GET)
    public BaseResponse getTravelAppById(@PathVariable int appId) {
        TravelApplication app = appService.getTravelApplication(appId);
        checkTravelAppPermission(app, RequestMethod.GET);
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    @RequestMapping(value = "/application/{appId}.pdf", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getAppPdf(@PathVariable int appId) throws IOException {
        ApplicationReview appReview = appReviewService.getApplicationReviewByAppId(appId);
        checkTravelAppPermission(appReview.application(), RequestMethod.GET);

        TravelAppPdfGenerator pdfGenerator = new TravelAppPdfGenerator(appReview);
        try (ByteArrayOutputStream pdfBytes = new ByteArrayOutputStream()) {
            // Draw the watermark unless user has TRAVEL_ADMIN or SOS roles.
            boolean drawWatermark = !(getSubject().hasRole(TravelRole.TRAVEL_ADMIN.name()) || getSubject().hasRole(TravelRole.SECRETARY_OF_THE_SENATE.name()));
            pdfGenerator.write(pdfBytes, drawWatermark);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            return new ResponseEntity<>(pdfBytes.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException ex) {
            logger.error("Error generating pdf for appId: " + appId, ex);
            throw ex;
        }
    }

    @RequestMapping(value = "/applications")
    public BaseResponse getActiveTravelApps() {
        List<TravelApplication> apps = appService.selectTravelApplications(getSubjectEmployeeId());
        List<TravelApplicationView> appViews = apps.stream()
                .map(TravelApplicationView::new)
                .collect(Collectors.toList());
        return ListViewResponse.of(appViews);
    }

    @RequestMapping(value = "/application/attachment/{uuid}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getAttachment(@PathVariable String uuid) throws IOException {
        Attachment attachment = attachmentDao.selectAttachment(uuid);
        File attachmentFile = attachmentService.getAttachmentFile(uuid);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(attachment.getContentType()));
        byte[] bytes = FileUtils.readFileToByteArray(attachmentFile);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
}
