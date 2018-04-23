package gov.nysenate.ess.supply.reconcilation.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.reconcilation.model.RecOrder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A View representing a Reconciliation Order
 */

public class RecOrderView implements ViewObject {

    protected String itemId;
    protected int quantity;

    public RecOrderView(){}

    public RecOrderView(RecOrder recOrder){
        this.itemId = recOrder.getItemId();
        this.quantity = recOrder.getQuantity();
    }

    @JsonIgnore
    public RecOrder toRecOrder(){
        return new RecOrder.Builder()
                .withItemId(itemId)
                .withQuantity(quantity)
                .build();
    }


    public String getItemId(){return itemId;}


    public int getQuantity(){return quantity;}

    @Override
    public String getViewType() {
        return "Reconciliation Order";
    }
}
