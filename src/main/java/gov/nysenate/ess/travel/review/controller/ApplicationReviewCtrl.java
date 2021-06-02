package gov.nysenate.ess.travel.review.controller;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.client.view.base.MapView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.authorization.permission.TravelPermissionBuilder;
import gov.nysenate.ess.travel.authorization.permission.TravelPermissionObject;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.authorization.role.TravelRoleView;
import gov.nysenate.ess.travel.review.ApplicationReview;
import gov.nysenate.ess.travel.review.dao.ApplicationReviewDao;
import gov.nysenate.ess.travel.review.ApplicationReviewService;
import gov.nysenate.ess.travel.review.view.ActionBodyView;
import gov.nysenate.ess.travel.review.view.ApplicationReviewView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/review")
public class ApplicationReviewCtrl extends BaseRestApiCtrl {

    @Autowired private ApplicationReviewService appReviewService;
    @Autowired private EmployeeInfoService employeeInfoService;

    @Autowired private ApplicationReviewDao reviewDao;

    /**
     * Get ApplicationReviews which have completed the review process.
     * @return
     */
    @RequestMapping(value = "/reconcile", method = RequestMethod.GET)
    public BaseResponse reconcileReviews(@RequestParam String from, @RequestParam String to) {
        checkPermission(new TravelPermissionBuilder()
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION_REVIEW)
                .forAllEmps()
                .forAction(RequestMethod.GET)
                .buildPermission());
        LocalDate fromDate = parseISODate(from, "from");
        LocalDate toDate = parseISODate(to, "to");
        List<ApplicationReview> toReconcile = appReviewService.appsToReconcile(fromDate, toDate);
        return ListViewResponse.of(toReconcile.stream()
                .map(ApplicationReviewView::new)
                .collect(Collectors.toSet()));
    }

    /**
     * Get app reviews which need to be reviewed by any of the given {@code roles}
     * <p>
     * This does not implement strict permission checking for this endpoint because it is
     * used in the badge service for all users. Instead of checking permissions,
     * this method will return an empty list if its not applicable to the user.
     */
    @RequestMapping(value = "/pending", method = RequestMethod.GET)
    public BaseResponse getPendingReviews() {
        Employee employee = employeeInfoService.getEmployee(getSubjectEmployeeId());

        Map<TravelRole, List<ApplicationReview>> pendingReviews = appReviewService.pendingReviews(employee);
        Map<TravelRoleView, ListView<ApplicationReviewView>> views = new HashMap<>();
        for (Map.Entry<TravelRole, List<ApplicationReview>> entry : pendingReviews.entrySet()) {
            List<ApplicationReviewView> appReviewViews = entry.getValue().stream()
                    .map(ApplicationReviewView::new)
                    .collect(Collectors.toList());
            views.put(new TravelRoleView(entry.getKey()), ListView.of(appReviewViews));
        }

        return new ViewObjectResponse<>(MapView.of(views));
    }

    @RequestMapping(value = "/shared", method = RequestMethod.GET)
    public BaseResponse getSharedReviews() {
        // Check the user is allowed to view application reviews
        checkPermission(new TravelPermissionBuilder()
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION_REVIEW)
                .forAllEmps()
                .forAction(RequestMethod.GET)
                .buildPermission());

        Set<ApplicationReview> pendingReviews = new HashSet<>(appReviewService.pendingSharedAppReviews());
        return ListViewResponse.of(pendingReviews.stream()
                .map(ApplicationReviewView::new)
                .collect(Collectors.toList()));
    }

    /**
     * @return A list of ApplicationReviews where the user has performed an action.
     */
    @RequestMapping(value = "/history")
    public BaseResponse reviewHistory() {
        Employee emp = employeeInfoService.getEmployee(getSubjectEmployeeId());
        List<ApplicationReview> reviews = appReviewService.appReviewHistory(emp);
        return ListViewResponse.of(reviews.stream()
                .map(ApplicationReviewView::new)
                .collect(Collectors.toList()));
    }

    /**
     * Adds an Approval to an ApplicationReview
     *
     * @param appReviewId The id of the ApplicationReview to approve.
     * @param role        The role that is approving this application.
     * @param body        Additional details about the approval.
     */
    @RequestMapping(value = "/{appReviewId}/approve", method = RequestMethod.POST)
    public BaseResponse approveApplication(@PathVariable int appReviewId,
                                           @RequestParam String role,
                                           @RequestBody ActionBodyView body) {
        TravelRole r = TravelRole.of(role);
        Employee employee = employeeInfoService.getEmployee(getSubjectEmployeeId());
        ApplicationReview appReview = appReviewService.getApplicationReview(appReviewId);

        checkPermission(new TravelPermissionBuilder()
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION_REVIEW)
                .forEmpId(appReview.application().getTraveler().getEmployeeId())
                .forAction(RequestMethod.POST)
                .buildPermission());

        appReviewService.approveApplication(appReview, employee, body.getNotes(), r);
        return new ViewObjectResponse<>(new ApplicationReviewView(appReview));
    }

    /**
     * Adds a Disapproval to an ApplicationReview
     *
     * @param appReviewId The id of the ApplicationReview to approve.
     * @param role        The role that is disapproving.
     * @param body        Contains the disapproval reason.
     */
    @RequestMapping(value = "/{appReviewId}/disapprove", method = RequestMethod.POST)
    public BaseResponse disapproveApplication(@PathVariable int appReviewId,
                                              @RequestParam String role,
                                              @RequestBody ActionBodyView body) {
        if (body.getNotes() == null || body.getNotes().trim().isEmpty()) {
            throw new InvalidRequestParamEx(body.getNotes(), "notes", "String", "not null & not empty");
        }

        TravelRole r = TravelRole.of(role);
        Employee employee = employeeInfoService.getEmployee(getSubjectEmployeeId());
        ApplicationReview appReview = appReviewService.getApplicationReview(appReviewId);

        checkPermission(new TravelPermissionBuilder()
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION_REVIEW)
                .forEmpId(appReview.application().getTraveler().getEmployeeId())
                .forAction(RequestMethod.POST)
                .buildPermission());

        appReviewService.disapproveApplication(appReview, employee, body.getNotes(), r);
        return new ViewObjectResponse<>(new ApplicationReviewView(appReview));
    }

    /**
     * Update the isShared filed of an ApplicationReview.
     *
     * @param appReviewId The id of the review to update.
     * @param isShared    The new value for isShared.
     * @return
     */
    @RequestMapping(value = "/{appReviewId}", method = RequestMethod.POST)
    public BaseResponse updateAppReview(@PathVariable int appReviewId,
                                        @RequestParam boolean isShared) {
        ApplicationReview appReview = appReviewService.getApplicationReview(appReviewId);
        checkPermission(new TravelPermissionBuilder()
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION_REVIEW)
                .forEmpId(appReview.application().getTraveler().getEmployeeId())
                .forAction(RequestMethod.POST)
                .buildPermission());

        appReview = appReviewService.updateIsShared(appReview, isShared);
        return new ViewObjectResponse<>(new ApplicationReviewView(appReview));
    }
}
