package gov.nysenate.ess.supply.config;

import gov.nysenate.ess.core.config.BaseConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@ComponentScan({"gov.nysenate.ess.core", "gov.nysenate.ess.supply"})
@Profile({"test", "dev", "prod"})
@Import(BaseConfig.class)
public class SupplyConfig {
}
