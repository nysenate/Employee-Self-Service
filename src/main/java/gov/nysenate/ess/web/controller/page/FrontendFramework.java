package gov.nysenate.ess.web.controller.page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Supported front-end implementations for an ESS application.
 */
public enum FrontendFramework {
    ANGULARJS,
    REACT;

    private static final Logger logger = LoggerFactory.getLogger(FrontendFramework.class);

    /**
     * Parses a front-end framework property, defaulting to AngularJS when the property is absent
     * or has an unsupported value.
     */
    public static FrontendFramework fromProperty(String propertyName, String configuredValue) {
        if (configuredValue == null || configuredValue.isBlank()) {
            logger.warn("{} is not configured; defaulting to angularjs.", propertyName);
            return ANGULARJS;
        }

        return switch (configuredValue.trim().toLowerCase(Locale.ROOT)) {
            case "angularjs" -> ANGULARJS;
            case "react" -> REACT;
            default -> {
                logger.warn("Invalid value '{}' for {}; expected 'angularjs' or 'react'. Defaulting to angularjs.",
                        configuredValue, propertyName);
                yield ANGULARJS;
            }
        };
    }
}
