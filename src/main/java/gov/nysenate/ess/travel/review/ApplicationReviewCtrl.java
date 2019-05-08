package gov.nysenate.ess.travel.review;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/review")
public class ApplicationReviewCtrl extends BaseRestApiCtrl {

    @Autowired private ApplicationReviewService appReviewService;
    @Autowired private EmployeeInfoService employeeInfoService;

    /**
     * Get app reviews which need review by the logged in user.
     */
    @RequestMapping(value = "/pending", method = RequestMethod.GET)
    public BaseResponse getPendingReviews() {
        TravelRole role = checkSubjectRole();
        Employee employee = employeeInfoService.getEmployee(getSubjectEmployeeId());
        List<ApplicationReview> pendingReviews = appReviewService.pendingAppReviewsForEmpWithRole(employee, role);
        return ListViewResponse.of(pendingReviews.stream()
                .map(ApplicationReviewView::new)
                .collect(Collectors.toList()));
    }

    /**
     * @return A list of ApplicationReviews where the logged in user performed a review action.
     */
    @RequestMapping(value = "/history")
    public BaseResponse reviewHistory() {
        List<ApplicationReview> reviewHistory = appReviewService.appReviewHistoryForEmp(getSubjectEmployeeId());
        return ListViewResponse.of(reviewHistory.stream()
                .map(ApplicationReviewView::new)
                .collect(Collectors.toList()));
    }

    @RequestMapping(value = "/{appReviewId}/approve", method = RequestMethod.POST)
    public BaseResponse approveApplication(@PathVariable int appReviewId) {
        TravelRole role = checkSubjectRole();
        Employee employee = employeeInfoService.getEmployee(getSubjectEmployeeId());
        ApplicationReview appReview = appReviewService.getApplicationReview(appReviewId);
        appReviewService.approveApplication(appReview, employee, role);
        return new ViewObjectResponse<>(new ApplicationReviewView(appReview));
    }

    @RequestMapping(value = "/{appReviewId}/disapprove", method = RequestMethod.POST)
    public BaseResponse disapproveApplication(@PathVariable int appReviewId) {
        TravelRole role = checkSubjectRole();
        Employee employee = employeeInfoService.getEmployee(getSubjectEmployeeId());
        ApplicationReview appReview = appReviewService.getApplicationReview(appReviewId);
        appReviewService.disapproveApplication(appReview, employee, role);
        return new ViewObjectResponse<>(new ApplicationReviewView(appReview));
    }

    /**
     * @return The TravelRole assigned to the user.
     */
    private TravelRole checkSubjectRole() {
        TravelRole role = TravelRole.NONE;
        if (getSubject().hasRole(TravelRole.SUPERVISOR.name())) {
            role = TravelRole.SUPERVISOR;
        }
        if (getSubject().hasRole(TravelRole.DEPUTY_EXECUTIVE_ASSISTANT.name())) {
            role = TravelRole.DEPUTY_EXECUTIVE_ASSISTANT;
        }
        if (getSubject().hasRole(TravelRole.SECRETARY_OF_THE_SENATE.name())) {
            role = TravelRole.SECRETARY_OF_THE_SENATE;
        }
        if (getSubject().hasRole(TravelRole.MAJORITY_LEADER.name())) {
            role = TravelRole.MAJORITY_LEADER;
        }
        return role;
    }
}
