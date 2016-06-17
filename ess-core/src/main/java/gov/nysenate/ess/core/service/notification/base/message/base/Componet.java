package gov.nysenate.ess.core.service.notification.base.message.base;

/**
 * Created by senateuser on 6/14/2016.
 */
public interface Componet {

    StringBuilder path = new StringBuilder(Componet.class.getSimpleName());

    int getComponetId();

    void setComponetId(int id);

    void attachTo(Message message);
}
