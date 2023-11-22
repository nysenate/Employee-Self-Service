package gov.nysenate.ess.travel.api;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ErrorResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.api.application.*;
import gov.nysenate.ess.travel.employee.TravelEmployee;
import gov.nysenate.ess.travel.employee.TravelEmployeeService;
import gov.nysenate.ess.travel.request.app.*;
import gov.nysenate.ess.travel.request.attachment.Attachment;
import gov.nysenate.ess.travel.request.draft.*;
import gov.nysenate.ess.travel.request.route.*;
import gov.nysenate.ess.travel.provider.ProviderException;
import gov.nysenate.ess.travel.utils.AttachmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/drafts")
public class DraftCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(DraftCtrl.class);
    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private RouteViewValidator routeViewValidator;
    @Autowired private TravelAppUpdateService appUpdateService;
    @Autowired private AttachmentService attachmentService;
    @Autowired private TravelEmployeeService travelEmployeeService;
    @Autowired private DraftService draftService;

    /**
     * Create new Draft API
     * -----------------------------
     * This creates a new Draft with the initial traveler set to the current user.
     * <p>
     * Usage:   (PUT) /api/v1/travel/drafts
     * </p>
     */
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public BaseResponse createDraft() {
        Employee user = employeeInfoService.getEmployee(getSubjectEmployeeId());
        TravelEmployee defaultTraveler = travelEmployeeService.getTravelEmployee(user);
        Draft draft = new Draft(0, getSubjectEmployeeId(), defaultTraveler);
        DraftView draftView = new DraftView(draft);
        return new ViewObjectResponse<>(draftView);
    }

    /**
     * Get Drafts API
     * -----------------------------
     * Retrieves the saved drafts for the current user.
     * <p>
     * Usage:   (GET) /api/v1/travel/drafts
     * </p>
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse getUsersDrafts() {
        List<Draft> drafts = draftService.getUserDrafts(getSubjectEmployeeId());
        List<DraftView> draftViews = drafts.stream()
                .map(DraftView::new)
                .collect(Collectors.toList());
        return ListViewResponse.of(draftViews);
    }

    /**
     * Fetch Draft API
     * -----------------------------
     * Retrieves the draft with the provided id.
     * <p>
     * Usage:   (GET) /api/v1/travel/drafts/{id}
     * <p>
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public BaseResponse getDraft(@PathVariable int id) {
        Draft draft = draftService.getDraft(id, getSubjectEmployeeId());
        return new ViewObjectResponse<>(new DraftView(draft));
    }

    /**
     * Delete a Draft API
     * -----------------------------
     * Deletes the draft with the provided id.
     * <p>
     * Usage:   (DELETE) /api/v1/travel/drafts/{id}
     * <p>
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public BaseResponse deleteDraft(@PathVariable int id) {
        draftService.deleteDraft(id, getSubjectEmployeeId());
        return new SimpleResponse(true, "Successfully deleted draft " + id, "");
    }

    /**
     * Patch a Draft API
     * ----------------------------
     * Updates pieces of a Draft.
     * Usage:   (PATCH) /api/v1/travel/drafts
     */
    @RequestMapping(value = "", method = RequestMethod.PATCH)
    public BaseResponse patchDraftApp(@RequestBody DraftViewPatches draftPatches) throws ProviderException, IOException {
        Draft draft = draftPatches.getDraft().toDraft();

        for (DraftViewPatchOption option : draftPatches.getOptions()) {
            switch (option) {
                case ROUTE:
                    routeViewValidator.validateTravelDates(new RouteView(draft.getTravelApplication().getRoute()));
                    appUpdateService.updateRoute(draft);
                    break;
                case ALLOWANCES:
//                    draft.setTravelApplication(appUpdateService.updateAllowances(draft.getTravelApplication(), draft.getTravelApplication().allowances()));
                    break;
                case MEAL_PER_DIEMS:
//                    draft.setTravelApplication(appUpdateService.updateMealPerDiems(draft.getTravelApplication(), draft.getTravelApplication().mealPerDiems()));
                    break;
                case LODGING_PER_DIEMS:
//                    draft.setTravelApplication(appUpdateService.updateLodgingPerDiems(draft.getTravelApplication(), draft.getTravelApplication().lodgingPerDiems()));
                    break;
                case MILEAGE_PER_DIEMS:
//                    draft.setTravelApplication(appUpdateService.updateMileagePerDiems(draft.getTravelApplication(), draft.getTravelApplication().mileagePerDiems()));
                    break;
                default:
                    logger.info("Call to travel draft patch API did not contain a valid patch option. Patches were: " + draftPatches.getOptions());
            }
        }

        DraftView draftView = new DraftView(draft);
        return new ViewObjectResponse<>(draftView);
    }

    /**
     * Submit Draft API
     * --------------------------
     * Saves the draft included in the response body.
     * <p>
     * Usage:   (POST) /api/v1/travel/drafts/submit
     * <p>
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public BaseResponse submitDraft(@RequestBody DraftView draftView) {
        Employee user = employeeInfoService.getEmployee(getSubjectEmployeeId());
        Draft draft = draftView.toDraft();

        TravelApplication app = appUpdateService.submitTravelApplication(draft, user);
        draftService.deleteDraft(draft.getId(), getSubjectEmployeeId());
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    /**
     * Save Draft API
     * --------------------------
     * Saves a draft without submitting it.
     * <p>
     * Usage:   (POST) /api/v1/travel/drafts
     * <p>
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public BaseResponse saveDraft(@RequestBody DraftView draftView) {
        Draft draft = draftView.toDraft();
        draft = draftService.saveDraft(draft);
        return new ViewObjectResponse<>(new DraftView(draft));
    }

    /**
     * Upload Attachment API
     * --------------------------
     * Uploads the provided files and returns them as AttachmentView's.
     * <p>
     * Usage:   (POST) /api/v1/travel/drafts/attachment
     * <p>
     * @param files
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/attachment", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse addAttachments(@RequestParam("file") MultipartFile[] files) throws IOException {
        List<Attachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            attachments.add(attachmentService.uploadAttachment(file));
        }

        List<AttachmentView> attachmentViews = attachments.stream()
                .map(AttachmentView::new)
                .collect(Collectors.toList());
        return ListViewResponse.of(attachmentViews);
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

