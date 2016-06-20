package gov.nysenate.ess.core.service.notification.base.message.base;

/**
 * Created by senateuser on 6/14/2016.
 */
public interface Component {

    StringBuilder path = new StringBuilder(Component.class.getSimpleName());

    int getComponetId();

    void attachTo(Message message);
}
