package gov.nysenate.ess.core.model.transaction;

public class TransactionHistoryException extends Exception {
    public TransactionHistoryException() {
        super();
    }

    public TransactionHistoryException(String message) {
        super(message);
    }

    public TransactionHistoryException(String message, Throwable cause) {
        super(message, cause);
    }
}