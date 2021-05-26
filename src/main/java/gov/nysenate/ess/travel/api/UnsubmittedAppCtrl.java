package gov.nysenate.ess.travel.api;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ErrorResponse;
import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.travel.allowedtravelers.AllowedTravelersService;
import gov.nysenate.ess.travel.api.application.AmendmentView;
import gov.nysenate.ess.travel.api.application.PurposeOfTravelView;
import gov.nysenate.ess.travel.api.application.TravelAppEditDto;
import gov.nysenate.ess.travel.api.application.TravelApplicationView;
import gov.nysenate.ess.travel.request.allowances.AllowancesView;
import gov.nysenate.ess.travel.request.allowances.lodging.LodgingPerDiemsView;
import gov.nysenate.ess.travel.request.allowances.meal.MealPerDiemsView;
import gov.nysenate.ess.travel.request.allowances.mileage.MileagePerDiemsView;
import gov.nysenate.ess.travel.request.amendment.Amendment;
import gov.nysenate.ess.travel.request.app.*;
import gov.nysenate.ess.travel.request.attachment.Attachment;
import gov.nysenate.ess.travel.request.route.*;
import gov.nysenate.ess.travel.provider.ProviderException;
import gov.nysenate.ess.travel.request.unsubmitted.UnsubmittedAppDao;
import gov.nysenate.ess.travel.utils.AttachmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("UnnecessaryBoxing") @RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/unsubmitted")
public class UnsubmittedAppCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(UnsubmittedAppCtrl.class);
    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private UnsubmittedAppDao unsubmittedAppDao;
    @Autowired private AllowedTravelersService allowedTravelersService;
    @Autowired private RouteViewValidator routeViewValidator;
    @Autowired private TravelAppUpdateService appUpdateService;
    @Autowired private AttachmentService attachmentService;

    /**
     * Get an unsubmitted app API
     * --------------------------
     * Get the current unsubmitted app for the logged in user.
     * <p>
     * Usage:   (GET) /api/v1/travel/unsubmitted
     * <p>
     *
     * @throws IOException
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse getUnsubmittedApps() {
        Employee user = employeeInfoService.getEmployee(getSubjectEmployeeId());
        Optional<TravelAppEditDto> dtoOpt = unsubmittedAppDao.find(getSubjectEmployeeId());
        TravelAppEditDto appEditDto = null;
        if (dtoOpt.isPresent()) {
            appEditDto = dtoOpt.get();
            // Always make sure the travler's department is up to date.
            appEditDto.setTraveler(new DetailedEmployeeView(
                    employeeInfoService.getEmployee(appEditDto.getTraveler().getEmployeeId())));
        } else {
            appEditDto = dtoOpt.orElseGet(() -> {
                Amendment amd = new Amendment.Builder().build();
                return new TravelAppEditDto(new DetailedEmployeeView(user), new AmendmentView(amd));
            });
        }
        appEditDto.setAllowedTravelers(allowedTravelersService.forEmp(user));
        unsubmittedAppDao.save(getSubjectEmployeeId(), appEditDto.getTraveler(), appEditDto.getAmendment());
        return new ViewObjectResponse<>(appEditDto);
    }

    /**
     * Delete an unsubmitted app API
     * -----------------------------
     * Deletes the currently saved unsubmitted app for the logged in user.
     * This effectively resets the application for starting over.
     * <p>
     * Usage:   (DELETE) /api/v1/travel/unsubmitted
     * <p>
     *
     * @throws IOException
     */
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public void deleteUnsubmittedApp() {
        unsubmittedAppDao.delete(getSubjectEmployeeId());
    }

    /**
     * Patch an unsubmitted app API
     * ----------------------------
     * Updates one or more fields of an unsubmitted app.
     * <p>
     * Usage:   (PATCH) /api/v1/travel/unsubmitted
     * <p>
     * <p>
     * Body:
     *
     * @param patches Map of patch keys to patch values. Patch key represents a field to be updated with the patch value.
     * @return {@link TravelApplicationView} updated with patches.
     * @throws IOException
     */
    @RequestMapping(value = "", method = RequestMethod.PATCH)
    public BaseResponse patchUnsubmittedApp(@RequestBody Map<String, String> patches) throws ProviderException, IOException {
        TravelAppEditDto dto = findApp(getSubjectEmployeeId());
        Amendment amendment = dto.getAmendment().toAmendment();
        Employee user = employeeInfoService.getEmployee(getSubjectEmployeeId());

        // Perform all updates specified in the patch.
        for (Map.Entry<String, String> patch : patches.entrySet()) {
            switch (patch.getKey()) {
                case "traveler":
                    int travelerEmpId = Integer.valueOf(patch.getValue());
                    if (travelerEmpId != dto.getTraveler().getEmployeeId()) {
                        dto.setTraveler(new DetailedEmployeeView(employeeInfoService.getEmployee(travelerEmpId)));
                    }
                    break;
                case "purposeOfTravel":
                    PurposeOfTravelView potView = OutputUtils.jsonToObject(patch.getValue(), PurposeOfTravelView.class);
                    amendment = appUpdateService.updatePurposeOfTravel(amendment, potView.toPurposeOfTravel());
                    break;
                case "outbound":
                    RouteView outboundRouteView = OutputUtils.jsonToObject(patch.getValue(), RouteView.class);
                    amendment = appUpdateService.updateOutboundRoute(amendment, outboundRouteView.toRoute());
                    break;
                case "route":
                    RouteView routeView = OutputUtils.jsonToObject(patch.getValue(), RouteView.class);
                    routeViewValidator.validateTravelDates(routeView);
                    amendment = appUpdateService.updateRoute(amendment, routeView.toRoute());
                    break;
                case "allowances":
                    AllowancesView allowancesView = OutputUtils.jsonToObject(patch.getValue(), AllowancesView.class);
                    amendment = appUpdateService.updateAllowances(amendment, allowancesView.toAllowances());
                    break;
                case "mealPerDiems":
                    MealPerDiemsView mealPerDiemsView = OutputUtils.jsonToObject(patch.getValue(), MealPerDiemsView.class);
                    amendment = appUpdateService.updateMealPerDiems(amendment, mealPerDiemsView.toMealPerDiems());
                    break;
                case "lodgingPerDiems":
                    LodgingPerDiemsView lodgingPerDiemsView = OutputUtils.jsonToObject(patch.getValue(), LodgingPerDiemsView.class);
                    amendment = appUpdateService.updateLodgingPerDiems(amendment, lodgingPerDiemsView.toLodgingPerDiems());
                    break;
                case "mileagePerDiems":
                    MileagePerDiemsView mileagePerDiemView = OutputUtils.jsonToObject(patch.getValue(), MileagePerDiemsView.class);
                    amendment = appUpdateService.updateMileagePerDiems(amendment, mileagePerDiemView.toMileagePerDiems());
                    break;
                default:
                    logger.info("Call to travel application patch API did not contain a valid patch key. Patches were: " + patches.toString());
            }
        }

        AmendmentView amendmentView = new AmendmentView(amendment);
        // Save after all changes are applied.
        unsubmittedAppDao.save(user.getEmployeeId(), dto.getTraveler(), amendmentView);

        dto.setAmendment(amendmentView);
        return new ViewObjectResponse<>(dto);
    }

    /**
     * Submit unsubmitted app API
     * --------------------------
     *
     * @return {@link TravelApplicationView}
     * @throws IOException
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public BaseResponse submitApp() {
        Employee user = employeeInfoService.getEmployee(getSubjectEmployeeId());
        TravelAppEditDto dto = findApp(getSubjectEmployeeId());

        TravelApplication app = appUpdateService.submitTravelApplication(
                dto.getAmendment().toAmendment(), dto.getTraveler().toEmployee(), user);
        unsubmittedAppDao.delete(user.getEmployeeId());
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    @RequestMapping(value = "/attachment", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse addAttachments(@RequestParam("file") MultipartFile[] files) throws IOException {
        TravelAppEditDto dto = findApp(getSubjectEmployeeId());
        Amendment amd = dto.getAmendment().toAmendment();

        List<Attachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            attachments.add(attachmentService.uploadAttachment(file));
        }

        List<Attachment> allAttachments = Stream.concat(amd.attachments().stream(), attachments.stream())
                .collect(Collectors.toList());

        amd = new Amendment.Builder(amd)
                .withAttachments(allAttachments)
                .build();

        AmendmentView amdView = new AmendmentView(amd);
        unsubmittedAppDao.save(getSubjectEmployeeId(), dto.getTraveler(), amdView);
        dto.setAmendment(amdView);
        return new ViewObjectResponse<>(dto);
    }

    /**
     * Delete an attachment
     *
     * @param filename
     * @return
     */
    @RequestMapping(value = "/attachment/{filename}", method = RequestMethod.DELETE)
    public BaseResponse deleteAttachment(@PathVariable String filename) {
        TravelAppEditDto dto = findApp(getSubjectEmployeeId());
        Amendment amd = dto.getAmendment().toAmendment();
        List<Attachment> newAttachments = new ArrayList<>();
        List<Attachment> attachments = amd.attachments();
        for (Attachment attachment : attachments) {
            if (!attachment.getFilename().equals(filename)) {
                newAttachments.add(attachment);
            }
        }

        amd = new Amendment.Builder(amd)
                .withAttachments(newAttachments)
                .build();

        AmendmentView amdView = new AmendmentView(amd);
        unsubmittedAppDao.save(getSubjectEmployeeId(), dto.getTraveler(), amdView);
        dto.setAmendment(amdView);
        return new ViewObjectResponse<>(dto);
    }

    private TravelAppEditDto findApp(int userId) {
        return unsubmittedAppDao.find(userId)
                .orElseThrow(() -> new InvalidRequestParamEx(String.valueOf(userId), "userId", "int",
                        "No Unsubmitted travel app found with provided userId"));
    }

    @ExceptionHandler(InvalidTravelDatesException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse invalidTravelDates(InvalidTravelDatesException ex) {
        return new ErrorResponse(ErrorCode.INVALID_TRAVEL_DATES);
    }

    @ExceptionHandler(ProviderException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ResponseBody
    public ErrorResponse providerException(ProviderException ex) {
        return new ErrorResponse(ErrorCode.DATA_PROVIDER_ERROR);
    }
}

