package gov.nysenate.ess.core.service.alert;

import gov.nysenate.ess.core.model.alert.AlertInfo;
import gov.nysenate.ess.core.model.alert.InvalidAlertInfoEx;

/**
 * Provides a method that validates {@link AlertInfo}
 */
public interface AlertInfoValidationService {

    /**
     * Validates the given {@link AlertInfo}, throwing a {@link InvalidAlertInfoEx} if invalid data is detected
     * @param alertInfo {@link AlertInfo}
     * @throws InvalidAlertInfoEx
     */
    void validateAlertInfo(AlertInfo alertInfo) throws InvalidAlertInfoEx;

}
