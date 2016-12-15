package gov.nysenate.ess.supply.item.model;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.unit.LocationId;

import static com.google.common.base.Preconditions.checkNotNull;

public final class SupplyItem {

    private final int id;
    private final String commodityCode;
    private final String description;
    private final ItemStatus status;
    private final Category category;
    private final ItemAllowance allowance;
    private final ItemUnit unit;
    private ItemRestriction restriction;

    public SupplyItem(Builder builder) {
        this.id = builder.id;
        this.commodityCode = checkNotNull(builder.commodityCode, "SupplyItem cannot have null commodityCode.");
        this.description = checkNotNull(builder.description, "SupplyItem cannot have null description.");
        this.status = checkNotNull(builder.status, "SupplyItem cannot have null ItemStatus.");
        this.category = checkNotNull(builder.category, "SupplyItem cannot have null Category.");
        this.allowance = checkNotNull(builder.allowance, "SupplyItem cannot have null ItemAllowance.");
        this.unit = checkNotNull(builder.unit, "SupplyItem cannot have null ItemUnit.");
        this.restriction = new ItemRestriction(ImmutableSet.of());
    }

    public int getId() {
        return id;
    }

    public String getCommodityCode() {
        return commodityCode;
    }

    public String getDescription() {
        return description;
    }

    public String getUnitDescription() {
        return unit.getDescription();
    }

    public Category getCategory() {
        return category;
    }

    public int getOrderMaxQty() {
        return allowance.getPerOrderAllowance();
    }

    public int getMonthlyMaxQty() {
        return allowance.getPerMonthAllowance();
    }

    public int getUnitQuantity() {
        return unit.getQuantity();
    }

    public boolean isVisible() {
        return status.isVisible();
    }

    public boolean isSpecialRequest() {
        return status.isSpecialRequest();
    }

    public boolean requiresSynchronization() {
        return status.requiresSynchronization();
    }

    public boolean isExpendable() {
        return status.isExpendable();
    }

    public void setRestriction(ItemRestriction restriction) {
        this.restriction = restriction == null ? new ItemRestriction(ImmutableSet.of()) : restriction;
    }

    public boolean isRestricted() {
        return this.restriction.isRestricted();
    }

    public boolean isAllowed(LocationId locId) {
        return restriction.isAllowed(locId);
    }

    public static class Builder {
        private int id;
        private String commodityCode;
        private String description;
        private ItemStatus status;
        private Category category;
        private ItemAllowance allowance;
        private ItemUnit unit;

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withCommodityCode(String commodityCode) {
            this.commodityCode = commodityCode;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withStatus(ItemStatus status) {
            this.status = status;
            return this;
        }

        public Builder withCategory(Category category) {
            this.category = category;
            return this;
        }

        public Builder withAllowance(ItemAllowance allowance) {
            this.allowance = allowance;
            return this;
        }

        public Builder withUnit(ItemUnit unit) {
            this.unit = unit;
            return this;
        }

        public SupplyItem build() {
            return new SupplyItem(this);
        }
    }

    @Override
    public String toString() {
        return "SupplyItem{" +
                "id=" + id +
                ", commodityCode='" + commodityCode + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", category=" + category +
                ", allowance=" + allowance +
                ", unit=" + unit +
                ", restriction=" + restriction +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupplyItem that = (SupplyItem) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
