package gov.nysenate.ess.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

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
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new GuavaModule());
        objectMapper.registerModule(new JSR310Module());
        objectMapper.registerModule(new JaxbAnnotationModule());
    }
}
