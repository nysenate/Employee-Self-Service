package gov.nysenate.ess.core.config;

import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Beans that implement {@link BeanPostProcessor} are configured here.
 * These beans must be configured separately because they are configured earlier in the process than most other beans.
 * This causes any other beans in the shared config class to be configured at the same time,
 *  which may occur before <code>@Autowired</code> or <code>@Value</code> aspects are evaluated
 * todo figure out why this occurs ^^
 */
@Configuration
public class BeanPostProcessorConfig {

    /**
     * Integrates Apache Shiro with Spring
     * @return LifecycleBeanPostProcessor
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
}
