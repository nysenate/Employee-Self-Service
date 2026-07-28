package gov.nysenate.ess.web.controller.page;

import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static gov.nysenate.ess.web.controller.page.FrontendFramework.ANGULARJS;
import static gov.nysenate.ess.web.controller.page.FrontendFramework.REACT;
import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class FrontendFrameworkTest {

    private static final String PROPERTY_NAME = "frontend.test.framework";

    @Test
    public void parsesSupportedFrameworksCaseInsensitively() {
        assertEquals(ANGULARJS, FrontendFramework.fromProperty(PROPERTY_NAME, "angularjs"));
        assertEquals(REACT, FrontendFramework.fromProperty(PROPERTY_NAME, "react"));
        assertEquals(REACT, FrontendFramework.fromProperty(PROPERTY_NAME, " React "));
    }

    @Test
    public void missingValueDefaultsToAngularJs() {
        assertEquals(ANGULARJS, FrontendFramework.fromProperty(PROPERTY_NAME, null));
        assertEquals(ANGULARJS, FrontendFramework.fromProperty(PROPERTY_NAME, ""));
        assertEquals(ANGULARJS, FrontendFramework.fromProperty(PROPERTY_NAME, "  "));
    }

    @Test
    public void invalidValueDefaultsToAngularJs() {
        assertEquals(ANGULARJS, FrontendFramework.fromProperty(PROPERTY_NAME, "vue"));
    }
}
