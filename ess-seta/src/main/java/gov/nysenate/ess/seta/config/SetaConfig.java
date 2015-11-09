package gov.nysenate.ess.seta.config;

import gov.nysenate.ess.core.config.BaseConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@ComponentScan({"gov.nysenate.ess.seta", "gov.nysenate.ess.core"})
@Profile({"test", "dev", "prod"})
@Import(BaseConfig.class)
public class SetaConfig {
}
