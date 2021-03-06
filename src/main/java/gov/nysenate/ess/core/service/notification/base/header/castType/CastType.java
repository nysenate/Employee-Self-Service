package gov.nysenate.ess.core.service.notification.base.header.castType;

import gov.nysenate.ess.core.service.notification.base.header.base.Header;

/**
 *  the type of cast
 * Created by Chenguang He  on 6/15/2016.
 */
public interface CastType extends Header {
    StringBuilder castType = new StringBuilder(Header.header).append("." + CastType.class.getSimpleName());
}
