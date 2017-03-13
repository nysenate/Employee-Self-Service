package gov.nysenate.ess.supply.item.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.item.model.*;

public class SupplyItemView implements Comparable<SupplyItemView>, ViewObject {

    protected int id;
    protected String commodityCode;
    protected String description;
    protected String unit; // * Oracle-Synchronization depends on this field name.
    protected String category;
    protected int perOrderAllowance;
    protected int perMonthAllowance;
    protected int unitQuantity;
    protected boolean isVisible;
    protected boolean isSpecialRequest;
    protected boolean isInventoryTracked;
    protected boolean isExpendable;
    protected boolean isRestricted;
    protected int reconciliationPage;

    public SupplyItemView() {
    }

    public SupplyItemView(SupplyItem item) {
        this.id = item.getId();
        this.commodityCode = item.getCommodityCode();
        this.description = item.getDescription();
        this.unit = item.getUnitDescription();
        this.category = item.getCategory().getName();
        this.perOrderAllowance = item.getOrderMaxQty();
        this.perMonthAllowance = item.getMonthlyMaxQty();
        this.unitQuantity = item.getUnitQuantity();
        this.isVisible = item.isVisible();
        this.isSpecialRequest = item.isSpecialRequest();
        this.isInventoryTracked = item.requiresSynchronization();
        this.isExpendable = item.isExpendable();
        this.isRestricted = item.isRestricted();
        this.reconciliationPage = item.getReconciliationPage();
    }

    public SupplyItem toSupplyItem() {
        return new SupplyItem.Builder()
                .withId(id)
                .withCommodityCode(commodityCode)
                .withDescription(description)
                .withStatus(new ItemStatus(isExpendable, isInventoryTracked, isVisible, isSpecialRequest))
                .withCategory(new Category(category))
                .withAllowance(new ItemAllowance(perOrderAllowance, perMonthAllowance))
                .withUnit(new ItemUnit(unit, unitQuantity))
                .withReconciliationPage(this.reconciliationPage)
                .build();
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

    public String getUnit() {
        return unit;
    }

    public String getCategory() {
        return category;
    }

    public int getPerOrderAllowance() {
        return perOrderAllowance;
    }

    public int getPerMonthAllowance() {
        return perMonthAllowance;
    }

    public int getUnitQuantity() {
        return unitQuantity;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isSpecialRequest() {
        return isSpecialRequest;
    }

    public boolean isExpendable() {
        return isExpendable;
    }

    public boolean isInventoryTracked() {
        return isInventoryTracked;
    }

    public boolean isRestricted() {
        return isRestricted;
    }

    public int getReconciliationPage() {
        return reconciliationPage;
    }

    @Override
    public String getViewType() {
        return "Supply Item";
    }

    @Override
    public int compareTo(SupplyItemView o) {
        return this.description.compareTo(o.description);
    }
}
