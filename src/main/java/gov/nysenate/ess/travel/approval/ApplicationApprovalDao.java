package gov.nysenate.ess.travel.approval;

import gov.nysenate.ess.travel.authorization.role.TravelRole;

import java.util.List;

public interface ApplicationApprovalDao {

    void saveApplicationApproval(ApplicationApproval appApproval);

    List<ApplicationApproval> selectApprovalsByNextRole(TravelRole nextReviewerRole);

    ApplicationApproval selectApprovalById(int approvalId);
}
