package gov.nysenate.ess.time.client.view.personnel;

import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.model.personnel.PrimarySupEmpGroup;

import java.time.LocalDate;

import static java.util.stream.Collectors.toList;

public class PrimarySupEmpGroupView implements ViewObject {

    protected int supId;
    protected LocalDate effectiveFrom;
    protected LocalDate effectiveTo;
    protected ListView<SupervisorEmpView> primaryEmployees;

    public PrimarySupEmpGroupView(PrimarySupEmpGroup seg) {
        if (seg != null) {
            this.supId = seg.getSupervisorId();
            this.effectiveFrom = seg.getStartDate();
            this.effectiveTo = seg.getEndDate();
            this.primaryEmployees = ListView.of(
                    seg.getPrimaryEmployees().values().stream()
                            .map(SupervisorEmpView::new).collect(toList()));
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

    @Override
    public String getViewType() {
        return "primary-sup-emp-group";
    }
}
