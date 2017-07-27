package gov.nysenate.ess.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.ess.core.util.OutputUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    /**
     * An object mapper for serializing objects into json.
     */
    @Bean
    public ObjectMapper jsonObjectMapper() {
        return OutputUtils.jsonMapper;
    }

    /**
     * An object mapper for serializing objects into XML
     */
    @Bean
    public ObjectMapper xmlObjectMapper() {
        return OutputUtils.xmlMapper;
    }
}
