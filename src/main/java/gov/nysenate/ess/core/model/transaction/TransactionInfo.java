package gov.nysenate.ess.core.model.transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Contains metadata for a single employee payroll/personnel transaction.
 * Does not contain any of the modified data
 * @see TransactionRecord
 */
public class TransactionInfo {

    /** The employee id that this record belongs to. */
    protected int employeeId;

    /** A reference to the change id used for grouping transactions. */
    protected int changeId;

    /** Indicates if record is active or inactive. */
    protected boolean active;

    /** The transaction code indicates the kinds of changes made. */
    protected TransactionCode transCode;

    /** Document Id for the transaction
     *  For temporary employees, this corresponds to a pay period and is prefixed with 'T'
     *  For annual employees, this uniquely identifies a group of transactions */
    protected String documentId;

    /** The date in which this transaction is effective. */
    protected LocalDate effectDate;

    /** The date when this record was created. */
    protected LocalDateTime originalDate;

    /** The date when this record was updated. */
    protected LocalDateTime updateDate;

    /** --- Constructors --- */

    public TransactionInfo() {}

    public TransactionInfo(TransactionInfo other) {
        this.employeeId = other.employeeId;
        this.changeId = other.changeId;
        this.active = other.active;
        this.transCode = other.transCode;
        this.documentId = other.documentId;
        this.effectDate = other.effectDate;
        this.originalDate = other.originalDate;
        this.updateDate = other.updateDate;
    }

    /** --- Functional Getters / Setters --- */

    public TransactionType getTransType() {
        return transCode.getType();
    }

    /** --- Getters / Setters --- */

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getChangeId() {
        return changeId;
    }

    public void setChangeId(int changeId) {
        this.changeId = changeId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public TransactionCode getTransCode() {
        return transCode;
    }

    public void setTransCode(TransactionCode transCode) {
        this.transCode = transCode;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public LocalDate getEffectDate() {
        return effectDate;
    }

    public void setEffectDate(LocalDate effectDate) {
        this.effectDate = effectDate;
    }

    public LocalDateTime getOriginalDate() {
        return originalDate;
    }

    public void setOriginalDate(LocalDateTime originalDate) {
        this.originalDate = originalDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }
}
