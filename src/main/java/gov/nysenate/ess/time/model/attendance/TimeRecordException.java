package gov.nysenate.ess.time.model.attendance;

public class TimeRecordException extends RuntimeException
{
    private static final long serialVersionUID = -8720758842907502676L;

    public TimeRecordException(){}

    public TimeRecordException(String message){
        super(message);
    }

    public  TimeRecordException(String message, Throwable cause){
        super(message, cause);
    }


}
