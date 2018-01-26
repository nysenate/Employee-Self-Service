package gov.nysenate.ess.travel.application.dao;

import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.allowance.gsa.model.LodgingAllowance;
import gov.nysenate.ess.travel.allowance.gsa.model.MealAllowance;
import gov.nysenate.ess.travel.allowance.mileage.model.MileageAllowance;
import gov.nysenate.ess.travel.application.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    public List<TravelApplication> getTravelApplicationsByEmpId(int empId) {
        return travelApplications.stream()
                .filter(r -> r.getTraveler().getEmployeeId() == empId)
                .collect(Collectors.toList());
    }

    public List<TravelApplication> getTravelApplicationsByStatus(TravelApplicationStatus status) {
        return travelApplications.stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }

    private void insertStubApplications() {
        int id = 1;
        for (int i = 0; i < 3; i++) {
            if (i < 2) {
                travelApplications.add(stubApplication(id, TravelApplicationStatus.SUBMITTED));
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
                .setTraveler(employeeInfoService.getEmployee(11168))
                .setAllowances(randomAllowances())
                .setItinerary(itinerary())
                .setStatus(status)
                .setCreatedBy(employeeInfoService.getEmployee(11168))
                .setCreatedDateTime(LocalDateTime.now())
                .setModifiedDateTime(LocalDateTime.now())
                .setPurposeOfTravel("Went somewhere for something....maybe a conference?  Who knows.")
                .build();
    }

    private Itinerary itinerary() {
        LocalDate arrival = LocalDate.now().minusDays(10).plusDays((int)randomWithRange(0, 30));
        TravelDestination dest = new TravelDestination(arrival, arrival.plusDays((int)randomWithRange(1, 5)),
                new Address("100 Washington Ave", "Albany", "NY", "12222"));
        return new Itinerary(new Address("88 Central Ave", "Albany", "NY", "12222"))
                .addDestination(dest, new TravelDestinationOptions(ModeOfTransportation.PERSONAL_AUTO));
    }

    private TravelAllowances randomAllowances() {
        return new TravelAllowances(new MealAllowance(), new LodgingAllowance(), new MileageAllowance(new BigDecimal("0.50")),
                String.valueOf(randomWithRange(0, 40.00)), String.valueOf(randomWithRange(0, 150.00)),
                String.valueOf(randomWithRange(0, 150.00)), String.valueOf(randomWithRange(0, 150.00)));
    }

    private double randomWithRange(double min, double max)
    {
        double range = Math.abs(max - min);
        return (Math.random() * range) + (min <= max ? min : max);
    }
}
