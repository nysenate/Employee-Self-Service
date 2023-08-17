package gov.nysenate.ess.core.client.view.base;

import java.math.BigDecimal;

public class BigDecimalView implements ViewObject {
    protected BigDecimal value;

    public BigDecimalView(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String getViewType() {
        return "big-decimal-view";
    }
}
