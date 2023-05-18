package gov.nysenate.ess.travel.request.draft;

import gov.nysenate.ess.travel.employee.TravelEmployee;
import gov.nysenate.ess.travel.request.amendment.Amendment;

import java.time.LocalDateTime;

public class Draft {

    private int id;
    private int userEmpId;
    private TravelEmployee traveler;
    private Amendment amendment;
    private LocalDateTime updatedDateTime;

    public Draft(TravelEmployee user) {
        this(0, user.getEmployeeId(), user, new Amendment.Builder().build(), LocalDateTime.now());
    }

    public Draft(int id, int userEmpId, TravelEmployee traveler, Amendment amendment, LocalDateTime updatedDateTime) {
        this.id = id;
        this.userEmpId = userEmpId;
        this.traveler = traveler;
        this.amendment = amendment;
        this.updatedDateTime = updatedDateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserEmpId() {
        return userEmpId;
    }

    public void setUserEmpId(int userEmpId) {
        this.userEmpId = userEmpId;
    }

    public TravelEmployee getTraveler() {
        return traveler;
    }

    public void setTraveler(TravelEmployee traveler) {
        this.traveler = traveler;
    }

    public Amendment getAmendment() {
        return amendment;
    }

    public void setAmendment(Amendment amendment) {
        this.amendment = amendment;
    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(LocalDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }
}
