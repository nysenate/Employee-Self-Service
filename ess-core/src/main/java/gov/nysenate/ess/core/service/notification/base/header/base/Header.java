package gov.nysenate.ess.core.service.notification.base.header.base;

import java.util.Map;

/**
 * Created by senateuser on 6/14/2016.
 */
public interface Header {
    StringBuilder header = new StringBuilder(Header.class.getSimpleName());

    String getHeaderName();

    String getHeaderValue();

    Map<String, String> toMap();
}
