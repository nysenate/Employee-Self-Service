package gov.nysenate.ess.core.service.pec.external.everfi.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.math.NumberUtils;

public class EverfiAssignmentUser {
    public String id;
    public String email;
    public boolean active;
    @JsonProperty("sso_id")
    public String ssoId; // Single sign on id.
    public boolean deleted;
    public EverfiAssignmentUserLocation location;
    @JsonProperty("last_name")
    public String lastName;
    @JsonProperty("first_name")
    public String firstName;
    @JsonProperty("employee_id")
    public String employeeId;

    public EverfiAssignmentUser() {
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return active;
    }

    public String getSsoId() {
        return ssoId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public EverfiAssignmentUserLocation getLocation() {
        return location;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public int getEmployeeId() {
        return NumberUtils.toInt(employeeId, 0);
    }

    @Override
    public String toString() {
        return "EverfiAssignmentUser{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                ", ssoId='" + ssoId + '\'' +
                ", deleted=" + deleted +
                ", location=" + location +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", employeeId='" + employeeId + '\'' +
                '}';
    }

    private static class EverfiAssignmentUserLocation {
        private String name;

        public EverfiAssignmentUserLocation() {
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "EverfiAssignmentUserLocation{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
