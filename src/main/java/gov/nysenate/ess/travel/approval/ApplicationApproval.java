package gov.nysenate.ess.travel.approval;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.TravelRole;
import gov.nysenate.ess.travel.application.TravelApplication;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The entire approval process for a single {@link TravelApplication}.
 */
public class ApplicationApproval {

    private int approvalId;
    private TravelApplication application;
    private List<Approval> approvals;

    public ApplicationApproval(TravelApplication application) {
        this.application = application;
        this.approvals = new ArrayList<>();
    }

    /**
     *
     * @param approver
     * @param approverRole
     * @param notes
     * @param dateTime
     * @return true if the approval is successfully added.
     */
    public boolean addApproval(Employee approver, TravelRole approverRole, String notes, LocalDateTime dateTime) {
        if (!nextApprover().isPresent()) {
            // TODO: Cannot approve anymore!
        }
        if (approverRole != nextApprover().get()) {
            // TODO: Incorrect role is approving!
        }
        return approvals.add(new Approval(approver, approverRole, notes, dateTime));
    }

    public Optional<TravelRole> nextApprover() {
        if (approvals.isEmpty()) {
            return Optional.of(TravelRole.DEPARTMENT_HEAD);
        }
        return previousApprover().flatMap(TravelRole::next);
    }

    public Optional<TravelRole> previousApprover() {
        if (approvals.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(approvals.get(approvals.size() - 1).getRole());
    }
}
