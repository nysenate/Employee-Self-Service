package gov.nysenate.ess.core.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.ResponsibilityCenter;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@XmlRootElement
public class RedmineEmployeeView implements ViewObject
{
    protected int employeeId;
    protected String uid;
    protected String firstName;
    protected String lastName;
    protected String fullName;
    protected String email;
    protected String workPhone;
    protected boolean active;
    protected LocationView location;
    /**
     * The normalized search tokens that matched this employee, for client-side result highlighting.
     * Empty when the view was not produced by a free-text search. See EmployeeSearchBuilder#tokenizeSearchTerm.
     */
    protected List<String> matchedTerms;

    public RedmineEmployeeView(Employee employee) {
        this(employee, Collections.emptyList());
    }

    public RedmineEmployeeView(Employee employee, List<String> matchedTerms) {
        this.matchedTerms = matchedTerms;
        if (employee != null) {
            this.employeeId = employee.getEmployeeId();
            this.uid = employee.getUid();
            this.firstName = employee.getFirstName();
            this.lastName = employee.getLastName();
            this.fullName = employee.getFullName();
            this.email = employee.getEmail();
            this.active = employee.isActive();
            this.workPhone = employee.getWorkPhone();
            this.location = Optional.ofNullable(employee.getWorkLocation())
                    .map(LocationView::new)
                    .orElse(null);
        }
    }

    @Override
    public String getViewType() {
        return "redmine employee";
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
    public LocationView getLocation() {
        return location;
    }

    @XmlElement
    public boolean isActive() {
        return active;
    }

    @XmlElement
    public List<String> getMatchedTerms() {
        return matchedTerms;
    }

}
