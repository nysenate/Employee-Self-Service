package gov.nysenate.ess.travel.application;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryTravelAppDao {

    private int idAutoInc = 1;
    private Map<Integer, Map<Integer, TravelApplication>> empIdToApps = new HashMap<>();

    public synchronized void saveTravelApplication(TravelApplication app) {
        Map<Integer, TravelApplication> empApps = empIdToApps.get(app.getTraveler().getEmployeeId());
        if (empApps == null) {
            empApps = new HashMap<>();
        }
        if (app.getId() == 0) {
            app.setSubmittedDateTime(LocalDateTime.now()); // TODO when/where should this be set?
            app.setId(idAutoInc);
            empApps.put(idAutoInc, app);
            idAutoInc++;
        }
        else {
            empApps.put((int) app.getId(), app);
        }
        empIdToApps.put(app.getTraveler().getEmployeeId(), empApps);
    }

    public TravelApplication getTravelAppById(int id) {
        for (Map<Integer, TravelApplication> empApps : empIdToApps.values()) {
            for (TravelApplication app : empApps.values()) {
                if (app.getId() == id) {
                    return app;
                }
            }
        }
        return null;
    }

    public List<TravelApplication> getTravelAppsByTravelerId(int travelerId) {
        return empIdToApps.get(travelerId) == null ? new ArrayList<>() : new ArrayList<>(empIdToApps.get(travelerId).values());
    }
}
