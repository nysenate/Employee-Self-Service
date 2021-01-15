package gov.nysenate.ess.travel.review;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.authorization.role.TravelRole;

import java.time.LocalDateTime;
import java.util.Objects;

public class Action {

    protected int actionId;
    protected final Employee user;
    protected final TravelRole role;
    protected final ActionType type;
    protected final String notes;
    protected final LocalDateTime dateTime;

    public Action(int actionId, Employee user, TravelRole role, ActionType type,
                  String notes, LocalDateTime dateTime) {
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
     * Was this action an approval.
     */
    public boolean isApproval() {
        return type == ActionType.APPROVE;
    }

    /**
     * Was this action a disapproval
     */
    public boolean isDisapproval() {
        return type == ActionType.DISAPPROVE;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return actionId == action.actionId &&
                Objects.equals(user, action.user) &&
                role == action.role &&
                type == action.type &&
                Objects.equals(notes, action.notes) &&
                Objects.equals(dateTime, action.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionId, user, role, type, notes, dateTime);
    }
}
