package gov.nysenate.ess.travel.approval;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.TravelRole;

import java.time.LocalDateTime;

public class Approval {

    private final Employee employee;
    private final TravelRole role;
    private final String notes;
    private final LocalDateTime dateTime;

    public Approval(Employee employee, TravelRole role, String notes, LocalDateTime dateTime) {
        this.employee = employee;
        this.role = role;
        this.notes = notes;
        this.dateTime = dateTime;
    }

    protected TravelRole getRole() {
        return role;
    }
}
