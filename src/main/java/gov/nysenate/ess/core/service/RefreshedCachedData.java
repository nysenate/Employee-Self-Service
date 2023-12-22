package gov.nysenate.ess.core.service;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.ess.core.config.InheritedService;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.support.CronExpression;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Some common code for classes that cache data for quicker access, where the cache is periodically refreshed.
 */
@InheritedService
public abstract class RefreshedCachedData<K, V> {
    private static final PooledTaskScheduler scheduler = new PooledTaskScheduler();
    private static final String defaultCron = "0 0 0 * * *";
    private final String cron;
    private ImmutableMap<K, V> dataMap = null;

    protected RefreshedCachedData(String cron) {
        this.cron = CronExpression.isValidExpression(cron) ? cron : defaultCron;
    }

    protected abstract Map<K, V> getMap();

    // Access to the internal map is only allowed through this method,
    // ensuring the map always exists, though it need not be created on start-up.
    protected Map<K, V> dataMap() {
        if (dataMap == null) {
            refreshData();
        }
        return dataMap;
    }

    protected Map<K, V> toMap(List<V> values, Function<V, K> keyFunction) {
        return values.stream().collect(Collectors.toMap(keyFunction, Function.identity()));
    }

    private void refreshData() {
        dataMap = ImmutableMap.copyOf(getMap());
    }

    @PostConstruct
    private void scheduler() {
        scheduler.scheduleCronTask(new CronTask(this::refreshData, cron));
    }
}
