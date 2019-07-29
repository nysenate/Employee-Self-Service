package gov.nysenate.ess.supply.reconcilation;

import com.google.common.collect.Lists;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.model.SupplyItem;
import gov.nysenate.ess.supply.item.view.SupplyItemView;
import gov.nysenate.ess.supply.reconcilation.model.Inventory;
import gov.nysenate.ess.supply.reconcilation.model.InventoryView;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.requisition.view.RequisitionView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ReconciliationDtoView implements ViewObject {

    // Requisitions which require reconciliation.
    protected List<RequisitionView> requisitions;
    protected Map<Integer, List<RequisitionView>> itemIdToRequisitions;
    // Inventory counts for these items must be given to perform the reconciliation.
    protected Set<SupplyItemView> items;
    protected InventoryView inventory;

    public ReconciliationDtoView() {
    }

    public ReconciliationDtoView(List<Requisition> requisitions, Set<SupplyItem> items, Inventory inventory) {
        this.requisitions = requisitions.stream()
                .map(RequisitionView::new)
                .collect(Collectors.toList());

        // Create itemIdToRequisitions map
        itemIdToRequisitions = new HashMap<>();
        for (Requisition r : requisitions) {
            Set<SupplyItem> rItems = r.getLineItems().stream()
                    .map(LineItem::getItem)
                    .collect(Collectors.toSet());
            for (SupplyItem i : rItems) {
                int id = i.getId();
                if (itemIdToRequisitions.containsKey(id)) {
                    itemIdToRequisitions.get(id).add(new RequisitionView(r));
                } else {
                    List<RequisitionView> l = Lists.newArrayList(new RequisitionView(r));
                    itemIdToRequisitions.put(id, l);
                }
            }
        }

        this.items = items.stream()
                .map(SupplyItemView::new)
                .collect(Collectors.toSet());

        this.inventory = new InventoryView(inventory);
    }

    public List<RequisitionView> getRequisitions() {
        return requisitions;
    }

    public Map<Integer, List<RequisitionView>> getItemIdToRequisitions() {
        return itemIdToRequisitions;
    }

    public Set<SupplyItemView> getItems() {
        return items;
    }

    public InventoryView getInventory() {
        return inventory;
    }

    @Override
    public String getViewType() {
        return "reconciliation-dto";
    }
}
