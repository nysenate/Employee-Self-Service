package gov.nysenate.ess.core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"test", "dev", "prod"})
@Import({PropertyConfig.class, LdapConfig.class, EventBusConfig.class, SchedulerConfig.class, AsyncConfig.class,
        DbConnectionPoolConfig.class, DatabaseConfig.class, CacheConfig.class, BeanPostProcessorConfig.class,
        FreemarkerConfig.class, JacksonConfig.class})
@ComponentScan({"gov.nysenate.ess.core", "gov.nysenate.ess.time", "gov.nysenate.ess.supply", "gov.nysenate.ess.travel", "gov.nysenate.ess.web"})
public class CoreConfig {
}
