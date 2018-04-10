package gov.nysenate.ess.travel.application;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.utils.Dollars;
import gov.nysenate.ess.travel.accommodation.Accommodation;
import gov.nysenate.ess.travel.route.Route;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class TravelApplication {

    private long id;
    private Employee traveler;
    private Employee submitter;
    private ImmutableList<Accommodation> accommodations;
    private Route route;
    private String purposeOfTravel;
    private Dollars tolls;
    private Dollars parking;
    private Dollars alternate; // Bus, subway, and train.
    private Dollars registration;
    private LocalDateTime submittedDateTime; // DateTime application was submitted for approval.

    public TravelApplication(long id, Employee traveler, Employee submitter) {
        this.id = id;
        this.traveler = Objects.requireNonNull(traveler, "Travel Application requires non null traveler.");
        this.submitter = Objects.requireNonNull(submitter, "Travel Application requires non null submitter.");
        this.accommodations = ImmutableList.of();
        this.route = Route.EMPTY_ROUTE;
        this.purposeOfTravel = "";
        this.tolls = Dollars.ZERO;
        this.parking = Dollars.ZERO;
        this.alternate = Dollars.ZERO;
        this.registration = Dollars.ZERO;
    }

    /**
     * @return The travel start date or {@code null} if no destinations.
     */
    public LocalDate startDate() {
        return getAccommodations().size() > 0 ? getAccommodations().get(0).arrivalDate() : null;
    }

    /**
     * @return The travel end date or {@code null} if no destinations.
     */
    public LocalDate endDate() {
        return getAccommodations().size() > 0 ? getAccommodations().reverse().get(0).departureDate() : null;
    }

    /**
     * Get the total lodging allowance for all Accommodations in this application.
     * @return
     */
    public Dollars lodgingAllowance() {
        return getAccommodations().stream()
                .map(Accommodation::lodgingAllowance)
                .reduce(Dollars.ZERO, Dollars::add);
    }

    /**
     * Get the total meal allowance for all accommodations in this application.
     * @return
     */
    public Dollars mealAllowance() {
        return getAccommodations().stream()
                .map(Accommodation::mealAllowance)
                .reduce(Dollars.ZERO, Dollars::add);
    }

    /**
     * Get the total mileage allowance.
     * @return
     */
    public Dollars mileageAllowance() {
        return getRoute().mileageAllowance();
    }

    /**
     * Get the total allowance for this application.
     * @return
     */
    public Dollars totalAllowance() {
        return lodgingAllowance()
                .add(mealAllowance())
                .add(mileageAllowance())
                .add(getTolls())
                .add(getParking())
                .add(getAlternate())
                .add(getRegistration());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Employee getTraveler() {
        return traveler;
    }

    public Employee getSubmitter() {
        return submitter;
    }

    public ImmutableList<Accommodation> getAccommodations() {
        return accommodations;
    }

    public void setAccommodations(List<Accommodation> accommodations) {
        Objects.requireNonNull(accommodations);
        this.accommodations = ImmutableList.copyOf(accommodations);
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = Objects.requireNonNull(route);
    }

    public String getPurposeOfTravel() {
        return purposeOfTravel;
    }

    public void setPurposeOfTravel(String purposeOfTravel) {
        this.purposeOfTravel = Objects.requireNonNull(purposeOfTravel);
    }

    public Dollars getTolls() {
        return tolls;
    }

    public void setTolls(Dollars tolls) {
        this.tolls = Objects.requireNonNull(tolls);
    }

    public Dollars getParking() {
        return parking;
    }

    public void setParking(Dollars parking) {
        this.parking = Objects.requireNonNull(parking);
    }

    public Dollars getAlternate() {
        return alternate;
    }

    public void setAlternate(Dollars alternate) {
        this.alternate = Objects.requireNonNull(alternate);
    }

    public Dollars getRegistration() {
        return registration;
    }

    public void setRegistration(Dollars registration) {
        this.registration = Objects.requireNonNull(registration);
    }

    public LocalDateTime getSubmittedDateTime() {
        return submittedDateTime;
    }

    public void setSubmittedDateTime(LocalDateTime submittedDateTime) {
        this.submittedDateTime = Objects.requireNonNull(submittedDateTime);
    }
}
