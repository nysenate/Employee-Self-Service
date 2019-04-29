package gov.nysenate.ess.travel.review;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.authorization.role.TravelRole;

import java.time.LocalDateTime;

public class Action {

    private int actionId;
    private final Employee user;
    private final TravelRole role;
    private final ActionType type;
    private final String notes;
    private final LocalDateTime dateTime;

    public Action(int actionId, Employee user, TravelRole role, ActionType type, String notes, LocalDateTime dateTime) {
        this.actionId = actionId;
        this.user = user;
        this.role = role;
        this.type = type;
        this.notes = notes;
        this.dateTime = dateTime;
    }

    /**
     * The user who performed this action.
     */
    public Employee user() {
        return user;
    }

    /**
     * The role of the user who performed this action.
     */
    public TravelRole role() {
        return role;
    }

    /**
     * The type of action performed.
     * @return
     */
    public ActionType type() {
        return type;
    }

    /**
     * Any notes left by the user.
     */
    public String notes() {
        return notes;
    }

    /**
     * The datetime this action was performed.
     */
    public LocalDateTime dateTime() {
        return dateTime;
    }

    int getActionId() {
        return actionId;
    }

    void setActionId(int actionId) {
        this.actionId = actionId;
    }
}
