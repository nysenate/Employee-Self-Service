package gov.nysenate.ess.supply.reconcilation.model;

import java.util.Objects;

public final class RecOrder {

    private final String itemName;
    private final int quantity;

    private RecOrder(Builder builder) {
        this.itemName = builder.itemName;
        this.quantity = builder.quantity;
    }


    /**
     * Returns a {@link RecOrder.Builder} which contains a copy of
     * this requisitions data. Useful for creating new instances where
     * only a few fields differ.
     */
    private RecOrder.Builder copy() {
        return new RecOrder.Builder()
                .withItemName(this.itemName)
                .withQuantity(this.quantity);
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public RecOrder setQuantity(int quantity) {
        return copy().withQuantity(quantity).build();
    }

    public RecOrder setItemName(String itemName) {
        return copy().withItemName(itemName).build();
    }

    @Override
    public String toString() {
        return "RecOrder{" +
                "itemName=" + itemName +
                ", quantity=" + quantity + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecOrder that = (RecOrder) o;
        return itemName == that.itemName && quantity == that.quantity;
    }

    @Override
    public int hashCode(){
        return Objects.hash(itemName, quantity);
    }


        public static class Builder {
            private String itemName;
            private int quantity;

            public Builder withItemName(String itemName) {
                this.itemName = itemName;
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
