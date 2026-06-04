package gov.nysenate.ess.core.client.view;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;

import java.time.LocalDateTime;
import java.util.Collection;

import jakarta.xml.bind.annotation.XmlElement;

public class RedmineEmpStatusChangeView extends RedmineEmployeeView {

    protected TransactionCode transactionCode;
    protected LocalDateTime postDateTime;
    protected String notes;

    public RedmineEmpStatusChangeView(Employee employee, TransactionRecord statusChangeRecord) {
        super(employee);
        this.transactionCode = statusChangeRecord.getTransCode();
        this.postDateTime = statusChangeRecord.getPostDate();
        this.notes = statusChangeRecord.getNote();
    }

    @XmlElement
    public TransactionCode getTransactionCode() {
        return transactionCode;
    }

    @XmlElement
    public LocalDateTime getPostDateTime() {
        return postDateTime;
    }

    @XmlElement
    public String getNotes() {
        return notes;
    }

    @Override
    public String getViewType() {
        return "redmine employee status change";
    }
}
