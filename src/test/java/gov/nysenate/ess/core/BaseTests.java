package gov.nysenate.ess.core;

import gov.nysenate.ess.core.config.CoreConfig;
import gov.nysenate.ess.web.config.WebApplicationConfig;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {TestConfig.class, CoreConfig.class})
//@ActiveProfiles("test")
//@Category(CoreTest.class)
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebApplicationConfig.class})
@ActiveProfiles("test")
public abstract class BaseTests {}
