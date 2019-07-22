package gov.nysenate.ess.travel.review;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.authorization.permission.TravelPermissionBuilder;
import gov.nysenate.ess.travel.authorization.permission.TravelPermissionObject;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.authorization.role.TravelRoleFactory;
import gov.nysenate.ess.travel.authorization.role.TravelRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/review")
public class ApplicationReviewCtrl extends BaseRestApiCtrl {

    @Autowired private ApplicationReviewService appReviewService;
    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private TravelRoleFactory travelRoleFactory;

    /**
     * Get app reviews which need to be reviewed by any of the given {@code roles}
     * <p>
     * This does not implement strict permission checking for this endpoint because it is
     * used in the badge service for all users. Instead of checking permissions,
     * this method will return an empty list if its not applicable to the user.
     *
     * @param roles A list of TravelRole's.
     */
    @RequestMapping(value = "/pending", method = RequestMethod.GET)
    public BaseResponse getPendingReviews(@RequestParam(required = false) List<String> roles) {
        List<TravelRole> roleList = roles == null || roles.isEmpty()
                ? new ArrayList<>()
                : roles.stream().map(TravelRole::of).collect(Collectors.toList());
        Employee employee = employeeInfoService.getEmployee(getSubjectEmployeeId());

        List<ApplicationReview> pendingReviews = new ArrayList<>();
        for (TravelRole r : roleList) {
            pendingReviews.addAll(appReviewService.pendingAppReviewsForEmpWithRole(employee, r));
        }

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
        TravelRoles roles = travelRoleFactory.travelRolesForEmp(emp);

        Set<ApplicationReview> reviews = new HashSet<>();
        for (TravelRole role : roles.all()) {
            reviews.addAll(appReviewService.appReviewHistoryForRole(role));
        }
        return ListViewResponse.of(reviews.stream()
                .map(ApplicationReviewView::new)
                .collect(Collectors.toList()));
    }

    /**
     * Adds an Approval to an ApplicationReview
     * @param appReviewId The id of the ApplicationReview to approve.
     * @param role The role that is approving this application.
     * @param body Additional details about the approval.
     */
    @RequestMapping(value = "/{appReviewId}/approve", method = RequestMethod.POST)
    public BaseResponse approveApplication(@PathVariable int appReviewId,
                                           @RequestParam String role,
                                           @RequestBody ActionBodyView body) {
        TravelRole r = TravelRole.of(role);
        Employee employee = employeeInfoService.getEmployee(getSubjectEmployeeId());
        ApplicationReview appReview = appReviewService.getApplicationReview(appReviewId);

        checkPermission(new TravelPermissionBuilder()
                .forEmpId(appReview.application().getTraveler().getEmployeeId())
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION_REVIEW)
                .forAction(RequestMethod.POST)
                .buildPermission());

        appReviewService.approveApplication(appReview, employee, body.getNotes(), r, body.isDiscussionRequested());
        return new ViewObjectResponse<>(new ApplicationReviewView(appReview));
    }

    /**
     * Adds a Disapproval to an ApplicationReview
     * @param appReviewId The id of the ApplicationReview to approve.
     * @param role The role that is disapproving.
     * @param body Contains the disapproval reason.
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
                .forEmpId(appReview.application().getTraveler().getEmployeeId())
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION_REVIEW)
                .forAction(RequestMethod.POST)
                .buildPermission());

        appReviewService.disapproveApplication(appReview, employee, body.getNotes(), r);
        return new ViewObjectResponse<>(new ApplicationReviewView(appReview));
    }
}
