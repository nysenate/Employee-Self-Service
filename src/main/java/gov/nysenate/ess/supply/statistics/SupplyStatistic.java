package gov.nysenate.ess.supply.statistics;

import gov.nysenate.ess.supply.requisition.model.Requisition;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class SupplyStatistic {

    protected Set<Requisition> requisitions;

    public SupplyStatistic(Set<Requisition> requisitions) {
        this.requisitions = checkNotNull(requisitions);
    }

    public abstract Map<String, Integer> calculate();
}
