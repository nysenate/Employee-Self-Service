package gov.nysenate.ess.core.service.notification.base.handler.base;

/**
 * Created by senateuser on 6/15/2016.
 */
public class GenericClass<T> {

    private final Class<T> type;

    public GenericClass(Class<T> type) {
        this.type = type;
    }

    public Class<T> getMyType() {
        return this.type;
    }
}