package gov.nysenate.ess.core.model.pec.moodle;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MoodleEmployeeRecord {

    private int userid;
    private String firstname;
    private String lastname;
    private String email;
    private String organization;
    private String confirmed;
    private String completed;

    public MoodleEmployeeRecord(){}

    public MoodleEmployeeRecord(int userid, String firstname, String lastname, String email,
                                String organization, String confirmed, String completed) {
        this.userid = userid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.organization = organization;
        this.confirmed = confirmed;
        this.completed = completed;

        if (didEmployeeCompleteCourse()) {
            convertStringToLocalDateTime();
        }
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public boolean didEmployeeCompleteCourse() {
        return !(completed.equalsIgnoreCase("incomplete"));
    }

    private LocalDateTime convertStringToLocalDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(this.completed, formatter);
    }

    public LocalDateTime getCompletedTime() {
        if (didEmployeeCompleteCourse()) {
            return convertStringToLocalDateTime();
        }
        return null;
    }
}
