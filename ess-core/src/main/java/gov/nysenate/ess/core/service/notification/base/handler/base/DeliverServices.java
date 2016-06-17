package gov.nysenate.ess.core.service.notification.base.handler.base;

/**
 * Created by Chenguang He on 6/16/2016.
 */
public interface DeliverServices<T> {

    void delivery(T message) throws ClassNotFoundException;
}
