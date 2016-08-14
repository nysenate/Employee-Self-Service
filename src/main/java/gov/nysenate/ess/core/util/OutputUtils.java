package gov.nysenate.ess.core.util;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputUtils
{
    private static final Logger logger = LoggerFactory.getLogger(OutputUtils.class);
    private static ObjectMapper jsonMapper = new ObjectMapper();
    private static XmlMapper xmlMapper = new XmlMapper();

    private OutputUtils() {}

    static {
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        jsonMapper.registerModule(new JSR310Module());
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
}