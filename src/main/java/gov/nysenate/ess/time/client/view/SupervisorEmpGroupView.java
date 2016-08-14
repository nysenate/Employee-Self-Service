package gov.nysenate.ess.time.client.view;

import gov.nysenate.ess.core.client.view.base.MapView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.time.model.personnel.SupervisorEmpGroup;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class SupervisorEmpGroupView implements ViewObject
{
    protected int supId;
    protected LocalDate effectiveFrom;
    protected LocalDate effectiveTo;
    protected ListView<SupervisorEmpView> primaryEmployees;
    protected MapView<Integer, ListView<SupervisorEmpView>> supOverrideEmployees;
    protected ListView<SupervisorEmpView> empOverrideEmployees;

    public SupervisorEmpGroupView(SupervisorEmpGroup seg) {
        if (seg != null) {
            this.supId = seg.getSupervisorId();
            this.effectiveFrom = seg.getStartDate();
            this.effectiveTo = seg.getEndDate();
            this.primaryEmployees = ListView.of(
                    seg.getPrimaryEmployees().values().stream()
                            .map(SupervisorEmpView::new).collect(toList()));
            Map<Integer, ListView<SupervisorEmpView>> supOvrEmps = new HashMap<>();
            seg.getSupOverrideEmployees().rowMap().forEach((supId, emps) ->
                supOvrEmps.put(supId, ListView.of(emps.values().stream()
                    .map(SupervisorEmpView::new).collect(toList()))));
            this.supOverrideEmployees = MapView.of(supOvrEmps);
            this.empOverrideEmployees = ListView.of(
                seg.getOverrideEmployees().values().stream().map(SupervisorEmpView::new).collect(toList()));
        }
    }

    public int getSupId() {
        return supId;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }

    public ListView<SupervisorEmpView> getPrimaryEmployees() {
        return primaryEmployees;
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
