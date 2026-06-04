package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;

/**
 * Test-only helper for suppressing expected log noise while preserving production log levels.
 */
final class TestLoggerControl {

    private final String loggerName;
    private final Level originalLevel;

    private TestLoggerControl(Class<?> loggerClass) {
        this.loggerName = loggerClass.getName();
        this.originalLevel = getCurrentLevel(loggerName);
    }

    static TestLoggerControl suppress(Class<?> loggerClass) {
        TestLoggerControl control = new TestLoggerControl(loggerClass);
        Configurator.setLevel(control.loggerName, Level.OFF);
        return control;
    }

    void restore() {
        Configurator.setLevel(loggerName, originalLevel);
    }

    private static Level getCurrentLevel(String loggerName) {
        LoggerContext context = LoggerContext.getContext(false);
        Configuration configuration = context.getConfiguration();
        return configuration.getLoggerConfig(loggerName).getLevel();
    }
}
