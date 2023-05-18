package gov.nysenate.ess.travel.request.draft;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.api.application.AmendmentView;
import gov.nysenate.ess.travel.employee.TravelEmployeeView;

import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class DraftView implements ViewObject {

    private int id;
    private int userEmpId;
    private TravelEmployeeView traveler;
    private AmendmentView amendment;
    private String updatedDateTime;

    public DraftView() {
    }

    public DraftView(Draft draft) {
        this.id = draft.getId();
        this.userEmpId = draft.getUserEmpId();
        this.traveler = new TravelEmployeeView(draft.getTraveler());
        this.amendment = new AmendmentView(draft.getAmendment());
        this.updatedDateTime = draft.getUpdatedDateTime().format(ISO_DATE_TIME);
    }

    public Draft toDraft() {
        return new Draft(id,
                userEmpId,
                traveler.toTravelEmployee(),
                amendment.toAmendment(),
                LocalDateTime.parse(updatedDateTime, ISO_DATE_TIME)
        );
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
