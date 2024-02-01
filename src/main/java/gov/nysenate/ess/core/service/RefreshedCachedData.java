package gov.nysenate.ess.core.service;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.ess.core.config.InheritedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Some common code for classes that cache data for quicker access, where the cache is periodically refreshed.
 */
@InheritedService
public abstract class RefreshedCachedData<K, V> {
    private static final String defaultCron = "0 0 0 * * *";
    @Autowired()
    private ThreadPoolTaskScheduler cronScheduler;
    private final CronTrigger cronTrigger;
    private final Supplier<Map<K, V>> mapSupplier;
    private ImmutableMap<K, V> dataMap = null;

    protected RefreshedCachedData(Supplier<Map<K, V>> mapSupplier) {
        this(defaultCron, mapSupplier);
    }

    protected RefreshedCachedData(String cron, Supplier<Map<K, V>> mapSupplier) {
        this.cronTrigger = new CronTrigger(cron);
        this.mapSupplier = mapSupplier;
    }

    /**
     * Access to the internal map is only allowed through this method,
     * ensuring the map always exists, though it need not be created on start-up.
     * @return the current data.
     */
    protected Map<K, V> dataMap() {
        if (dataMap == null) {
            refreshData();
        }
        return dataMap;
    }

    private void refreshData() {
        dataMap = ImmutableMap.copyOf(mapSupplier.get());
    }

    @PostConstruct
    private void scheduler() {
        cronScheduler.schedule(this::refreshData, cronTrigger);
    }
}
