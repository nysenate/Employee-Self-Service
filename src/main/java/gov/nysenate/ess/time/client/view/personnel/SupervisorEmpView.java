package gov.nysenate.ess.time.client.view.personnel;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.time.model.personnel.EmployeeSupInfo;
import org.apache.commons.lang3.ObjectUtils;

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
    protected boolean senator;
    protected PayType payType;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected LocalDate supStartDate;
    protected LocalDate supEndDate;

    /** --- Constructors --- */

    public SupervisorEmpView(EmployeeSupInfo supInfo) {
        if (supInfo == null) {
            return;
        }
        this.empId = supInfo.getEmpId();
        this.supId = supInfo.getSupId();
        this.empLastName = supInfo.getEmpLastName();
        this.empFirstName = supInfo.getEmpFirstName();
        this.senator = supInfo.isSenator();
        this.payType = supInfo.getPayType();
        this.startDate = supInfo.getStartDate();
        this.endDate = supInfo.getEndDate();
        this.supStartDate = supInfo.getSupStartDate();
        this.supEndDate = supInfo.getSupEndDate();
    }

    /* --- Functional Getters --- */

    @XmlElement
    public LocalDate getEffectiveStartDate() {
        return ObjectUtils.max(startDate, supStartDate);
    }

    @XmlElement
    public LocalDate getEffectiveEndDate() {
        return ObjectUtils.min(endDate, supEndDate);
    }

    /* --- Basic Getters --- */

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
    public boolean isSenator() {
        return senator;
    }

    @XmlElement
    public LocalDate getStartDate() {
        return supStartDate;
    }

    @XmlElement
    public LocalDate getEndDate() {
        return supEndDate;
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
