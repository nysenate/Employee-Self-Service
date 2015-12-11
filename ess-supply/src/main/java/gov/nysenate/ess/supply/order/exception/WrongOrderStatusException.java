package gov.nysenate.ess.supply.order.exception;

public class WrongOrderStatusException extends RuntimeException {

    public WrongOrderStatusException() {

    }

    public WrongOrderStatusException(String message) {
        super(message);
    }
}
