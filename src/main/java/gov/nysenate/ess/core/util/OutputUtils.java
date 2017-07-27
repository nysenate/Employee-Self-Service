package gov.nysenate.ess.core.util;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputUtils
{
    private static final Logger logger = LoggerFactory.getLogger(OutputUtils.class);
    public static final ObjectMapper jsonMapper = new ObjectMapper();
    public static final XmlMapper xmlMapper = new XmlMapper();

    private OutputUtils() {}

    static {
        configureMapper(jsonMapper);
        xmlMapper.setDefaultUseWrapper(false);
        configureMapper(xmlMapper);
    }

    /**
     * Given an object, this method will attempt to serialize it into JSON.
     * @param object Object
     * @return String - Json or empty string if failed.
     */
    public static String toJson(Object object) {
        try {
            return jsonMapper.writeValueAsString(object);
        }
        catch(JsonGenerationException ex){
            logger.error("Failed to generate json: " + ex.getMessage());
        }
        catch(JsonMappingException ex){
            logger.error("Failed to map json: " + ex.getMessage());
        }
        catch(Exception ex){
            logger.error("ObjectMapper exception: " + ex.getMessage());
        }
        return "";
    }

    /**
     * Given an object, this method will attempt to serialize it into XML.
     * @param object Object
     * @return String - Xml or empty string if failed.
     */
    public static String toXml(Object object) {
        try {
            return xmlMapper.writeValueAsString(object);
        }
        catch (JsonProcessingException ex) {
            logger.error("Failed to generate xml: " + ex.getMessage());
        }
        catch (Exception ex) {
            logger.error("ObjectMapper exception: " + ex.getMessage());
        }
        return "";
    }

    /* --- Internal Methods --- */

    /**
     * Common object mapper config used by both json and xml mappers.
     */
    private static void configureMapper(ObjectMapper objectMapper) {
        objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new GuavaModule());
        objectMapper.registerModule(new JaxbAnnotationModule());
        objectMapper.registerModule(new JavaTimeModule());
    }
}