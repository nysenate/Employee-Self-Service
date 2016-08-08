package gov.nysenate.ess.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"test", "dev", "prod"})
@Import({PropertyConfig.class, LdapConfig.class, EventBusConfig.class, SchedulerConfig.class,
        DbConnectionPoolConfig.class, DatabaseConfig.class, CacheConfig.class, BeanPostProcessorConfig.class})
public class BaseConfig {}
