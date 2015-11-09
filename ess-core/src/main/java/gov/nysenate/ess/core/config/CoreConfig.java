package gov.nysenate.ess.core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"test", "dev", "prod"})
@ComponentScan("gov.nysenate.ess.core")
@Import(BaseConfig.class)
public class CoreConfig {}
