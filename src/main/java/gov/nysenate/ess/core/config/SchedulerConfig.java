package gov.nysenate.ess.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.annotation.PreDestroy;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@EnableScheduling
public class SchedulerConfig implements SchedulingConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);

    /**
     * Used to prevent Tomcat having to forcibly unregister the JDBC driver.
     * Code taken from <a href="https://stackoverflow.com/a/23912257">here</a>
     */
//    @PreDestroy
//    private void destroyContext() {
//        ClassLoader cl = Thread.currentThread().getContextClassLoader();
//        Enumeration<Driver> drivers = DriverManager.getDrivers();
//        while (drivers.hasMoreElements()) {
//            Driver driver = drivers.nextElement();
//            if (driver.getClass().getClassLoader() == cl) {
//                // This driver was registered by the webapp's ClassLoader, so deregister it:
//                try {
//                    logger.info("De-registering JDBC driver {}", driver);
//                    DriverManager.deregisterDriver(driver);
//                }
//                catch (SQLException ex) {
//                    logger.error("Error de-registering JDBC driver {}", driver, ex);
//                }
//            } else
//                logger.trace("JDBC driver {} as it does not belong to this webapp's ClassLoader", driver);
//        }
//    }

    @Bean(name = "essScheduler", destroyMethod = "shutdownNow")
    public ScheduledExecutorService schedulerThreadPool() {
        return Executors.newScheduledThreadPool(4, new CustomizableThreadFactory("scheduler"));
    }

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler getTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(8);
        scheduler.initialize();
        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(schedulerThreadPool());
    }
}
