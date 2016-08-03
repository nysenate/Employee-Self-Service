package gov.nysenate.ess.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.springframework.context.annotation.*;

@Configuration
@Profile({"test", "dev", "prod"})
@ComponentScan("gov.nysenate.ess.core")
@Import(BaseConfig.class)
public class CoreConfig {

    /**
     * An object mapper for serializing objects into json.
     */
    @Bean
    public ObjectMapper jsonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        configureMapper(objectMapper);
        return objectMapper;
    }

    /**
     * An object mapper for serializing objects into XML
     */
    @Bean
    public ObjectMapper xmlObjectMapper() {
        ObjectMapper objectMapper = new XmlMapper();
        configureMapper(objectMapper);
        return objectMapper;
    }

    /**
     * Common object mapper config used by both json and xml mappers.
     */
    private void configureMapper(ObjectMapper objectMapper) {
        objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new GuavaModule());
        objectMapper.registerModule(new JSR310Module());
    }
}
