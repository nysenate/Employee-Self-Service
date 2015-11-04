package gov.nysenate.ess.core.model.transaction;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import gov.nysenate.ess.core.model.base.UpdateEvent;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransactionHistoryUpdateEvent extends UpdateEvent
{
    private List<TransactionRecord> transRecs;
    private LocalDateTime updatedDateTime;
    private Set<Integer> affectedEmps = new HashSet<>();
    private Set<TransactionCode> affectedTransCodes = new HashSet<>();

    public TransactionHistoryUpdateEvent(List<TransactionRecord> transRecs, LocalDateTime updatedDateTime) {
        this.transRecs = transRecs;
        this.updatedDateTime = updatedDateTime;
        this.transRecs.forEach(tr -> {
            affectedEmps.add(tr.getEmployeeId());
            affectedTransCodes.add(tr.getTransCode());
        });
    }

    /** --- Functional Getters --- */

    public Multimap<Integer, TransactionRecord> getEmployeeRecs() {
        return Multimaps.index(this.transRecs, TransactionRecord::getEmployeeId);
    }

    /** --- Basic Getters --- */

    public List<TransactionRecord> getTransRecs() {
        return transRecs;
    }

    public Set<Integer> getAffectedEmps() {
        return affectedEmps;
    }

    public Set<TransactionCode> getAffectedTransCodes() {
        return affectedTransCodes;
    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }
}
