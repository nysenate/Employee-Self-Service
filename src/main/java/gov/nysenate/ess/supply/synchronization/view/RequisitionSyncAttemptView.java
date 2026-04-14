package gov.nysenate.ess.supply.synchronization.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.synchronization.model.RequisitionSyncAttempt;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class RequisitionSyncAttemptView implements ViewObject {
    protected int syncAttemptId;
    protected int requisitionId;
    protected int revisionId;
    protected int attemptCount;
    protected String attemptDateTime;
    protected boolean wasSuccessful;
    protected String outcomeSyncStatus;
    protected String errorMsg;
    protected List<Integer> syncedItemIds;

    public RequisitionSyncAttemptView() {

    }

    public RequisitionSyncAttemptView(RequisitionSyncAttempt attempt) {
        this.syncAttemptId = attempt.getSyncAttemptId();
        this.revisionId = attempt.getRevisionId();
        this.attemptCount = attempt.getAttemptCount();
        this.attemptDateTime = attempt.getAttemptDateTime().toString();
        this.outcomeSyncStatus = String.valueOf(attempt.getOutcomeSyncStatus());
        this.errorMsg = attempt.getErrorMsg() == null ? "" : attempt.getErrorMsg();
        this.wasSuccessful = attempt.getWasSuccessful();
        this.syncedItemIds = attempt.getSyncedItemIds();
    }

    public int getSyncAttemptId() {
        return syncAttemptId;
    }

    public int getRequisitionId() {
        return requisitionId;
    }

    public int getRevisionId() {
        return revisionId;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public String getAttemptDateTime() {
        return attemptDateTime;
    }

    public boolean getWasSuccessful() {
        return wasSuccessful;
    }

    public String getOutcomeSyncStatus() {
        return outcomeSyncStatus;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public List<Integer> getSyncedItemIds() {
        return syncedItemIds;
    }

    @Override
    public String getViewType() {
        return "requistionsyncattempt";
    }
}
