package gov.nysenate.ess.time.client.view;

import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.time.model.personnel.SupervisorChain;

import java.util.Map;
import java.util.stream.Collectors;

public class SupervisorChainView implements ViewObject
{
    protected int supervisorId;
    protected ListView<EmployeeView> supChain;

    public SupervisorChainView(SupervisorChain chain, Map<Integer, Employee> empMap) {
        if (chain != null) {
            this.supervisorId = chain.getEmployeeId();
            this.supChain = ListView.of(
                chain.getChain().stream()
                    .map(supId -> new EmployeeView(empMap.get(supId)))
                    .collect(Collectors.toList()));
        }
    }

    public int getSupervisorId() {
        return supervisorId;
    }

    public ListView<EmployeeView> getSupChain() {
        return supChain;
    }

    @Override
    public String getViewType() {
        return "supervisor chain";
    }
}
