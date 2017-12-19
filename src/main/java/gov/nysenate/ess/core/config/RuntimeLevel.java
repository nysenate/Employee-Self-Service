package gov.nysenate.ess.core.config;

import org.apache.commons.lang3.StringUtils;

/**
 * Enum modeling the possible runtime levels of the application.
 * Runtime level is configurable in the app properties with each
 * level being appropriate for a particular instance of ESS.
 */
public enum RuntimeLevel {

    DEV,
    TEST,
    PROD,
    ;

    public static RuntimeLevel of(String runtimeLevel) throws IllegalArgumentException {
        return valueOf(StringUtils.upperCase(runtimeLevel));
    }
}
