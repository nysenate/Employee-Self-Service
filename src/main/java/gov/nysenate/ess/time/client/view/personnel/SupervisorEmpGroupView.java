package gov.nysenate.ess.time.client.view.personnel;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.client.view.base.MapView;
import gov.nysenate.ess.time.model.personnel.SupervisorEmpGroup;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

import static java.util.stream.Collectors.*;

public class SupervisorEmpGroupView extends PrimarySupEmpGroupView
{
    protected MapView<Integer, ListView<SupervisorEmpView>> supOverrideEmployees;
    protected ListView<SupervisorEmpView> empOverrideEmployees;

    public SupervisorEmpGroupView(SupervisorEmpGroup seg) {
        super(seg);
        if (seg != null) {
            List<SupervisorEmpView> supEmpViews =
                    seg.getSupOverrideInfos().stream().map(SupervisorEmpView::new).collect(toList());
            Multimap<Integer, SupervisorEmpView> supOvrEmps = Multimaps.index(supEmpViews, SupervisorEmpView::getSupId);
            this.supOverrideEmployees = supOvrEmps.asMap().entrySet().stream()
                    .map(entry -> Pair.of(entry.getKey(), ListView.of(entry.getValue())))
                    .collect(collectingAndThen(toMap(Pair::getKey, Pair::getValue), MapView::of));
            this.empOverrideEmployees = ListView.of(
                seg.getOverrideEmployees().stream().map(SupervisorEmpView::new).collect(toList()));
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
