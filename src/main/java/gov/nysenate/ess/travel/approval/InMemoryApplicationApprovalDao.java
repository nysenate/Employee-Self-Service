package gov.nysenate.ess.travel.approval;

import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.application.TravelApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryApplicationApprovalDao {

    @Autowired private TravelApplicationService applicationService;
    private Map<Integer, ApplicationApproval> approvals;

    @PostConstruct
    private void init() {
        approvals = new HashMap<>();
        List<TravelApplication> apps = applicationService.selectTravelApplications(11168);
        for (TravelApplication app : apps) {
            approvals.put(approvals.size(), new ApplicationApproval(app));
        }
    }


}
