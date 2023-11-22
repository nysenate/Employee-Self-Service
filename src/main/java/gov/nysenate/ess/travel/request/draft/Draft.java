package gov.nysenate.ess.travel.request.draft;

import gov.nysenate.ess.travel.employee.TravelEmployee;
import gov.nysenate.ess.travel.request.amendment.Amendment;
import gov.nysenate.ess.travel.request.app.AppStatus;
import gov.nysenate.ess.travel.request.app.TravelApplication;

import java.time.LocalDateTime;

public class Draft {

    private int id;
    private int userEmpId; // The employee id of the user who created this draft.
    private TravelEmployee traveler;
    private TravelApplication travelApplication;
    private LocalDateTime updatedDateTime; // DateTime this was last saved into the database.

    public Draft(int id, int userEmpId, TravelEmployee traveler) {
        this.id = id;
        this.userEmpId = userEmpId;
        this.traveler = traveler;
        this.travelApplication = new TravelApplication();
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

    public TravelApplication getTravelApplication() {
        return travelApplication;
    }

    public void setTravelApplication(TravelApplication travelApplication) {
        this.travelApplication = travelApplication;
    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(LocalDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }
}
