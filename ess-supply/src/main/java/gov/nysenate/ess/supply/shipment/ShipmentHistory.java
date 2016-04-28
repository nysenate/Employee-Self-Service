package gov.nysenate.ess.supply.shipment;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;

public class ShipmentHistory {

    private final ImmutableSortedMap<LocalDateTime, ShipmentVersion> shipmentVersionMap;

    private ShipmentHistory(ImmutableSortedMap<LocalDateTime, ShipmentVersion> shipmentVersionMap) {
        this.shipmentVersionMap = shipmentVersionMap;
    }

    /**
     * Static constructors
     */

    public static ShipmentHistory of(LocalDateTime modifyDateTime, ShipmentVersion version) {
        return new ShipmentHistory(ImmutableSortedMap.of(modifyDateTime, version));
    }

    public static ShipmentHistory of(SortedMap<LocalDateTime, ShipmentVersion> shipmentVersionMap) {
        return new ShipmentHistory(ImmutableSortedMap.copyOf(shipmentVersionMap));
    }

    public static ShipmentHistory of(ImmutableSortedMap<LocalDateTime, ShipmentVersion> shipmentVersionMap) {
        return new ShipmentHistory(shipmentVersionMap);
    }

    /**
     * Functional Methods
     */

    public Optional<LocalDateTime> getProcessedDateTime() {
        return getDateTimeOfStatus(ShipmentStatus.PROCESSING);
    }

    public Optional<LocalDateTime> getCompletedDateTime() {
        return getDateTimeOfStatus(ShipmentStatus.COMPLETED);
    }

    public Optional<LocalDateTime> getApprovedDateTime() {
        return getDateTimeOfStatus(ShipmentStatus.APPROVED);
    }

    public Optional<LocalDateTime> getCanceledDateTime() {
        return getDateTimeOfStatus(ShipmentStatus.CANCELED);
    }

    private Optional<LocalDateTime> getDateTimeOfStatus(ShipmentStatus status) {
        for (Map.Entry<LocalDateTime, ShipmentVersion> version: shipmentVersionMap.entrySet()) {
            if (version.getValue().getStatus() == status) {
                return Optional.of(version.getKey());
            }
        }
        return Optional.empty();
    }

    public ShipmentVersion get(LocalDateTime modifiedDateTime) {
        return shipmentVersionMap.get(modifiedDateTime);
    }

    public ImmutableSortedMap<LocalDateTime, ShipmentVersion> getHistory() {
        return shipmentVersionMap;
    }

    protected ShipmentVersion current() {
        return shipmentVersionMap.get(shipmentVersionMap.lastKey());
    }

    protected ShipmentHistory addVersion(LocalDateTime modifiedDateTime, ShipmentVersion version) {
        ImmutableSortedMap versions = new ImmutableSortedMap.Builder<LocalDateTime, ShipmentVersion>(Ordering.natural())
                .putAll(shipmentVersionMap).put(modifiedDateTime, version).build();
        return ShipmentHistory.of(versions);
    }

    protected int size() {
        return shipmentVersionMap.size();
    }

    @Override
    public String toString() {
        return "ShipmentHistory{" +
               "shipmentVersionMap=" + shipmentVersionMap +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShipmentHistory that = (ShipmentHistory) o;
        return !(shipmentVersionMap != null ? !shipmentVersionMap.equals(that.shipmentVersionMap) : that.shipmentVersionMap != null);
    }

    @Override
    public int hashCode() {
        return shipmentVersionMap != null ? shipmentVersionMap.hashCode() : 0;
    }
}
