package gov.nysenate.ess.supply.reconciliation;

/**
 * Created by Chenguang He on 7/28/2016.
 */
public class Reconciliation {
    private final String itemCatagory;
    private final int page;

    private Reconciliation(Builder builder) {
        this.itemCatagory = builder.itemCatagory;
        this.page = builder.page;
    }

    public int getPage() {
        return page;
    }

    public String getItemCatagory() {
        return itemCatagory;
    }

    public static class Builder {
        private String itemCatagory;
        private int page;

        public Builder withItemCatagory(String itemCatagory) {
            this.itemCatagory = itemCatagory;
            return this;
        }

        public Builder withPage(int page) {
            this.page = page;
            return this;
        }

        public Reconciliation build() {
            return new Reconciliation(this);
        }
    }
}
