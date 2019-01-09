package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ErrorResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.travel.application.allowances.AllowancesDtoView;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingAllowancesView;
import gov.nysenate.ess.travel.application.allowances.meal.MealAllowancesView;
import gov.nysenate.ess.travel.application.route.RouteView;
import gov.nysenate.ess.travel.provider.ProviderException;
import gov.nysenate.ess.travel.utils.Dollars;
import gov.nysenate.ess.travel.utils.UploadProcessor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        TravelApplication app = uncompletedAppService.getSavedTravelApplication(empId, getSubjectEmployeeId());
        checkWritePermissions(app.getTraveler().getEmployeeId());
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    /**
     * Submits an application to be reviewed.
     *
     * @param id The Uncompleted Application Id to be submitted.
     *
     *           <p>
     *           (PUT) /api/v1/travel/application/uncompleted/{id}/purpose
     *           </p>
     *           <p>
     *           Path Params: id (string) - The id of an uncompleted travel application to submit.
     * @return
     */
    @RequestMapping(value = "/{id}/submit", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse submitTravelApp(@PathVariable String id) {
        checkWritePermissions(id);
        TravelApplication app = uncompletedAppService.submitApplication(UUID.fromString(id));
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    /**
     * Deletes the uncompleted travel application belonging the the specified employee.
     * <p>
     * This allows users to reset their application and start over.
     *
     * <p>
     * (DELETE) /api/v1/travel/application/uncompleted/{empId}
     * </p>
     * Path Params: empId (int) - The employee Id who's uncompleted applications should be deleted.
     *
     * @param empId
     * @return
     */
    @RequestMapping(value = "/{empId}", method = RequestMethod.DELETE)
    public BaseResponse cancelApplication(@PathVariable int empId) {
        checkWritePermissions(empId);
        uncompletedAppService.deleteUncompletedTravelApplication(empId);
        return new SimpleResponse(true, "Successfully canceled travel application", "travel-app-cancel");
    }

    /**
     * Saves a purpose to an uncompleted application.
     * <p>
     * (PUT) /api/v1/travel/application/uncompleted/{id}/purpose
     * <p>
     * Path Params: id (string) - The id of an uncompleted travel application to update with the given purpose.<br>
     * Body : The purpose of travel.
     *
     * @param id
     * @param purpose
     * @return The application with its purpose updated.
     */
    @RequestMapping(value = "/{id}/purpose", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse savePurpose(@PathVariable String id, @RequestBody String purpose) {
        checkWritePermissions(id);
        TravelApplication app = uncompletedAppService.savePurpose(UUID.fromString(id), purpose);
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    /**
     * Saves the outbound segments of an uncompleted application.
     * <p>
     * (PUT) /api/v1/travel/application/uncompleted/{id}/outbound
     * <p>
     * Path Params: id (String) - The id of an uncompleted travel application to update with the given outbound segments.<br>
     * Body : An array of SegmentView's
     */
    @RequestMapping(value = "/{id}/outbound", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse saveOutboundSegments(@PathVariable String id, @RequestBody RouteView route) {
        checkWritePermissions(id);
        TravelApplication app = uncompletedAppService.saveOutboundLegs(UUID.fromString(id), route.toRoute().getOutgoingLegs());
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    /**
     * Saves the return segments of an uncompleted application and calculates the Route and Accommodations.
     * <p>
     * (PUT) /api/v1/travel/application/uncompleted/{id}/return
     * <p>
     * Path Params: id (String) - The id of an uncompleted travel application to update with the given return segments.<br>
     * Body : An array of SegmentView's
     *
     * @throws ProviderException if an error is encountered while communicating with any of our 3rd party data providers.
     *                           This exception is handled by the {@link #handleProviderException(HttpServletRequest, ProviderException)} method.
     */
    @RequestMapping(value = "/{id}/return", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse saveReturnSegments(@PathVariable String id, @RequestBody RouteView route) {
        checkWritePermissions(id);
        TravelApplication app = uncompletedAppService.saveReturnLegs(UUID.fromString(id), route.toRoute().getReturnLegs());
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    /**
     * Sets the user specified expenses on a travel application
     * <p>
     * (PUT) /api/v1/travel/application/uncompleted/{id}/expenses
     * <p>
     */
    @RequestMapping(value = "/{id}/expenses", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse saveExpenses(@PathVariable String id, @RequestBody AllowancesDtoView allowancesDto) {
        checkWritePermissions(id);
        TravelApplication app = uncompletedAppService.saveExpenses(UUID.fromString(id), new Dollars(allowancesDto.tollsAllowance),
                new Dollars(allowancesDto.getParkingAllowance()), new Dollars(allowancesDto.getAlternateAllowance()),
                new Dollars(allowancesDto.getRegistrationAllowance()), new Dollars(allowancesDto.getTrainAndAirplaneAllowance()));
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    /**
     * Updates a travel application with the provided meal allowances
     * <p>
     * (PUT) /api/v1/travel/application/uncompleted/{id}/meal-allowances
     * <p>
     */
    @RequestMapping(value = "/{id}/meal-allowances", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse updateMealAllowances(@PathVariable String id, @RequestBody MealAllowancesView mealAllowancesView) {
        checkWritePermissions(id);
        TravelApplication app = uncompletedAppService.updateMealAllowances(UUID.fromString(id), mealAllowancesView.toMealAllowances());
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    /**
     * Updates a travel application with the provided lodging allowances
     * <p>
     * (PUT) /api/v1/travel/application/uncompleted/{id}/lodging-allowances
     * <p>
     */
    @RequestMapping(value = "/{id}/lodging-allowances", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse updateLodgingAllowances(@PathVariable String id, @RequestBody LodgingAllowancesView lodgingAllowancesView) {
        checkWritePermissions(id);
        TravelApplication app = uncompletedAppService.updateLodgingAllowances(UUID.fromString(id), lodgingAllowancesView.toLodgingAllowances());
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    // Checks that the logged in user is allowed to modify this application.
    private void checkWritePermissions(String appId) {
        TravelApplication app = uncompletedAppDao.selectUncompletedApplication(UUID.fromString(appId));
        checkWritePermissions(app.getTraveler().getEmployeeId());
    }

    // Checks that the logged in user is allowed to modify applications where the traveler's empId = empId.
    private void checkWritePermissions(int empId) {
        checkPermission(new CorePermission(empId, TRAVEL_APPLICATION, RequestMethod.GET));
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

    /**
     * Upload one or more files to attach to a application.
     */
    @RequestMapping(value = "/{id}/attachment", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse addAttachments(@PathVariable String id, @RequestParam("file") MultipartFile[] files) throws IOException {
        TravelApplication app = uncompletedAppDao.selectUncompletedApplication(UUID.fromString(id));

        List<TravelAttachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            attachments.add(uploadProcessor.uploadTravelAttachment(file));
        }
        app.addAttachments(attachments);
        uncompletedAppDao.saveUncompletedApplication(app);
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }


    /**
     * Delete an attachment
     *
     * @param id
     * @param attachmentId
     * @return
     */
    @RequestMapping(value = "/{id}/attachment/{attachmentId}", method = RequestMethod.DELETE)
    public BaseResponse deleteAttachment(@PathVariable String id, @PathVariable String attachmentId) {
        TravelApplication app = uncompletedAppDao.selectUncompletedApplication(UUID.fromString(id));
        app.deleteAttachment(attachmentId);
        uncompletedAppDao.saveUncompletedApplication(app);
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    @ExceptionHandler(ProviderException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ResponseBody
    public ErrorResponse handleProviderException(HttpServletRequest request, ProviderException ex) {
        logger.error("Error communicating with 3rd party data providers.", ex);
        return new ErrorResponse(ErrorCode.DATA_PROVIDER_ERROR);
    }
}
