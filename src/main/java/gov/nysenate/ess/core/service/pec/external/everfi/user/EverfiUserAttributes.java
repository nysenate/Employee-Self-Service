package gov.nysenate.ess.core.service.pec.external.everfi.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class EverfiUserAttributes {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd'T'hh:mm:ss.SSSx");

    private boolean active;
    @JsonProperty("created_at")
    private String createdAt;
    private String email;
    @JsonProperty("employee_id")
    private String employeeId;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;


    public EverfiUserAttributes() {
    }

    public Integer getEmployeeId() {
        if (StringUtils.isBlank(employeeId)) {
            return null;
        }
        try {
            return Integer.valueOf(employeeId);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public boolean isActive() {
        return active;
    }

    public ZonedDateTime getCreatedAt() {
        return ZonedDateTime.parse(createdAt, DATE_TIME_FORMATTER);
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return "EverfiUserAttributes{" +
                "active=" + active +
                ", createdAt='" + createdAt + '\'' +
                ", email='" + email + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
