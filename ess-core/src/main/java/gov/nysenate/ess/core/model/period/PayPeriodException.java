package gov.nysenate.ess.core.model.period;

public class PayPeriodException extends RuntimeException
{
    public PayPeriodException() {
    }

    public PayPeriodException(String message) {
        super(message);
    }

    public PayPeriodException(String message, Throwable cause) {
        super(message, cause);
    }
}
