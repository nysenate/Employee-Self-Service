package gov.nysenate.ess.travel.request.app;

import gov.nysenate.ess.travel.request.app.dao.TravelApplicationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TravelApplicationService {

    @Autowired private TravelApplicationDao travelApplicationDao;

    /**
     * Get Travel application by application id
     *
     * @return
     */
    public TravelApplication getTravelApplication(int appId) {
        return travelApplicationDao.selectTravelApplication(appId);
    }

    /**
     * Get a list of an employees travel applications this user has submitted or is the traveler.
     *
     * @return
     */
    public List<TravelApplication> selectTravelApplications(int userId) {
        return travelApplicationDao.selectTravelApplications(userId).stream()
                .filter(app -> app.getSubmittedDateTime() != null)
                .collect(Collectors.toList());
    }

    public void saveApplication(TravelApplication app) {
        travelApplicationDao.saveTravelApplication(app);
    }
}
