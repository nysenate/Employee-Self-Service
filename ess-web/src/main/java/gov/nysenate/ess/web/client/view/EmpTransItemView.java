package gov.nysenate.ess.web.client.view;

import gov.nysenate.ess.web.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.transaction.TransactionColumn;

public class EmpTransItemView implements ViewObject
{
    protected String desc;
    protected String value;

    public EmpTransItemView(String code, String value) {
        String desc = TransactionColumn.valueOf(code).getDesc();
        if (desc.isEmpty()) desc = code;
        this.desc = desc;
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getViewType() {
        return "transaction item";
    }
}
