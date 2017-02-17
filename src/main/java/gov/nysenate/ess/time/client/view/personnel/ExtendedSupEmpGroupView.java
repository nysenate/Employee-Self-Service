package gov.nysenate.ess.time.client.view.personnel;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import gov.nysenate.ess.time.model.personnel.ExtendedSupEmpGroup;

public class ExtendedSupEmpGroupView extends SupervisorEmpGroupView {

    private Multimap<Integer, SupervisorEmpGroupView> employeeSupEmpGroups = HashMultimap.create();

    public ExtendedSupEmpGroupView(ExtendedSupEmpGroup eseg) {
        super(eseg);

        eseg.getEmployeeSupEmpGroups().values().stream()
                .map(SupervisorEmpGroupView::new)
                .forEach(seg -> employeeSupEmpGroups.put(seg.getSupId(), seg));
    }

    public Multimap<Integer, SupervisorEmpGroupView> getEmployeeSupEmpGroups() {
        return employeeSupEmpGroups;
    }

    @Override
    public String getViewType() {
        return "extended " + super.getViewType();
    }
}
