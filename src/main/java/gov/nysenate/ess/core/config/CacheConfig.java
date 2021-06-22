package gov.nysenate.ess.core.config;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

import static net.sf.ehcache.config.SizeOfPolicyConfiguration.MaxDepthExceededBehavior.CONTINUE;

@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer
{
    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);
    @Value("${cache.max.size}") private String cacheMaxHeapSize;

    @Bean(destroyMethod = "shutdown")
    public net.sf.ehcache.CacheManager pooledCacheManger() {
        // Set the upper limit when computing heap size for objects. Once it reaches the limit
        // it stops computing further. Some objects can contain many references so we set the limit
        // fairly high.
        SizeOfPolicyConfiguration sizeOfConfig = new SizeOfPolicyConfiguration();
        sizeOfConfig.setMaxDepth(100000);
        sizeOfConfig.maxDepthExceededBehavior(CONTINUE);

        // Configure the default cache to be used as a template for actual caches.
        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
        cacheConfiguration.addSizeOfPolicy(sizeOfConfig);

        // Configure the cache manager.
        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.setMaxBytesLocalHeap(cacheMaxHeapSize + "M");
        config.addDefaultCache(cacheConfiguration);
        config.sizeOfPolicy(sizeOfConfig);

        return net.sf.ehcache.CacheManager.newInstance(config);
    }

    @Override
    @Bean
    public CacheManager cacheManager() {
        net.sf.ehcache.CacheManager cacheManager = pooledCacheManger();
        logger.info("{}", cacheManager.getConfiguration().getSizeOfPolicyConfiguration().getMaxDepth());

        EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager(cacheManager);
        logger.info("{}", Objects.requireNonNull(ehCacheCacheManager.getCacheManager()).getConfiguration().getSizeOfPolicyConfiguration().getMaxDepth());
        return ehCacheCacheManager;
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
}