package gov.nysenate.ess.core.model.personnel;

public class RespCtrMultipleMatchesEx extends RespCtrException {

    public RespCtrMultipleMatchesEx() {
    }

    public RespCtrMultipleMatchesEx(String message) {
        super(message);
    }

    public RespCtrMultipleMatchesEx(String message, Throwable cause) {
        super(message, cause);
    }
}
