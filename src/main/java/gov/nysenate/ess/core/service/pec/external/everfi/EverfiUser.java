package gov.nysenate.ess.core.service.pec.external.everfi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EverfiUser {

    // TODO WIP - Add more user fields!

    @JsonProperty("id")
    private String uuid;

    private EverfiUserAttributes attributes;


    public EverfiUser() {
    }

    public String getUuid() {
        return uuid;
    }

    public EverfiUserAttributes getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "EverfiUser{" +
                "uuid='" + uuid + '\'' +
                ", attributes=" + attributes +
                '}';
    }

    private class EverfiUserAttributes {

        private String email;

        @JsonProperty("employee_id")
        @JsonFormat(shape= JsonFormat.Shape.STRING)
        private int employeeId;

        @JsonProperty("first_name")
        private String firstName;

        public EverfiUserAttributes() {
        }

        public String getEmail() {
            return email;
        }

        public int getEmployeeId() {
            return employeeId;
        }

        public String getFirstName() {
            return firstName;
        }

        @Override
        public String toString() {
            return "EverfiUserAttributes{" +
                    "email='" + email + '\'' +
                    ", employeeId=" + employeeId +
                    ", firstName='" + firstName + '\'' +
                    '}';
        }
    }
}
