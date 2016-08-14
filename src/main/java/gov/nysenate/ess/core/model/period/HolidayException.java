package gov.nysenate.ess.core.model.period;

public class HolidayException extends RuntimeException
{
    private static final long serialVersionUID = -8378816826233312846L;

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
