package gov.nysenate.ess.supply.reconcilation.model;

import java.util.Objects;

public class ReconciliationError {

    private final int itemId;
    private final int expectedQuantity;
    private final int actualQuantity;

    public ReconciliationError(int itemId, int expectedQuantity, int actualQuantity) {
        this.itemId = itemId;
        this.expectedQuantity = expectedQuantity;
        this.actualQuantity = actualQuantity;
    }

    public int getItemId() {
        return itemId;
    }

    public int getExpectedQuantity() {
        return expectedQuantity;
    }

    public int getActualQuantity() {
        return actualQuantity;
    }

    @Override
    public String toString() {
        return "ReconciliationError{" +
                "itemId=" + itemId +
                ", expectedQuantity=" + expectedQuantity +
                ", actualQuantity=" + actualQuantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReconciliationError that = (ReconciliationError) o;
        return itemId == that.itemId &&
                expectedQuantity == that.expectedQuantity &&
                actualQuantity == that.actualQuantity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, expectedQuantity, actualQuantity);
    }
}
