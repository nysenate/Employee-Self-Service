package gov.nysenate.ess.core.config;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.filter.LevelMatchFilter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.util.ErrorHandler;

import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.concurrent.Executor;

@Configuration
@EnableScheduling
@EnableAsync
public class AsyncConfig implements SchedulingConfigurer, AsyncConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(AsyncConfig.class);
    private boolean isShuttingDown = false;

    @EventListener(ContextClosedEvent.class)
    public void onShutdown() {
        logger.info("Disabling warning logging during shutdown...");
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        ctx.addFilter(LevelMatchFilter.newBuilder().setLevel(Level.WARN)
                .setOnMatch(Filter.Result.DENY).setOnMismatch(Filter.Result.NEUTRAL).build());
        ctx.updateLoggers();
        isShuttingDown = true;
    }

    /**
     * Used to prevent Tomcat having to forcibly unregister the JDBC driver.
     * Code taken from <a href="https://stackoverflow.com/a/23912257">here</a>
     */
    @PreDestroy
    private void destroyContext() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Enumeration<Driver> drivers = DriverManager.getDrivers();

        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() == cl) {
                // This driver was registered by the webapp's ClassLoader, so deregister it:
                try {
                    logger.info("De-registering JDBC driver {}", driver);
                    DriverManager.deregisterDriver(driver);
                }
                catch (SQLException ex) {
                    logger.error("Error de-registering JDBC driver {}", driver, ex);
                }
            } else
                logger.trace("JDBC driver {} as it does not belong to this webapp's ClassLoader", driver);
        }
    }

    @Bean
    public ThreadPoolTaskScheduler getTaskScheduler() {
        var scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(8);
        scheduler.setThreadGroupName("scheduler");
        scheduler.setErrorHandler(new CustomThrowableHandler());
        scheduler.initialize();
        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(getTaskScheduler());
    }

    @Override
    @Bean(name = "essAsync")
    public Executor getAsyncExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setAwaitTerminationSeconds(10);
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomThrowableHandler();
    }

    private class CustomThrowableHandler implements ErrorHandler, AsyncUncaughtExceptionHandler {
        private static final Logger logger = LoggerFactory.getLogger(CustomThrowableHandler.class);

        @Override
        public void handleError(@NotNull Throwable t) {
            logThrowable("scheduled", t);
        }

        @Override
        public void handleUncaughtException(@NotNull Throwable ex, @NotNull Method method, @NotNull Object... params) {
            logThrowable("async", ex);
        }

        private void logThrowable(String type, Throwable t) {
            if (!isShuttingDown) {
                logger.error("Unexpected error occurred during {} task", type, t);
            }
        }
    }
}
