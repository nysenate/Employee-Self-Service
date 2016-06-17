package gov.nysenate.ess.core.service.notification.base.handler.base;

import com.google.common.eventbus.Subscribe;
import gov.nysenate.ess.core.service.notification.base.handler.exception.UnSupportMessageException;

/**
 * Created by senateuser on 6/14/2016.
 */
public interface Handler<T> {
    @Subscribe
    void handle(T message) throws UnSupportMessageException, ClassNotFoundException;

}
