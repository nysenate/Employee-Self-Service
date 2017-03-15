package gov.nysenate.ess.time.client.view.personnel;

import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.client.view.base.MapView;
import gov.nysenate.ess.time.model.personnel.SupervisorEmpGroup;

import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class SupervisorEmpGroupView extends PrimarySupEmpGroupView
{
    protected MapView<Integer, ListView<SupervisorEmpView>> supOverrideEmployees;
    protected ListView<SupervisorEmpView> empOverrideEmployees;

    public SupervisorEmpGroupView(SupervisorEmpGroup seg) {
        super(seg);
        if (seg != null) {
            Map<Integer, ListView<SupervisorEmpView>> supOvrEmps = new HashMap<>();
            seg.getSupOverrideEmployees().rowMap().forEach((supId, emps) ->
                supOvrEmps.put(supId, ListView.of(emps.values().stream()
                    .map(SupervisorEmpView::new).collect(toList()))));
            this.supOverrideEmployees = MapView.of(supOvrEmps);
            this.empOverrideEmployees = ListView.of(
                seg.getOverrideEmployees().values().stream().map(SupervisorEmpView::new).collect(toList()));
        }
    }

    public MapView<Integer, ListView<SupervisorEmpView>> getSupOverrideEmployees() {
        return supOverrideEmployees;
    }

    public ListView<SupervisorEmpView> getEmpOverrideEmployees() {
        return empOverrideEmployees;
    }

    @Override
    public String getViewType() {
        return "supervisor emp group";
    }
}
