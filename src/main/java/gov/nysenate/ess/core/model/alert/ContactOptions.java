package gov.nysenate.ess.core.model.alert;

/**
 * Enumerates different options for phone alert subscriptions.
 * Subscribers can receive only calls, only texts, or both.
 */
public enum ContactOptions {

    //          Calls   Texts
    CALLS_ONLY( true,   false, "Calls"),
    TEXTS_ONLY( false,  true, "Texts"),
    EVERYTHING( true,   true, "Both"),
    ;

    private final boolean callable;
    private final boolean textable;
    private final String jsString;

    ContactOptions(boolean callable, boolean textable, String jsString) {
        this.callable = callable;
        this.textable = textable;
        this.jsString = jsString;
    }

    public static ContactOptions fromJsString(String jsString) {
        for (ContactOptions option : values()) {
            if (option.jsString.equals(jsString))
                return option;
        }
        throw new IllegalArgumentException("No contact option exists with jsString = " + jsString);
    }

    public boolean isCallable() {
        return callable;
    }

    public boolean isTextable() {
        return textable;
    }

    public String getJsString() {
        return jsString;
    }

    /**
     * Get a {@link ContactOptions} with callable and textable flags equal to the parameters.
     *
     * @param callable boolean
     * @param textable boolean
     * @return {@link ContactOptions}
     */
    public static ContactOptions getContactOptions(boolean callable, boolean textable) {
        for (ContactOptions option : values()) {
            if (option.callable == callable && option.textable == textable)
                return option;
        }
        throw new IllegalArgumentException("No contact option exists with " +
                "callable = " + callable + " and textable = " + textable);
    }

}
