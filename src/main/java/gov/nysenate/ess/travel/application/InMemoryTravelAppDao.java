package gov.nysenate.ess.travel.application;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryTravelAppDao {

    private int idAutoInc = 1;
    private Map<Integer, Map<UUID, TravelApplication>> empIdToApps = new HashMap<>();
    private int uncompleteId = 1;
    private Map<Integer, TravelApplicationView> uncompletedTravelApps = new HashMap<>();

    public synchronized void saveTravelApplication(TravelApplication app) {
        Map<UUID, TravelApplication> empApps = empIdToApps.get(app.getTraveler().getEmployeeId());
        if (empApps == null) {
            empApps = new HashMap<>();
        }
//        if (app.getId() == 0) {
//            app.setSubmittedDateTime(LocalDateTime.now()); // TODO when/where should this be set?
//            app.setId(idAutoInc);
//            empApps.put(idAutoInc, app);
//            idAutoInc++;
//        }
//        else {
            empApps.put(app.getId(), app);
//        }
        empIdToApps.put(app.getTraveler().getEmployeeId(), empApps);
    }

    public TravelApplication getTravelAppById(UUID id) {
        for (Map<UUID, TravelApplication> empApps : empIdToApps.values()) {
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

    public TravelApplicationView saveUncompleteTravelApp(TravelApplicationView app) {
//        if (app.getId() == 0) {
//            app.setId(uncompleteId);
//            uncompleteId++;
//        }
        uncompletedTravelApps.put(app.getTraveler().getEmployeeId(), app);
        return app;
    }

    public TravelApplicationView getUncompletedAppByEmpId(int empId) {
        return uncompletedTravelApps.get(empId);
    }

    public TravelApplicationView getUncompletedAppById(UUID id) {
        return uncompletedTravelApps.values().stream().filter(a -> UUID.fromString(a.getId()).equals(id)).collect(Collectors.toList()).get(0);
    }

    /**
     * Deletes a uncompleted travel app for the given employee id.
     * @param empId
     */
    public void deleteApplication(int empId) {
        uncompletedTravelApps.remove(empId);
    }
}
