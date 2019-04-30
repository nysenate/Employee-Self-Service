package gov.nysenate.ess.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Profile("!test")
@EnableScheduling
@Configuration
public class SchedulerConfig implements SchedulingConfigurer
{

    @Bean(name = "essScheduler", destroyMethod = "shutdownNow")
    public ScheduledExecutorService schedulerThreadPool() {
        return Executors.newScheduledThreadPool(4, new CustomizableThreadFactory("scheduler"));
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(schedulerThreadPool());
    }
}
