package gov.nysenate.ess.core.model.alert;

/**
 * Enumerates different options for mobile phone alert subscriptions.
 * Subscribers can receive only calls, only texts, or both.
 */
public enum MobileContactOptions {

    //          Calls   Texts
    CALLS_ONLY( true,   false),
    TEXTS_ONLY( false,  true),
    EVERYTHING( true,   true),
    ;

    private boolean callable;
    private boolean textable;

    MobileContactOptions(boolean callable, boolean textable) {
        this.callable = callable;
        this.textable = textable;
    }

    public boolean isCallable() {
        return callable;
    }

    public boolean isTextable() {
        return textable;
    }
}
