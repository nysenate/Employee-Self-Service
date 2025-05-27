package gov.nysenate.ess.core.config;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionHandler;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventBusConfig
{
    private static final Logger logger = LoggerFactory.getLogger(EventBusConfig.class);

    /** --- Guava Event Bus Configuration --- */

    @Bean
    public EventBus eventBus() {
        SubscriberExceptionHandler errorHandler = (exception, context) ->
                logger.error("Exception thrown during event handling within {}: {}, {}", context.getSubscriberMethod(),
                exception, ExceptionUtils.getStackTrace(exception));
        return new EventBus(errorHandler);
    }
}
