package gov.nysenate.ess.core.service.notification.base.header.base;

import java.util.Map;

/**
 * message header
 * Created by Chenguang He  on 6/14/2016.
 */
public interface Header {
    StringBuilder header = new StringBuilder(Header.class.getSimpleName());

    /**
     * get header name
     *
     * @return header name
     */
    String getHeaderName();

    /**
     * get header value
     * @return header value
     */
    String getHeaderValue();

    /**
     * get the map of header
     * @return map of header
     */
    Map<String, String> toMap();
}
