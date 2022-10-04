package gov.nysenate.ess.core.service.base;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.model.cache.CacheType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@Category(IntegrationTest.class)
public class CachingServiceTest extends BaseTest{

    @Autowired ApplicationContext context;

    @Test
    public void contentIdTypeTest() throws IOException {
        final String evictMethodName = "evictContent";
        // TODO: really shouldn't be using reflection here
        Collection<CachingService> cachingServices = context.getBeansOfType(CachingService.class).values();
        for (CachingService cachingService : cachingServices) {
            CacheType cacheType = cachingService.cacheType();
            Class<? extends CachingService> aClass = cachingService.getClass();
            Assert.assertNotNull("Caching Service + " + aClass.getSimpleName() +
                    " has null " + CacheType.class.getSimpleName(),
                    cacheType);

//            Optional<Method> evictMethodOpt = Arrays.stream(aClass.getMethods())
//                    .filter(method -> evictMethodName.equals(method.getName()))
//                    .filter(method -> method.getParameterCount() == 1)
//                    .filter(method -> method.getParameterTypes()[0].isAssignableFrom(cacheType.getKeyType()))
//                    .findAny();
//
//            if (evictMethodOpt.isEmpty()) {
//                final String messageTemplate = "%s implementation %s does not have method %s " +
//                        "taking a parameter of type %s as specified by %s.%s";
//
//                final String message = String.format(messageTemplate,
//                        CachingService.class.getSimpleName(),
//                        aClass.getSimpleName(),
//                        evictMethodName,
//                        cacheType.getKeyType().getSimpleName(),
//                        CacheType.class.getSimpleName(),
//                        cacheType.name());
//
//                Assert.fail(message);
//            }
        }
    }
}
