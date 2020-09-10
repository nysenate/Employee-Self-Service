package gov.nysenate.ess.core.service.pec.external.everfi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EverfiAssignment {
    public String id;

    @JsonProperty("user")
    public EverfiAssignmentUser user;

    @Override
    public String toString() {
        return "EverfiAssignment{" +
                "id='" + id + '\'' +
                ", user=" + user +
                '}';
    }

    private class EverfiAssignmentUser {
        public String id;
        public String email;
        public boolean active;
        public String first_name;
        public String last_name;

        @JsonFormat(shape= JsonFormat.Shape.STRING)
        public int employee_id;

        public EverfiAssignmentUser() {
        }

        @Override
        public String toString() {
            return "EverfiAssignmentUser{" +
                    "id='" + id + '\'' +
                    ", email='" + email + '\'' +
                    ", active=" + active +
                    ", first_name='" + first_name + '\'' +
                    ", last_name='" + last_name + '\'' +
                    ", employee_id=" + employee_id +
                    '}';
        }
    }
}
