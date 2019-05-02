package gov.nysenate.ess.core.model.pec;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MoodleEmployeeRecord {

    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private String organization;
    private String city;
    private String office;
    private String location;
    private String phoneNumber;
    private boolean confirmed;
    private String completedString;
    private LocalDateTime completedTime;


    public MoodleEmployeeRecord(int userId, String firstName, String lastName, String email,
                                String organization, String city, String office, String location,
                                String phoneNumber, boolean confirmed, String completedString) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.organization = organization;
        this.city = city;
        this.office = office;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.confirmed = confirmed;
        this.completedString = completedString;

    }

    private void convertStringToLocalDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
        this.completedTime = LocalDateTime.parse(this.completedString, formatter);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean  didEmployeeCompleteCourse() {
        return !(completedTime == null);
    }

    public String getCompletedString() {
        return completedString;
    }

    public void setCompletedString(String completedString) {
        this.completedString = completedString;
    }

    public LocalDateTime getCompletedTime() {
        if (didEmployeeCompleteCourse()) {
            return completedTime;
        }
        return null;
    }

    public void setCompletedTime(LocalDateTime completedTime) {
        this.completedTime = completedTime;
    }
}
