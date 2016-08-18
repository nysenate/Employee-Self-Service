package gov.nysenate.ess.supply.statistics;

import gov.nysenate.ess.supply.requisition.Requisition;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class SupplyStatistic {

    protected List<Requisition> requisitions;

    public SupplyStatistic(List<Requisition> requisitions) {
        this.requisitions = checkNotNull(requisitions);
    }

    public abstract Map<String, Integer> calculate();
}
