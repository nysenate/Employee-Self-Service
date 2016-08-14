package gov.nysenate.ess.time.model.attendance;

public class TimeEntryNotFoundEx extends TimeEntryException
{
    public TimeEntryNotFoundEx(){}

    public TimeEntryNotFoundEx(String message){
        super(message);
    }

    public TimeEntryNotFoundEx(String message, Throwable cause){
        super(message, cause);
    }

}
