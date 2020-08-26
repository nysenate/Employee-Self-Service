package gov.nysenate.ess.core.model.alert;

/**
 * Enumerates different options for phone alert subscriptions.
 * Subscribers can receive only calls, only texts, or both.
 */
public enum ContactOptions {

    //          Calls   Texts
    CALLS_ONLY( true,   false),
    TEXTS_ONLY( false,  true),
    EVERYTHING( true,   true),
    ;

    private final boolean callable;
    private final boolean textable;

    ContactOptions(boolean callable, boolean textable) {
        this.callable = callable;
        this.textable = textable;
    }

    public boolean isCallable() {
        return callable;
    }

    public boolean isTextable() {
        return textable;
    }

    /**
     * Get a {@link ContactOptions} with callable and textable flags equal to the parameters.
     *
     * @param callable boolean
     * @param textable boolean
     * @return {@link ContactOptions}
     */
    public static ContactOptions getContactOptions(boolean callable, boolean textable) {
        for (ContactOptions option : ContactOptions.values()) {
            if (option.callable == callable && option.textable == textable)
                return option;
        }
        throw new IllegalArgumentException("No contact option exists with " +
                "callable = " + callable + " and textable = " + textable);
    }

}
