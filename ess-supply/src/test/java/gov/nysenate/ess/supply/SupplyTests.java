package gov.nysenate.ess.supply;

import gov.nysenate.ess.core.BaseTests;
import gov.nysenate.ess.supply.config.SupplyConfig;
import gov.nysenate.ess.supply.order.dao.InMemoryOrderDao;
import gov.nysenate.ess.supply.order.dao.OrderDao;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = SupplyConfig.class)
public abstract class SupplyTests extends BaseTests {

}
