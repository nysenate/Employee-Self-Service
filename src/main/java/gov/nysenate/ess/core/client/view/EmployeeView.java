package gov.nysenate.ess.core.client.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.Gender;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EmployeeView extends SimpleEmployeeView implements ViewObject
{
    protected String title;
    protected String firstName;
    protected String lastName;
    protected String initial;
    protected String suffix;
    protected String workPhone;
    protected String homePhone;
    protected String gender;

    public EmployeeView() {}

    public EmployeeView(Employee employee) {
        super(employee);
        if (employee != null) {
            this.title = employee.getTitle();
            this.firstName = employee.getFirstName();
            this.lastName = employee.getLastName();
            this.initial = employee.getInitial();
            this.suffix = employee.getSuffix();
            this.workPhone = employee.getWorkPhone();
            this.homePhone = employee.getHomePhone();
            this.gender = employee.getGender().name();
        }
    }

    @JsonIgnore
    public Employee toEmployee() {
        Employee emp = new Employee();
        emp.setEmployeeId(employeeId);
        emp.setUid(uid);
        emp.setTitle(title);
        emp.setFirstName(firstName);
        emp.setLastName(lastName);
        emp.setInitial(initial);
        emp.setSuffix(suffix);
        emp.setFullName(fullName);
        emp.setActive(active);
        emp.setEmail(email);
        emp.setWorkPhone(workPhone);
        emp.setHomePhone(homePhone);
        emp.setGender(Gender.valueOf(gender));
        return emp;
    }

    @Override
    @XmlElement
    public String getViewType() {
        return "employee";
    }

    @XmlElement
    public String getTitle() {
        return title;
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
    public String getInitial() {
        return initial;
    }

    @XmlElement
    public String getSuffix() {
        return suffix;
    }

    @XmlElement
    public String getWorkPhone() {
        return workPhone;
    }

    @XmlElement
    public String getHomePhone() {
        return homePhone;
    }

    @XmlElement
    public String getGender() {
        return gender;
    }
}