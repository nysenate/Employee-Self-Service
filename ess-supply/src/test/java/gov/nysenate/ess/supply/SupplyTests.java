package gov.nysenate.ess.supply;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.BaseTests;
import gov.nysenate.ess.supply.config.SupplyConfig;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;

@ContextConfiguration(classes = SupplyConfig.class)
public abstract class SupplyTests extends BaseTests {

    protected static final Range<LocalDate> ONE_WEEK_RANGE = Range.closed(LocalDate.now().minusWeeks(1), LocalDate.now());
    protected static final Range<LocalDate> THREE_MONTH_RANGE = Range.closed(LocalDate.now().minusMonths(3), LocalDate.now());
    protected static final Range<LocalDate> SIX_MONTH_RANGE = Range.closed(LocalDate.now().minusMonths(6), LocalDate.now());
}
