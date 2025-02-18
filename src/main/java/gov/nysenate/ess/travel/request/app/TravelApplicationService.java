package gov.nysenate.ess.travel.request.app;

import gov.nysenate.ess.travel.api.application.TravelApplicationStatusView;
import gov.nysenate.ess.travel.api.application.TravelStatusCountView;
import gov.nysenate.ess.travel.request.app.dao.TravelApplicationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    /**
     * Get a list of all travel applications.
     *
     * @return
     */
    public List<TravelStatusCountView> selectAllTravelApplications(LocalDateTime from, LocalDateTime to) {
        List<TravelApplication> travelAllApplications = travelApplicationDao.selectAllApplications().stream()
                                    .filter(app -> app.getSubmittedDateTime() != null)
                                    .collect(Collectors.toList());

        List<TravelApplication> filteredApplications = travelAllApplications.stream()
                .filter(app -> !app.getSubmittedDateTime().isBefore(from) && !app.getSubmittedDateTime().isAfter(to))
                .toList();

        Map<String, Long> statusCountMap = filteredApplications.stream()
                .collect(Collectors.groupingBy(app -> app.getStatus().status().label(), Collectors.counting()));

        return statusCountMap.entrySet().stream()
                .map(entry ->
                        new TravelStatusCountView(entry.getKey(), entry.getValue().intValue()))
                .toList();

    }

    public void saveApplication(TravelApplication app) {
        travelApplicationDao.saveTravelApplication(app);
    }

    public void updateApplicationStatus(int appId, TravelApplicationStatus status) {
        travelApplicationDao.updateTravelApplicationStatus(appId, status);
    }
}
