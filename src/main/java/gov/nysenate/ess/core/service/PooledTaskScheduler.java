package gov.nysenate.ess.core.service;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.FixedRateTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

public class PooledTaskScheduler {
    private final ScheduledTaskRegistrar taskReg = new ScheduledTaskRegistrar();
    private final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();

    public PooledTaskScheduler() {
        taskScheduler.initialize();
        taskReg.setScheduler(taskScheduler);
    }

    public void scheduleCronTask(CronTask task) {
        incrementPool();
        taskReg.scheduleCronTask(task);
    }

    public void scheduleFixedRateTask(FixedRateTask task) {
        incrementPool();
        taskReg.scheduleFixedRateTask(task);
    }

    private void incrementPool() {
        taskScheduler.setPoolSize(taskScheduler.getPoolSize() + 1);
    }
}
