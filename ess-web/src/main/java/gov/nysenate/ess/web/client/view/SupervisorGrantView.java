package gov.nysenate.ess.web.client.view;

import gov.nysenate.ess.web.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.web.model.personnel.SupervisorOverride;

/**
 * Similar to SupervisorOverrideView, just reversed.
 */
public class SupervisorGrantView extends SupervisorGrantSimpleView implements ViewObject
{
    protected EmployeeView granteeSupervisor;

    /** --- Constructors --- */

    public SupervisorGrantView(SupervisorOverride ovr, Employee granteeSupervisor) {
        super(ovr);
        this.granteeSupervisor = new EmployeeView(granteeSupervisor);
    }

    @Override
    public String getViewType() {
        return "supervisor grant";
    }

    /** --- Basic Getters --- */

    public EmployeeView getGranteeSupervisor() {
        return granteeSupervisor;
    }
}
