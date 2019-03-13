package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ErrorResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.travel.application.allowances.AllowancesView;
import gov.nysenate.ess.travel.application.route.RouteView;
import gov.nysenate.ess.travel.provider.ProviderException;
import gov.nysenate.ess.travel.utils.UploadProcessor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static gov.nysenate.ess.core.model.auth.CorePermissionObject.TRAVEL_APPLICATION;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/application/uncompleted")
public class UncompletedTravelAppCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(UncompletedTravelAppCtrl.class);

    @Autowired private UncompletedTravelApplicationService uncompletedAppService;
    @Autowired private UncompletedTravelApplicationDao uncompletedAppDao;
    @Autowired private UploadProcessor uploadProcessor;

    /**
     * Gets a users currently saved travel application or creates a new empty application.
     * <p>
     * (POST) /api/v1/travel/application/uncompleted/init
     * <p>
     * Request Params: empId (int) - The travelers employee id.
     *
     * @param empId
     * @return An application with the traveler and submitter initialized.
     */
    @RequestMapping(value = "/init", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse getSavedTravelApplication(@RequestParam int empId) {
        TravelApplication app = uncompletedAppService.getSavedTravelApplication(empId);
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    /**
     * Submits an application to be reviewed.
     *           <p>
     *           (POST) /api/v1/travel/application/uncompleted/submit
     *           </p>
     *           <p>
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse submitTravelApp() {
        int travelerId = getSubjectEmployeeId();
        checkWritePermissions(travelerId);
        TravelApplication app = uncompletedAppService.submitApplication(travelerId);
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    /**
     * Deletes a uncompleted travel application.
     * <p>
     * This allows users to reset their application and start over.
     *
     * <p>
     * (DELETE) /api/v1/travel/application/uncompleted
     * </p>
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public BaseResponse cancelApplication() {
        int travelerId = getSubjectEmployeeId();
        checkWritePermissions(travelerId);
        uncompletedAppService.deleteUncompletedTravelApplication(travelerId);
        return new SimpleResponse(true, "Successfully canceled travel application", "travel-app-cancel");
    }

    /**
     * Saves a purpose to an uncompleted application.
     * <p>
     * (PUT) /api/v1/travel/application/uncompleted/purpose
     * <p>
     * Body : The purpose of travel.
     *
     * @param purpose
     * @return The application with its purpose updated.
     */
    @RequestMapping(value = "/purpose", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse savePurpose(@RequestBody String purpose) {
        int travelerId = getSubjectEmployeeId();
        checkWritePermissions(travelerId);
        TravelApplication app = uncompletedAppService.savePurpose(travelerId, purpose);
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    /**
     * Saves the outbound segments of an uncompleted application.
     * <p>
     * (PUT) /api/v1/travel/application/uncompleted/outbound
     * <p>
     * Body : An array of SegmentView's
     */
    @RequestMapping(value = "/outbound", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse saveOutboundSegments(@RequestBody RouteView route) {
        int travelerId = getSubjectEmployeeId();
        checkWritePermissions(travelerId);
        TravelApplication app = uncompletedAppService.saveOutboundLegs(travelerId, route.toRoute().getOutgoingLegs());
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    /**
     * Saves the return segments of an uncompleted application and calculates the Route and Accommodations.
     * <p>
     * (PUT) /api/v1/travel/application/uncompleted/return
     * <p>
     * Body : An array of SegmentView's
     *
     * @throws ProviderException if an error is encountered while communicating with any of our 3rd party data providers.
     *                           This exception is handled by the {@link #handleProviderException(HttpServletRequest, ProviderException)} method.
     */
    @RequestMapping(value = "/return", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse saveReturnSegments(@RequestBody RouteView route) {
        int travelerId = getSubjectEmployeeId();
        checkWritePermissions(travelerId);
        TravelApplication app = uncompletedAppService.saveRoute(travelerId, route.toRoute());
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    /**
     * Sets the user specified expenses on a travel application
     * <p>
     * (PUT) /api/v1/travel/application/uncompleted/expenses
     * <p>
     */
    @RequestMapping(value = "/expenses", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse saveExpenses(@RequestBody AllowancesView allowancesView) {
        int travelerId = getSubjectEmployeeId();
        checkWritePermissions(travelerId);
        TravelApplication app = uncompletedAppService.saveExpenses(travelerId, allowancesView.toAllowances());
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    // Checks that the logged in user is allowed to modify this application.
    private void checkWritePermissions(int travelerId) {
        checkPermission(new CorePermission(travelerId, TRAVEL_APPLICATION, RequestMethod.POST));
    }

    // TODO: Move attachment API's to their own controller? Would need to update purpose page logic.

    @ResponseBody
    @RequestMapping(value = "/{id}/attachment/{attachmentId}", method = RequestMethod.GET)
    public void getAttachment(@PathVariable int id, @PathVariable String attachmentId, HttpServletResponse response) throws IOException {
        File attachment = uploadProcessor.getAttachmentFile(attachmentId);
        if (!attachment.exists()) {
            response.sendError(404, "Cannot find file for attachment " + attachmentId);
            return;
        }

        InputStream is = null;
        try {
            is = new FileInputStream(attachment);
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            logger.error("Error fetching travel attachment " + attachmentId, e);
            response.sendError(500, e.getMessage());
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
//
//    /**
//     * Upload one or more files to attach to a application.
//     */
//    @RequestMapping(value = "/{id}/attachment", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public BaseResponse addAttachments(@PathVariable String id, @RequestParam("file") MultipartFile[] files) throws IOException {
//        TravelApplication app = uncompletedAppDao.selectUncompletedApplication(UUID.fromString(id));
//
//        List<TravelAttachment> attachments = new ArrayList<>();
//        for (MultipartFile file : files) {
//            attachments.add(uploadProcessor.uploadTravelAttachment(file));
//        }
//        app.addAttachments(attachments);
//        uncompletedAppDao.saveUncompletedApplication(app);
//        return new ViewObjectResponse<>(new TravelApplicationView(app));
//    }
//
//
//    /**
//     * Delete an attachment
//     *
//     * @param id
//     * @param attachmentId
//     * @return
//     */
//    @RequestMapping(value = "/{id}/attachment/{attachmentId}", method = RequestMethod.DELETE)
//    public BaseResponse deleteAttachment(@PathVariable String id, @PathVariable String attachmentId) {
//        TravelApplication app = uncompletedAppDao.selectUncompletedApplication(UUID.fromString(id));
//        app.deleteAttachment(attachmentId);
//        uncompletedAppDao.saveUncompletedApplication(app);
//        return new ViewObjectResponse<>(new TravelApplicationView(app));
//    }

    @ExceptionHandler(ProviderException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ResponseBody
    public ErrorResponse handleProviderException(HttpServletRequest request, ProviderException ex) {
        logger.error("Error communicating with 3rd party data providers.", ex);
        return new ErrorResponse(ErrorCode.DATA_PROVIDER_ERROR);
    }
}
