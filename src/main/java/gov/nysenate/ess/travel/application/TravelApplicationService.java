package gov.nysenate.ess.travel.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TravelApplicationService {

    @Autowired private TravelApplicationDao applicationDao;

    /**
     * Get Travel application by application id
     *
     * @return
     */
    public TravelApplication getTravelApplication(int appId) {
        return applicationDao.selectTravelApplication(appId);
    }

    /**
     * Get a list of an employees travel applications this user has submitted or is the traveler.
     *
     * @return
     */
    public List<TravelApplication> selectTravelApplications(int userId) {
        return applicationDao.selectTravelApplications(userId).stream()
                .filter(app -> app.getSubmittedDateTime() != null)
                .collect(Collectors.toList());
    }

    public void saveApplication(TravelApplication app) {
        applicationDao.saveTravelApplication(app);
    }
}
