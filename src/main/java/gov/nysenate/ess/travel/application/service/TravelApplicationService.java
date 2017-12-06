package gov.nysenate.ess.travel.application.service;

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

    @Autowired
    public TravelApplicationService(InMemoryTravelApplicationDao travelAppDao) {
        this.travelAppDao = travelAppDao;
    }

    public synchronized TravelApplication submitTravelApplication(TravelApplication travelApplication) {
        travelAppDao.saveTravelApplication(travelApplication);
        return travelApplication;
    }

    public void saveTravelApplication(TravelApplication travelApplication) {
        travelAppDao.saveTravelApplication(travelApplication);
    }

    public List<TravelApplication> getTravelApplicationsByEmpId(int empId) {
        return travelAppDao.getTravelApplicationsByEmpId(empId);
    }

}
