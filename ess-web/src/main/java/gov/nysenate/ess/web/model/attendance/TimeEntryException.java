package gov.nysenate.ess.web.model.attendance;

public class TimeEntryException extends Exception
{
    public TimeEntryException(){}

    public TimeEntryException(String message){
        super(message);
    }

    public  TimeEntryException(String message, Throwable cause){
        super(message, cause);
    }
}
