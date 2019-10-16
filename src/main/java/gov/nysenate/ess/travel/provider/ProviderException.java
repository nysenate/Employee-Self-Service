package gov.nysenate.ess.travel.provider;

/**
 * Indicates an exception communicating with our 3rd party providers.
 */
public class ProviderException extends RuntimeException {

    public ProviderException() {
        super();
    }

    public ProviderException(String message) {
        super(message);
    }

    public ProviderException(Throwable cause) {
        super(cause);
    }

    public ProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
