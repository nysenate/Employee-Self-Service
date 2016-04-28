package gov.nysenate.ess.supply;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import gov.nysenate.ess.core.TestConfig;
import gov.nysenate.ess.supply.config.SupplyConfig;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@RunWith(HierarchicalContextRunner.class)
@ContextConfiguration(classes = {TestConfig.class, SupplyConfig.class})
@ActiveProfiles("test")
public class SupplyUnitTests {
}
