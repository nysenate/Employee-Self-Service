package gov.nysenate.ess.supply.reconcilation.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.reconcilation.model.RecOrder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A View representing a Reconciliation Order
 */
@JsonFormat(shape=JsonFormat.Shape.ARRAY)
@XmlRootElement
public class RecOrderView implements ViewObject {

    protected int itemId;
    protected int quantity;

    public RecOrderView(){}

    public RecOrderView(RecOrder recOrder){
        this.itemId = Integer.valueOf(recOrder.getItemId());
        this.quantity = recOrder.getQuantity();
    }

    @JsonIgnore
    public RecOrder toRecOrder(){
        return new RecOrder.Builder()
                .withItemId(String.valueOf(itemId))
                .withQuantity(quantity)
                .build();
    }


    public int getItemId(){return itemId;}


    public int getQuantity(){return quantity;}

    @Override
    public String getViewType() {
        return "Reconciliation Order";
    }
}
