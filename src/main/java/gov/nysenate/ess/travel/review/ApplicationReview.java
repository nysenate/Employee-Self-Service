package gov.nysenate.ess.travel.review;

import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.review.strategy.*;
import gov.nysenate.ess.travel.authorization.role.TravelRole;

import java.util.ArrayList;
import java.util.List;

/**
 * The entire review process for a single {@link TravelApplication}.
 */
public class ApplicationReview {

    private int appReviewId;
    private TravelApplication application;
    private TravelRole travelerRole;
    private List<Action> actions;
    private ReviewerStrategy reviewerStrategy;

    public ApplicationReview(int appReviewId, TravelApplication application, TravelRole travelerRole, List<Action> actions) {
        this.appReviewId = appReviewId;
        this.application = application;
        this.travelerRole = travelerRole;
        this.actions = actions;

        if (travelerRole == TravelRole.NONE) {
            reviewerStrategy = new RegularReviewerStrategy();
        }
        else if (travelerRole == TravelRole.MAJORITY_LEADER) {
            reviewerStrategy = new MajReviewerStrategy();
        }
        else if (application.getTraveler().isSenator()) {
            reviewerStrategy = new SenatorReviewerStrategy();
        }
        else if (travelerRole == TravelRole.SUPERVISOR) {
            reviewerStrategy = new SupervisorReviewerStrategy();
        }
        else if (travelerRole == TravelRole.DEPUTY_EXECUTIVE_ASSISTANT) {
            reviewerStrategy = new DeaReviewerStrategy();
        }
        else if (travelerRole == TravelRole.SECRETARY_OF_THE_SENATE) {
            reviewerStrategy = new SosReviewerStrategy();
        }
    }

    public ApplicationReview(TravelApplication application, TravelRole travelerRole) {
        this(0, application, travelerRole, new ArrayList<>());
    }

    public void addAction(Action action) {
        //TODO
        // Verify correct role is doing the action.
        // Verify this approval can receive mroe actions
        actions.add(action);
    }

//    public boolean addApproval(Employee approver, TravelRole approverRole, String notes, LocalDateTime dateTime) {
//        if (!nextApprover().isPresent()) {
//            // TODO: Cannot approve anymore!
//        }
//        if (approverRole != nextApprover().get()) {
//            // TODO: Incorrect role is approving!
//        }
//        return actions.add(new Action(approver, approverRole, notes, dateTime));
//    }

    /**
     * Returns the role which needs to review the application next.
     * If the application has been disapproved there is no need to continue the review workflow.
     */
    public TravelRole nextReviewerRole() {
        if (application.isDisapproved()) {
            return TravelRole.NONE;
        }
        else {
            return reviewerStrategy.after(previousReviewerRole());
        }
    }

    public TravelApplication application() {
        return application;
    }

    public List<Action> actions() {
        return actions;
    }

    public TravelRole travelerRole() {
        return travelerRole;
    }

    int getAppReviewId() {
        return appReviewId;
    }

    void setAppReviewId(int appReviewId) {
        this.appReviewId = appReviewId;
    }

    private TravelRole previousReviewerRole() {
        if (actions.isEmpty()) {
            return null;
        }
        return actions.get(actions.size() - 1).role();
    }
}
