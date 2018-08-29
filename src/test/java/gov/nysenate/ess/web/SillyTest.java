package gov.nysenate.ess.web;

import gov.nysenate.ess.travel.application.TravelApplicationDao;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingAllowanceDao;
import gov.nysenate.ess.travel.application.allowances.mileage.MileageAllowanceDao;
import gov.nysenate.ess.travel.application.destination.DestinationDao;
import gov.nysenate.ess.travel.application.route.RouteDao;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * A sample file to run misc tests.
 */
@Category(gov.nysenate.ess.core.annotation.SillyTest.class)
public class SillyTest extends WebTest
{
    private static final Logger logger = LoggerFactory.getLogger(SillyTest.class);
}