package gov.nysenate.ess.travel.application.dao;

import gov.nysenate.ess.travel.application.model.TravelApplication;
import gov.nysenate.ess.travel.application.model.TravelApplicationStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InMemoryTravelApplicationDao {

    private List<TravelApplication> travelApplications;

    public InMemoryTravelApplicationDao() {
        this.travelApplications = new ArrayList<>();
    }

    public void saveTravelApplication(TravelApplication travelApplication) {
        travelApplications.add(travelApplication);
    }

    public List<TravelApplication> searchTravelApplications(int empId, TravelApplicationStatus status) {
       return getTravelApplicationsByEmpId(empId).stream()
               .filter(getTravelApplicationsByStatus(status)::contains)
               .collect(Collectors.toList());
    }

    public List<TravelApplication> getTravelApplicationsByEmpId(int empId) {
        return travelApplications.stream()
                .filter(r -> r.getApplicant().getEmployeeId() == empId)
                .collect(Collectors.toList());
    }

    public List<TravelApplication> getTravelApplicationsByStatus(TravelApplicationStatus status) {
        return travelApplications.stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }
}
