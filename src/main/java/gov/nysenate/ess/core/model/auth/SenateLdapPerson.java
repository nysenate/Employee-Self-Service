package gov.nysenate.ess.core.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.io.Serializable;

/**
 * Represents a person record in the Senate LDAP. This class is annotated such that methods
 * called by {@link org.springframework.ldap.core.LdapTemplate} can map the resulting context
 * directly into this object.
 */
@Entry(objectClasses = {"person", "top"})
public final class SenateLdapPerson implements Serializable, SenatePerson
{
    private static final long serialVersionUID = 3289890768256266928L;

    @Id
    private Name dn;

    @Attribute(name = "cn")
    private String commonName;

    @Attribute(name = "ou")
    private String organization;

    @Attribute(name = "employeeid")
    private String employeeId;

    @Attribute(name = "mail")
    private String email;

    @Attribute(name = "uid")
    private String uid;

    @Attribute(name = "givenname")
    private String firstName;

    @Attribute(name = "middleinitial")
    private String middleInitial;

    @Attribute(name = "sn")
    private String lastName;

    @Attribute(name = "title")
    private String title;

    @Attribute(name = "postaladdress")
    private String postalAddress;

    @Attribute(name = "officestreetaddress")
    private String officeAddress;

    @Attribute(name = "l")
    private String location;

    @Attribute(name = "st")
    private String state;

    @Attribute(name = "postalcode")
    private String postalCode;

    @Attribute(name = "department")
    private String department;

    @Attribute(name = "telephonenumber")
    private String phoneNumber;

    public SenateLdapPerson() {}

    public SenateLdapPerson(Attributes attrs) throws NamingException {
        if (attrs != null) {
            this.commonName = (attrs.get("cn") != null) ? attrs.get("cn").get().toString() : null;
            this.organization = (attrs.get("ou") != null) ? attrs.get("ou").get().toString() : null;
            this.employeeId = (attrs.get("employeeid") != null) ? attrs.get("employeeid").get().toString() : null;
            this.email = (attrs.get("mail") != null) ? attrs.get("mail").get().toString() : null;
            this.uid = (attrs.get("uid") != null) ? attrs.get("uid").get().toString() : null;
            this.firstName = (attrs.get("givenname") != null) ? attrs.get("givenname").get().toString() : null;
            this.middleInitial = (attrs.get("middleinitial") != null) ? attrs.get("middleinitial").get().toString() : null;
            this.lastName = (attrs.get("sn") != null) ? attrs.get("sn").get().toString() : null;
            this.title = (attrs.get("title") != null) ? attrs.get("title").get().toString() : null;
            this.postalAddress = (attrs.get("postaladdress") != null) ? attrs.get("postaladdress").get().toString() : null;
            this.officeAddress = (attrs.get("officestreetaddress") != null) ? attrs.get("officestreetaddress").get().toString() : null;
            this.location = (attrs.get("l") != null) ? attrs.get("l").get().toString() : null;
            this.state = (attrs.get("st") != null) ? attrs.get("st").get().toString() : null;
            this.postalCode = (attrs.get("postalcode") != null) ? attrs.get("postalcode").get().toString() : null;
            this.department = (attrs.get("department") != null) ? attrs.get("department").get().toString() : null;
            this.phoneNumber = (attrs.get("telephonenumber") != null) ? attrs.get("telephonenumber").get().toString() : null;
        }
    }

    @Override
    public String toString() {
        return "SenateLdapPerson{" +
                "dn=" + dn +
                ", commonName='" + commonName + '\'' +
                ", organization='" + organization + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", email='" + email + '\'' +
                ", uid='" + uid + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleInitial='" + middleInitial + '\'' +
                ", lastName='" + lastName + '\'' +
                ", title='" + title + '\'' +
                ", department='" + department + '\'' +
                '}';
    }

    /** --- Overrides --- */

//    @Override
//    public String toString() {
//        return uid;
//    }



    public Integer getEmployeeId() {
        return Integer.parseInt(employeeId);
    }

    @Override
    public String getFullName() {
        return this.getFirstName() + " " + this.getLastName();
    }

    /** --- Basic Getters/Setters --- */

    @JsonIgnore
    public Name getDn() {
        return dn;
    }

    public void setDn(Name dn) {
        this.dn = dn;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleInitial() {
        return middleInitial;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
