package gov.nysenate.ess.core.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.personnel.Employee;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BACHelpEmployeeView implements ViewObject
{
    protected int employeeId;
    protected String uid;
    protected String firstName;
    protected String lastName;
    protected String fullName;
    protected String email;
    protected String workPhone;
    protected boolean active;

    public BACHelpEmployeeView(Employee employee) {
        if (employee != null) {
            this.employeeId = employee.getEmployeeId();
            this.uid = employee.getUid();
            this.firstName = employee.getFirstName();
            this.lastName = employee.getLastName();
            this.fullName = employee.getFullName();
            this.email = employee.getEmail();
            this.active = employee.isActive();
            this.workPhone = employee.getWorkPhone();
        }
    }

    @Override
    public String getViewType() {
        return "bachelp employee";
    }

    @XmlElement
    public int getEmployeeId() {
        return employeeId;
    }

    @XmlElement
    public String getUid() {
        return uid;
    }

    @XmlElement
    public String getFirstName() {
        return firstName;
    }

    @XmlElement
    public String getLastName() {
        return lastName;
    }

    @XmlElement
    public String getFullName() {
        return fullName;
    }

    @XmlElement
    public String getEmail() {
        return email;
    }

    @XmlElement
    public String getWorkPhone() {
        return workPhone;
    }

    @XmlElement
    public boolean isActive() {
        return active;
    }
}
