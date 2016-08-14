package gov.nysenate.ess.supply.reconciliation.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.reconciliation.Reconciliation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Chenguang He on 8/3/2016.
 */
@XmlRootElement
public class ReconciliationView implements ViewObject {
    protected int page;
    protected String item_category;

    public ReconciliationView() {
    }

    public ReconciliationView(int page, String item_category) {
        this.page = page;
        this.item_category = item_category;
    }

    public ReconciliationView(Reconciliation reconciliation) {
        this.item_category = reconciliation.getItemCatagory();
        this.page = reconciliation.getPage();
    }

    @XmlElement
    public int getPage() {
        return page;
    }

    @XmlElement
    public String getItem_category() {
        return item_category;
    }

    @XmlElement
    public void setPage(int page) {
        this.page = page;
    }

    @XmlElement
    public void setItem_category(String item_category) {
        this.item_category = item_category;
    }

    @Override
    public String getViewType() {
        return "reconciliation";
    }
}
