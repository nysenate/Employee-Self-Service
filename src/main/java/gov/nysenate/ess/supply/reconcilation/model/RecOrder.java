package gov.nysenate.ess.supply.reconcilation.model;

import java.util.Objects;

public final class RecOrder {

    private final String itemId;
    private final int quantity;

    private RecOrder(Builder builder) {
        this.itemId = builder.itemId;
        this.quantity = builder.quantity;
    }

    public RecOrder(RecOrder recOrder){
        this.itemId = recOrder.getItemId();
        this.quantity = recOrder.getQuantity();
    }


    /**
     * Returns a {@link RecOrder.Builder} which contains a copy of
     * this rec order data. Useful for creating new instances where
     * only a few fields differ.
     */
    private RecOrder.Builder copy() {
        return new RecOrder.Builder()
                .withItemId(this.itemId)
                .withQuantity(this.quantity);
    }


    public String getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }


    @Override
    public String toString() {
        return "RecOrder{" +
                "itemId=" + itemId +
                ", quantity=" + quantity + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecOrder that = (RecOrder) o;
        return itemId == that.itemId && quantity == that.quantity;
    }

    @Override
    public int hashCode(){
        return Objects.hash(itemId, quantity);
    }


        public static class Builder {
            private String itemId;
            private int quantity;

            public Builder withItemId(String itemId) {
                this.itemId = itemId;
                return this;
            }

            public Builder withQuantity(int quantity) {
                this.quantity = quantity;
                return this;
            }

            public RecOrder build() {
                return new RecOrder(this);
            }
        }
    }
