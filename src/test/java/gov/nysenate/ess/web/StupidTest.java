package gov.nysenate.ess.web;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Category(gov.nysenate.ess.core.annotation.SillyTest.class)
public class StupidTest {
    private static final Logger logger = LoggerFactory.getLogger(StupidTest.class);

    interface A {
    }
    interface B extends A{
    }
    interface C extends A{
    }
    class D implements B, C {
    }

    public void funk(A a) {
        logger.info("A");
    }

    public void funk(B b) {
        logger.info("B");
    }

    public void funk(C c) {
        logger.info("C");
    }

    @Test
    public void typeTest() {
    }
}
