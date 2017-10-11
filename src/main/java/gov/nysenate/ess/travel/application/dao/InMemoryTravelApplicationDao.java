package gov.nysenate.ess.travel.application.dao;

import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.application.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InMemoryTravelApplicationDao {

    private List<TravelApplication> travelApplications;
    private EmployeeInfoService employeeInfoService;

    @Autowired
    public InMemoryTravelApplicationDao(EmployeeInfoService employeeInfoService) {
        this.travelApplications = new ArrayList<>();
        this.employeeInfoService = employeeInfoService;
        insertStubApplications();
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

    public List<TravelApplication> activeApplications(int empId, LocalDate date) {
        return getTravelApplicationsByEmpId(empId).stream()
                .filter(ta -> ta.travelEndDate().isAfter(date.minusDays(1)))
                .collect(Collectors.toList());
    }

    private void insertStubApplications() {
        int id = 1;
        for (int i = 0; i < 20; i++) {
            if (i < 4) {
                travelApplications.add(stubApplication(id, TravelApplicationStatus.SUBMITTED));
            }
            else if (i < 6) {
                travelApplications.add(stubApplication(id, TravelApplicationStatus.RETURNED));
            }
            else if (i < 10) {
                travelApplications.add(stubApplication(id, TravelApplicationStatus.REJECTED));
            }
            else {
                travelApplications.add(stubApplication(id, TravelApplicationStatus.APPROVED));
            }
            id++;
        }
    }

    private TravelApplication stubApplication(int id, TravelApplicationStatus status) {
        return TravelApplication.Builder()
                .setId(id)
                .setApplicant(employeeInfoService.getEmployee(11168))
                .setGsaAllowance(randomGsaAllowance())
                .setTransportationAllowance(randomTransportationAllowance())
                .setItinerary(itinerary())
                .setModeOfTransportation(ModeOfTransportation.PERSONAL_AUTO)
                .setStatus(status)
                .setCreatedDateTime(LocalDateTime.now())
                .setModifiedDateTime(LocalDateTime.now())
                .build();
    }

    private Itinerary itinerary() {
        List<TravelDestination> tds = new ArrayList<>();
        LocalDate arrival = LocalDate.now().minusDays(10).plusDays((int)randomWithRange(0, 30));
        tds.add(new TravelDestination(arrival, arrival.plusDays((int)randomWithRange(1, 5)),
                new Address("100 Washington Ave", "Albany", "NY", "12222")));
        return new Itinerary(new Address("88 Central Ave", "Albany", "NY", "12222"), tds);
    }

    private GsaAllowance randomGsaAllowance() {
        return new GsaAllowance(String.valueOf(randomWithRange(0, 25.00)),
                String.valueOf(randomWithRange(0, 375.00)),
                String.valueOf(randomWithRange(0, 12.00)));
    }

    private TransportationAllowance randomTransportationAllowance() {
        return new TransportationAllowance(String.valueOf(randomWithRange(0, 150.00)),
                String.valueOf(randomWithRange(0, 40.00)));
    }

    private double randomWithRange(double min, double max)
    {
        double range = Math.abs(max - min);
        return (Math.random() * range) + (min <= max ? min : max);
    }
}
