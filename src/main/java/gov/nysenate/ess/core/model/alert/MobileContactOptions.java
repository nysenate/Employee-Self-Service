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

    /**
     * Get a {@link MobileContactOptions} with callable and textable flags equal to the parameters.
     *
     * @param callable boolean
     * @param textable boolean
     * @return {@link MobileContactOptions}
     */
    public static MobileContactOptions getMobileContactOption(boolean callable, boolean textable) {
        for (MobileContactOptions option : MobileContactOptions.values()) {
            if (option.callable == callable && option.textable == textable) {
                return option;
            }
        }
        throw new IllegalArgumentException("No mobile contact option exists with " +
                "callable = " + callable + " and textable = " + textable);
    }

}
