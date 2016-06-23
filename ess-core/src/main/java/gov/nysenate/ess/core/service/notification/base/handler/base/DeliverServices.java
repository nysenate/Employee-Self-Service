package gov.nysenate.ess.core.service.notification.base.handler.base;

/**
 *  This interface is used by event bus class to inject the services to message handler.
 *  the message is processing in delivery method.
 * Created by Chenguang He on 6/16/2016.
 */
public interface DeliverServices<T> {
    /**
     * deliver message to handlers
     *
     * @param message the message
     * @throws ClassNotFoundException if the message is not fixed in specific type of handle.
     */
    void delivery(T message) throws ClassNotFoundException;
}
