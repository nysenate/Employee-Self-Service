package gov.nysenate.ess.core.service.mail;

import javax.mail.MessagingException;

/**
 * A wrapper of {@link MessagingException} that extends {@link RuntimeException} preventing the need for catching
 */
public class EssMessagingException extends RuntimeException {

    private static final long serialVersionUID = 8663783591300066901L;

    public EssMessagingException(MessagingException cause) {
        super(cause);
    }
}
