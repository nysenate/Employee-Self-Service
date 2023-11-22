package gov.nysenate.ess.travel.request.draft;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.api.application.AmendmentView;
import gov.nysenate.ess.travel.api.application.TravelApplicationView;
import gov.nysenate.ess.travel.employee.TravelEmployeeView;
import gov.nysenate.ess.travel.request.app.TravelApplication;

import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class DraftView implements ViewObject {

    private int id;
    private int userEmpId;
    private TravelEmployeeView traveler;
    private TravelApplicationView travelApplication;
    private AmendmentView amendment; // TODO delete
    private String updatedDateTime;

    public DraftView() {
    }

    public DraftView(Draft draft) {
        this.id = draft.getId();
        this.userEmpId = draft.getUserEmpId();
        this.traveler = new TravelEmployeeView(draft.getTraveler());
        this.travelApplication = new TravelApplicationView(draft.getTravelApplication());
        this.amendment = new AmendmentView(draft.getTravelApplication().activeAmendment()); // TODO delete
        this.updatedDateTime = draft.getUpdatedDateTime().format(ISO_DATE_TIME);
    }

    public Draft toDraft() {
        Draft d = new Draft(id, userEmpId, traveler.toTravelEmployee());
        d.setTravelApplication(travelApplication.toTravelApplication());
        d.setUpdatedDateTime(LocalDateTime.parse(updatedDateTime, ISO_DATE_TIME));
        return d;
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

    public TravelEmployeeView getTraveler() {
        return traveler;
    }

    public void setTraveler(TravelEmployeeView traveler) {
        this.traveler = traveler;
    }

    public AmendmentView getAmendment() {
        return amendment;
    }

    public void setAmendment(AmendmentView amendment) {
        this.amendment = amendment;
    }

    public String getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(String updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    @Override
    public String getViewType() {
        return "draft";
    }
}
