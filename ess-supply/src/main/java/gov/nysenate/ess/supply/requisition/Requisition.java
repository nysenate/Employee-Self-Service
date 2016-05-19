package gov.nysenate.ess.supply.requisition;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.item.LineItem;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Requisition {

    private int id;
    private SortedMap<LocalDateTime, RequisitionVersion> history;
    private final LocalDateTime orderedDateTime;
    private LocalDateTime modifiedDateTime;
    private LocalDateTime processedDateTime;
    private LocalDateTime completedDateTime;
    private LocalDateTime approvedDateTime;
    private LocalDateTime rejectedDateTime;

    public Requisition(LocalDateTime orderedDateTime, RequisitionVersion firstVersion) {
        checkArgument(firstVersion.getStatus() == RequisitionStatus.PENDING, "First Requisition Version must have PENDING status");
        this.orderedDateTime = checkNotNull(orderedDateTime, "Requisition order date time cannot be null.");
        this.modifiedDateTime = orderedDateTime;
        this.history = new TreeMap<>();
        history.put(orderedDateTime, firstVersion);
    }

    public void addVersion(LocalDateTime dateTime, RequisitionVersion version) {
        if (version.getStatus() == RequisitionStatus.PROCESSING) {
            processedDateTime = dateTime;
        }
        else if (version.getStatus() == RequisitionStatus.COMPLETED) {
            completedDateTime = dateTime;
        }
        else if (version.getStatus() == RequisitionStatus.APPROVED) {
            approvedDateTime = dateTime;
        }
        else if (version.getStatus() == RequisitionStatus.REJECTED) {
            rejectedDateTime = dateTime;
        }
        modifiedDateTime = dateTime;
        history.put(dateTime, version);
    }

    public RequisitionVersion getCurrentVersion() {
        return history.get(history.lastKey());
    }

    /** Getters */

    public int getId() {
        return id;
    }

    public SortedMap<LocalDateTime, RequisitionVersion> getHistory() {
        return history;
    }

    public LocalDateTime getOrderedDateTime() {
        return orderedDateTime;
    }

    public LocalDateTime getModifiedDateTime() {
        return modifiedDateTime;
    }

    public Optional<LocalDateTime> getProcessedDateTime() {
        return Optional.ofNullable(processedDateTime);
    }

    public Optional<LocalDateTime> getCompletedDateTime() {
        return Optional.ofNullable(completedDateTime);
    }

    public Optional<LocalDateTime> getApprovedDateTime() {
        return Optional.ofNullable(approvedDateTime);
    }

    public Optional<LocalDateTime> getRejectedDateTime() {
        return Optional.ofNullable(rejectedDateTime);
    }

    /** Getters - return information for the current requisition version. **/

    public Employee getCustomer() {
        return getCurrentVersion().getCustomer();
    }

    public Location getDestination() {
        return getCurrentVersion().getDestination();
    }

    public ImmutableSet<LineItem> getLineItems() {
        return getCurrentVersion().getLineItems();
    }

    public RequisitionStatus getStatus() {
        return getCurrentVersion().getStatus();
    }

    public Optional<Employee> getIssuer() {
        return getCurrentVersion().getIssuer();
    }

    public Employee getModifiedBy() {
        return getCurrentVersion().getModifiedBy();
    }

    public Optional<String> getNote() {
        return getCurrentVersion().getNote();
    }
}
