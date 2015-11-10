package gov.nysenate.ess.seta;

import gov.nysenate.ess.core.BaseTests;
import gov.nysenate.ess.seta.config.SetaConfig;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = SetaConfig.class)
public abstract class SetaTests extends BaseTests {
}
