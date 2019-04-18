package gov.nysenate.ess.travel.approval;

import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.approval.reviewer.*;
import gov.nysenate.ess.travel.authorization.role.TravelRole;

import java.util.ArrayList;
import java.util.List;

/**
 * The entire approval process for a single {@link TravelApplication}.
 */
public class ApplicationApproval {

    private int approvalId;
    private TravelApplication application;
    private TravelRole travelerRole;
    private List<Action> actions;
    private ReviewerStrategy reviewerStrategy;

    public ApplicationApproval(int approvalId, TravelApplication application, TravelRole travelerRole, List<Action> actions) {
        this.approvalId = approvalId;
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

    public ApplicationApproval(TravelApplication application, TravelRole travelerRole) {
        this(0, application, travelerRole, new ArrayList<>());
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
     * Returns the role which needs to approve the application next.
     */
    public TravelRole nextReviewerRole() {
        return reviewerStrategy.after(previousReviewerRole());
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

    int getApprovalId() {
        return approvalId;
    }

    void setApprovalId(int approvalId) {
        this.approvalId = approvalId;
    }

    private TravelRole previousReviewerRole() {
        if (actions.isEmpty()) {
            return null;
        }
        return actions.get(actions.size() - 1).role();
    }
}
