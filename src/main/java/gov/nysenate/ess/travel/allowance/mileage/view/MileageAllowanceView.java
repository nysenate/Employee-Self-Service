package gov.nysenate.ess.travel.allowance.mileage.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.math.BigDecimal;

public class MileageAllowanceView implements ViewObject {

    private String mileage;

    public MileageAllowanceView(BigDecimal bigDecimal) {
        this.mileage = bigDecimal.toString();
    }

    public String getMileage() {
        return mileage;
    }

    @Override
    public String getViewType() {
        return "mileage-allowance-view";
    }
}
