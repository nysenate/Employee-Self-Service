package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TravelApplicationService {

    @Autowired private TravelApplicationDao applicationDao;
    @Autowired private EmployeeInfoService employeeInfoService;

    /**
     * Inserts a travel application into the backing store.
     * @param app
     * @return
     */
    public TravelApplication insertTravelApplication(TravelApplication app) {
        LocalDateTime modifiedDateTime = LocalDateTime.now();
        app.setSubmittedDateTime(modifiedDateTime);
        app.setModifiedDateTime(modifiedDateTime);
        app.setModifiedBy(app.getSubmitter()); // submitter is also the first modifier.
        applicationDao.insertTravelApplication(app);
        return app;
    }

    /**
     * Get Travel application by application id
     * @return
     */
    public TravelApplication getTravelApplication(UUID appId) {
        return applicationDao.getTravelApplication(appId);
    }

    /**
     * Get a list of an employees travel applications.
     * @return
     */
    public List<TravelApplication> getActiveTravelApplications(int travelerId) {
        return applicationDao.getActiveTravelApplications(travelerId);
    }

    /**
     * Updates a travel application in the backing store.
     // TODO Not currently used. Should implement Pessimistic locking?
     */
    public TravelApplication updateTravelApplication(TravelApplication app, int modifiedByEmpId) {
        LocalDateTime modifiedDateTime = LocalDateTime.now();
        app.setModifiedDateTime(modifiedDateTime);
        app.setModifiedBy(employeeInfoService.getEmployee(modifiedByEmpId));
        app.setVersionId(UUID.randomUUID());
        return app;
    }
}
