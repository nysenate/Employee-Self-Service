package gov.nysenate.ess.core.client.view;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;

import java.time.LocalDateTime;
import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;

public class BACHelpEmpStatusChangeView extends BACHelpEmployeeView {

    protected TransactionCode transactionCode;
    protected LocalDateTime postDateTime;

    public BACHelpEmpStatusChangeView(Employee employee, TransactionRecord statusChangeRecord) {
        super(employee);
        this.transactionCode = statusChangeRecord.getTransCode();
        this.postDateTime = statusChangeRecord.getPostDate();
    }

    @XmlElement
    public TransactionCode getTransactionCode() {
        return transactionCode;
    }

    @XmlElement
    public LocalDateTime getPostDateTime() {
        return postDateTime;
    }

    @Override
    public String getViewType() {
        return "bachelp employee status change";
    }
}
