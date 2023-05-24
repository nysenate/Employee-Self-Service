package gov.nysenate.ess.travel.api;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ErrorResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.allowedtravelers.AllowedTravelersService;
import gov.nysenate.ess.travel.api.application.*;
import gov.nysenate.ess.travel.employee.TravelEmployee;
import gov.nysenate.ess.travel.employee.TravelEmployeeService;
import gov.nysenate.ess.travel.request.amendment.Amendment;
import gov.nysenate.ess.travel.request.app.*;
import gov.nysenate.ess.travel.request.attachment.Attachment;
import gov.nysenate.ess.travel.request.department.SqlDepartmentHeadDao;
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

import javax.swing.text.html.ListView;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("UnnecessaryBoxing") @RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/drafts")
public class DraftCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(DraftCtrl.class);
    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private DraftDao draftDao;
    @Autowired private AllowedTravelersService allowedTravelersService;
    @Autowired private RouteViewValidator routeViewValidator;
    @Autowired private TravelAppUpdateService appUpdateService;
    @Autowired private AttachmentService attachmentService;
    @Autowired private SqlDepartmentHeadDao departmentHeadDao;
    @Autowired private TravelEmployeeService travelEmployeeService;
    @Autowired private DraftService draftService;

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public BaseResponse createDraft() {
        Employee user = employeeInfoService.getEmployee(getSubjectEmployeeId());
        TravelEmployee travelEmployee = travelEmployeeService.getTravelEmployee(user);
        Draft draft = new Draft(travelEmployee);
        DraftView draftView = new DraftView(draft);
        return new ViewObjectResponse<>(draftView);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse getUsersDrafts() {
        List<Draft> drafts = draftService.getUserDrafts(getSubjectEmployeeId());
        List<DraftView> draftViews = drafts.stream()
                .map(DraftView::new)
                .collect(Collectors.toList());
        return ListViewResponse.of(draftViews);
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
        draftDao.delete(getSubjectEmployeeId());
    }

    /**
     * Patch a Draft API
     * ----------------------------
     * Usage:   (PATCH) /api/v1/travel/drafts
     */
    @RequestMapping(value = "", method = RequestMethod.PATCH)
    public BaseResponse patchDraftApp(@RequestBody DraftViewPatches draftPatches) throws ProviderException, IOException {
        Draft draft = draftPatches.getDraft().toDraft();

        for (DraftViewPatchOption option : draftPatches.getOptions()) {
            switch (option) {
                case ROUTE:
                    routeViewValidator.validateTravelDates(new RouteView(draft.getAmendment().route()));
                    draft.setAmendment(appUpdateService.updateRoute(draft.getAmendment(), draft.getAmendment().route()));
                    break;
                case ALLOWANCES:
                    draft.setAmendment(appUpdateService.updateAllowances(draft.getAmendment(), draft.getAmendment().allowances()));
                    break;
                case MEAL_PER_DIEMS:
                    draft.setAmendment(appUpdateService.updateMealPerDiems(draft.getAmendment(), draft.getAmendment().mealPerDiems()));
                    break;
                case LODGING_PER_DIEMS:
                    draft.setAmendment(appUpdateService.updateLodgingPerDiems(draft.getAmendment(), draft.getAmendment().lodgingPerDiems()));
                    break;
                case MILEAGE_PER_DIEMS:
                    draft.setAmendment(appUpdateService.updateMileagePerDiems(draft.getAmendment(), draft.getAmendment().route().mileagePerDiems()));
                    break;
                default:
                    logger.info("Call to travel draft patch API did not contain a valid patch option. Patches were: " + draftPatches.getOptions());
            }
        }

        DraftView draftView = new DraftView(draft);
        return new ViewObjectResponse<>(draftView);
    }

    /**
     * Submit unsubmitted app API
     * --------------------------
     *
     * @return {@link TravelApplicationView}
     * @throws IOException
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public BaseResponse submitApp(@RequestBody DraftView draftView) {
        Employee user = employeeInfoService.getEmployee(getSubjectEmployeeId());
        Draft draft = draftView.toDraft();

        TravelApplication app = appUpdateService.submitTravelApplication(draft, user);
        draftDao.delete(draft.getId());
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

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

//    /**
//     * Delete an attachment
//     *
//     * @param filename
//     * @return
//     */
//    @RequestMapping(value = "/attachment/{filename}", method = RequestMethod.DELETE)
//    public BaseResponse deleteAttachment(@PathVariable String filename) {
//        TravelAppEditDto dto = findApp(getSubjectEmployeeId());
//        Amendment amd = dto.getAmendment().toAmendment();
//        List<Attachment> newAttachments = new ArrayList<>();
//        List<Attachment> attachments = amd.attachments();
//        for (Attachment attachment : attachments) {
//            if (!attachment.getFilename().equals(filename)) {
//                newAttachments.add(attachment);
//            }
//        }
//
//        amd = new Amendment.Builder(amd)
//                .withAttachments(newAttachments)
//                .build();
//
//        AmendmentView amdView = new AmendmentView(amd);
////        draftDao.save(getSubjectEmployeeId(), dto.getTraveler(), amdView, dto.getTravelerDeptHeadEmpId());
//        dto.setAmendment(amdView);
//        return new ViewObjectResponse<>(dto);
//    }

    private TravelAppEditDto findApp(int userId) {
        return null;
//        return draftDao.find(userId)
//                .orElseThrow(() -> new InvalidRequestParamEx(String.valueOf(userId), "userId", "int",
//                        "No Unsubmitted travel app found with provided userId"));
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

