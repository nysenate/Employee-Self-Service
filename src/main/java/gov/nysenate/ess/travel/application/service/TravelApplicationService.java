package gov.nysenate.ess.travel.application.service;

import gov.nysenate.ess.travel.application.TravelApplicationValidator;
import gov.nysenate.ess.travel.application.dao.InMemoryTravelApplicationDao;
import gov.nysenate.ess.travel.application.model.TravelApplication;
import gov.nysenate.ess.travel.application.model.TravelApplicationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TravelApplicationService {

    private InMemoryTravelApplicationDao travelAppDao;
    private TravelApplicationValidator validator;

    @Autowired
    public TravelApplicationService(InMemoryTravelApplicationDao travelAppDao, TravelApplicationValidator validator) {
        this.travelAppDao = travelAppDao;
        this.validator = validator;
    }

    public synchronized TravelApplication submitTravelApplication(TravelApplication travelApplication) {
        // validate
        // save
        // return updated app

        // DO all that in a separate object, ctrl should only validate permissions and request body.

        // Save app to db.
        return null;
    }

    public void saveTravelApplication(TravelApplication travelApplication) {
        travelAppDao.saveTravelApplication(travelApplication);
    }

    /**
     * Get travel applications for the given empId and status.
     */
    public List<TravelApplication> searchTravelApplications(int empId, TravelApplicationStatus status) {
        return travelAppDao.searchTravelApplications(empId, status);
    }

    public List<TravelApplication> getTravelApplicationsByEmpId(int empId) {
        return travelAppDao.getTravelApplicationsByEmpId(empId);
    }

    public List<TravelApplication> activeApplications(int empId) {
        return travelAppDao.activeApplications(empId, LocalDate.now());
    }
}
