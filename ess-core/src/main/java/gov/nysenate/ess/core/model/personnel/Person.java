package gov.nysenate.ess.core.model.personnel;

import gov.nysenate.ess.core.model.unit.Address;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

/**
 * Represents basic information that is associated with a person.
 */
public class Person
{
    protected String title;
    protected String firstName;
    protected String lastName;
    protected String initial;
    protected String suffix;
    protected String fullName;
    protected String email;
    protected String workPhone;
    protected String homePhone;
    protected LocalDate dateOfBirth;
    protected Gender gender;
    protected MaritalStatus maritalStatus;
    protected Address homeAddress;

    public Person() {}

    public Person(Person other) {
        this.title = other.title;
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.initial = other.initial;
        this.suffix = other.suffix;
        this.fullName = other.fullName;
        this.email = other.email;
        this.workPhone = other.workPhone;
        this.homePhone = other.homePhone;
        this.dateOfBirth = other.dateOfBirth;
        this.gender = other.gender;
        this.maritalStatus = other.maritalStatus;
        this.homeAddress = other.homeAddress;
    }

    /** Functional Getters */

    /**
     * Returns the age of the person in years based on dateOfBirth.
     * @return int - age
     * @throws IllegalStateException if date of birth is not set.
     */
    public int getAge() {
        if (dateOfBirth != null) {
            return (int) ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now());
        }
        throw new IllegalStateException("Cannot compute age if date of birth is null");
    }

    /** Basic Getters/Setters */

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getFullName() {
        String fullName = this.getFirstName() + " " +
                ((StringUtils.isNotBlank(this.getInitial())) ? (this.getInitial() + " ") : "") +
                this.getLastName() + " " +
                ((StringUtils.isNotBlank(this.getSuffix())) ? this.getSuffix() : "");
        return WordUtils.capitalizeFully(fullName.toLowerCase()).trim().replaceAll("\\s+", " ");
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }
}
