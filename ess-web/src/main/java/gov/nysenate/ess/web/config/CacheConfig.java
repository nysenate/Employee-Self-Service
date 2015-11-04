package gov.nysenate.ess.web.config;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer
{

    @Value("${cache.max.size}") private String cacheMaxHeapSize;

    @Bean(destroyMethod = "shutdown")
    public net.sf.ehcache.CacheManager pooledCacheManger() {
        // Set the upper limit when computing heap size for objects. Once it reaches the limit
        // it stops computing further. Some objects can contain many references so we set the limit
        // fairly high.
        SizeOfPolicyConfiguration sizeOfConfig = new SizeOfPolicyConfiguration();
        sizeOfConfig.setMaxDepth(100000);
        sizeOfConfig.setMaxDepthExceededBehavior("continue");

        // Configure the default cache to be used as a template for actual caches.
        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
        cacheConfiguration.addSizeOfPolicy(sizeOfConfig);

        // Configure the cache manager.
        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.setMaxBytesLocalHeap(cacheMaxHeapSize + "M");
        config.addDefaultCache(cacheConfiguration);
        config.setUpdateCheck(false);

        return net.sf.ehcache.CacheManager.newInstance(config);
    }

    @Override
    @Bean
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(pooledCacheManger());
    }

    @Override
    public CacheResolver cacheResolver() {
        return null;
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return null;
    }

    @Bean
    public List<Cache> springCaches() {
        Cache employeeCache = new ConcurrentMapCache("employees");
        Cache supervisorCache = new ConcurrentMapCache("supervisors");
        Cache supervisorEmpCache = new ConcurrentMapCache("supervisorEmps");
        Cache transactionCache = new ConcurrentMapCache("transactions");
        return Arrays.asList(employeeCache, supervisorCache, supervisorEmpCache, transactionCache);
    }
}