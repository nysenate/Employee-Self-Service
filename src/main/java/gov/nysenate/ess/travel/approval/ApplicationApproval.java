package gov.nysenate.ess.travel.approval;

import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.authorization.role.TravelRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The entire approval process for a single {@link TravelApplication}.
 */
public class ApplicationApproval {

    private int approvalId;
    private TravelApplication application;
    private TravelRole travelerRole;
    private List<Action> actions;

    public ApplicationApproval(int approvalId, TravelApplication application, TravelRole travelerRole, List<Action> actions) {
        this.approvalId = approvalId;
        this.application = application;
        this.travelerRole = travelerRole;
        this.actions = actions;
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

    public Optional<TravelRole> nextReviewerRole() {
        if (actions.isEmpty()) {
            return Optional.of(TravelRole.SUPERVISOR);
        }
        return previousReviewerRole().flatMap(TravelRole::next);
    }

    public Optional<TravelRole> previousReviewerRole() {
        if (actions.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(actions.get(actions.size() - 1).role());
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
}
