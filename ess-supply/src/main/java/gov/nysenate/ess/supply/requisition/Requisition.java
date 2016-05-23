package gov.nysenate.ess.supply.requisition;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.item.LineItem;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Requisition {

    private int id;
    private SortedMap<LocalDateTime, RequisitionVersion> history;
    private final LocalDateTime orderedDateTime;
    private LocalDateTime processedDateTime;
    private LocalDateTime completedDateTime;
    private LocalDateTime approvedDateTime;
    private LocalDateTime rejectedDateTime;

    public Requisition(LocalDateTime orderedDateTime, RequisitionVersion firstVersion) {
        checkArgument(firstVersion.getStatus() == RequisitionStatus.PENDING, "First Requisition Version must have PENDING status");
        this.orderedDateTime = checkNotNull(orderedDateTime, "Requisition order date time cannot be null.");
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
        history.put(dateTime, version);
    }

    public RequisitionVersion getCurrentVersion() {
        return history.get(history.lastKey());
    }

    public RequisitionVersion getLatestVersionWithStatusIn(EnumSet<RequisitionStatus> statuses) {
        LocalDateTime latestDateTime = getHistory().firstKey();
        RequisitionVersion latestVersion = getHistory().get(latestDateTime);
        for (Map.Entry<LocalDateTime, RequisitionVersion> entry: getHistory().entrySet()) {
            if (statuses.contains(entry.getValue().getStatus())) {
                if (entry.getKey().isAfter(latestDateTime)) {
                    latestDateTime = entry.getKey();
                    latestVersion = entry.getValue();
                }
            }
        }
        return latestVersion;
    }

    /** Getters */

    public int getId() {
        return id;
    }

    public SortedMap<LocalDateTime, RequisitionVersion> getHistory() {
        return new TreeMap<>(history);
    }

    public LocalDateTime getOrderedDateTime() {
        return orderedDateTime;
    }

    public LocalDateTime getModifiedDateTime() {
        return history.lastKey();
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

    @Override
    public String toString() {
        return "Requisition{" +
               "history=" + history +
               ", orderedDateTime=" + orderedDateTime +
               ", processedDateTime=" + processedDateTime +
               ", completedDateTime=" + completedDateTime +
               ", approvedDateTime=" + approvedDateTime +
               ", rejectedDateTime=" + rejectedDateTime +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Requisition that = (Requisition) o;
        if (history != null ? !history.equals(that.history) : that.history != null) return false;
        if (orderedDateTime != null ? !orderedDateTime.equals(that.orderedDateTime) : that.orderedDateTime != null)
            return false;
        if (processedDateTime != null ? !processedDateTime.equals(that.processedDateTime) : that.processedDateTime != null)
            return false;
        if (completedDateTime != null ? !completedDateTime.equals(that.completedDateTime) : that.completedDateTime != null)
            return false;
        if (approvedDateTime != null ? !approvedDateTime.equals(that.approvedDateTime) : that.approvedDateTime != null)
            return false;
        return rejectedDateTime != null ? rejectedDateTime.equals(that.rejectedDateTime) : that.rejectedDateTime == null;
    }

    @Override
    public int hashCode() {
        int result = history != null ? history.hashCode() : 0;
        result = 31 * result + (orderedDateTime != null ? orderedDateTime.hashCode() : 0);
        result = 31 * result + (processedDateTime != null ? processedDateTime.hashCode() : 0);
        result = 31 * result + (completedDateTime != null ? completedDateTime.hashCode() : 0);
        result = 31 * result + (approvedDateTime != null ? approvedDateTime.hashCode() : 0);
        result = 31 * result + (rejectedDateTime != null ? rejectedDateTime.hashCode() : 0);
        return result;
    }
}
