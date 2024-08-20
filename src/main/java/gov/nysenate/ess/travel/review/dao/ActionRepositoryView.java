package gov.nysenate.ess.travel.review.dao;

import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.review.view.ActionType;

import java.time.LocalDateTime;

class ActionRepositoryView {

    public int actionId;
    public int userEmpId;
    public TravelRole role;
    public ActionType type;
    public String notes;
    public LocalDateTime dateTime;

    ActionRepositoryView() {

    }
}
