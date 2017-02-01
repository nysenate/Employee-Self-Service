package gov.nysenate.ess.time.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.model.personnel.EmployeeSupInfo;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;

@XmlRootElement
public class SupervisorEmpView implements ViewObject
{
    protected int empId;
    protected int supId;
    protected String empLastName;
    protected String empFirstName;
    protected LocalDate supStartDate;
    protected LocalDate supEndDate;

    /** --- Constructors --- */

    public SupervisorEmpView(EmployeeSupInfo supInfo) {
        if (supInfo != null) {
            this.empId = supInfo.getEmpId();
            this.supId = supInfo.getSupId();
            this.empLastName = supInfo.getEmpLastName();
            this.empFirstName = supInfo.getEmpFirstName();
            this.supStartDate = supInfo.getSupStartDate();
            this.supEndDate = supInfo.getSupEndDate();
        }
    }

    /** --- Basic Getters --- */

    @XmlElement
    public int getEmpId() {
        return empId;
    }

    @XmlElement
    public int getSupId() {
        return supId;
    }

    @XmlElement
    public String getEmpLastName() {
        return empLastName;
    }

    @XmlElement
    public String getEmpFirstName() {
        return empFirstName;
    }

    @XmlElement
    public LocalDate getSupStartDate() {
        return supStartDate;
    }

    @XmlElement
    public LocalDate getSupEndDate() {
        return supEndDate;
    }

    @XmlElement
    @Override
    public String getViewType() {
        return "supervisor emp";
    }
}
