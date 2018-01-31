package gov.nysenate.ess.core.model.acknowledgment;

import gov.nysenate.ess.core.model.unit.Location;

import java.time.LocalDateTime;
import java.util.HashMap;

public class EmpAckReport {

    private int empId;
    private String firstName;
    private String lastName;
    private String email;
    private Location workLocation;
    private HashMap<String, LocalDateTime> AckedTimeMap = new HashMap<>();

    /*
    Constructors
     */
    public EmpAckReport() {}

    //Report 1 we care about all emp
    public EmpAckReport(int empId, String firstName, String lastName, String email, Location workLocation) {
        this.empId = empId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.workLocation = workLocation;
    }

    public EmpAckReport(int empId, String ackDocTitle ,LocalDateTime timestamp) {
        this.empId = empId;
        AckedTimeMap.put(ackDocTitle,timestamp);
    }

    /*
    Getters and Setters
     */

    public HashMap<String, LocalDateTime> getAckedTimeMap() {
        return AckedTimeMap;
    }

    public void setAckedTimeMap(HashMap<String, LocalDateTime> ackedTimeMap) {
        AckedTimeMap = ackedTimeMap;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
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

    public Location getWorkLocation() {
        return workLocation;
    }

    public void setWorkLocation(Location workLocation) {
        this.workLocation = workLocation;
    }
}
