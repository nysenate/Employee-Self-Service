package gov.nysenate.ess.core.service.notification.base.handler.base;

import com.google.common.eventbus.Subscribe;
import gov.nysenate.ess.core.service.notification.base.handler.exception.UnSupportMessageException;

/**
 *  message handler class
 * Created by Chenguang He  on 6/14/2016.
 */
public interface Handler<T> {
    /**
     * this method is subscribed to google event buss
     * it's used to handle the message.
     *
     * @param message message
     * @throws UnSupportMessageException throw if the message can not be handled by this handler
     * @throws ClassNotFoundException    throw if the message can not be casted into specific type
     */
    @Subscribe
    void handle(T message) throws UnSupportMessageException, ClassNotFoundException;

}
