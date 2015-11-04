package gov.nysenate.ess.core.model.period;

public class HolidayException extends Exception
{
    public HolidayException() {
        super();
    }

    public HolidayException(String message) {
        super(message);
    }

    public HolidayException(String message, Throwable cause) {
        super(message, cause);
    }
}
