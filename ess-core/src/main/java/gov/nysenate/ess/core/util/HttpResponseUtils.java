package gov.nysenate.ess.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Utility for writing serialized output to HttpServletResponse for various content types.
 */
public class HttpResponseUtils
{
    private static final Logger logger = LoggerFactory.getLogger(HttpResponseUtils.class);

    /**
     * This method serializes an object writes it to the HttpServletResponse. The media type supplied
     * determines what format the object will be serialized into. For JSON use MediaType.APPLICATION_JSON.
     * For XML use MediaType.APPLICATION_XML. The appropriate headers and content length will be set as well.
     * @param response HttpServletResponse
     * @param object   Object to write
     * @param mediaType MediaType that indicates the format to serialize the object into.
     * @return String representing serialized output or empty if response/object is null.
     */
    public static String writeHttpResponse(HttpServletResponse response, Object object, MediaType mediaType) {
        if (response != null && object != null) {
            if (mediaType == null) {
                mediaType = MediaType.TEXT_PLAIN;
            }
            if (mediaType.equals(MediaType.APPLICATION_JSON)) {
                return writeJsonHttpResponse(response, object);
            }
            else if (mediaType.equals(MediaType.APPLICATION_XML)) {
                return writeXmlHttpResponse(response, object);
            }
            else if (mediaType.equals(MediaType.TEXT_HTML)) {
                return writeHtmlHttpResponse(response, object);
            }
            else {
                return writePlainHttpResponse(response, object);
            }
        }
        return "";
    }

    /**
     * Same as overloaded method except the MediaType is automatically determined first.
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param object Object to write
     * @return String representing serialized output or empty if response/object is null.
     */
    public static String writeHttpResponse(HttpServletRequest request, HttpServletResponse response, Object object) {
        MediaType mediaType = getRequestedMediaType(request);
        return writeHttpResponse(response, object, mediaType);
    }

    /**
     * Serialize the object into JSON and write it to the HttpServletResponse.
     * @param response HttpServletResponse
     * @param object Object to write
     * @return String representing the JSON output.
     */
    public static String writeJsonHttpResponse(HttpServletResponse response, Object object) {
        String jsonOutput = OutputUtils.toJson(object);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return writeStringHttpResponse(response, jsonOutput);
    }

    /**
     * Serialize the object into XML and write it to the HttpServletResponse.
     * @param response HttpServletResponse
     * @param object Object to write
     * @return String representing the XML output.
     */
    public static String writeXmlHttpResponse(HttpServletResponse response, Object object) {
        String xmlOutput = OutputUtils.toXml(object);
        response.setContentType(MediaType.APPLICATION_XML_VALUE);
        return writeStringHttpResponse(response, xmlOutput);
    }

    /**
     * Serialize the object into HTML and write it to the HttpServletResponse. Nothing special
     * really happens to the output, just the correct content type header is set.
     * @param response HttpServletResponse
     * @param html Object (String) to write
     * @return String representing the HTML output.
     */
    public static String writeHtmlHttpResponse(HttpServletResponse response, Object html) {
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        return writeStringHttpResponse(response, html.toString());
    }

    /**
     * Serialize the object into plain text (via toString()) and write it to the HttpServletResponse.
     * @param response HttpServletResponse
     * @param text Object (String) to write
     * @return String representing output.
     */
    public static String writePlainHttpResponse(HttpServletResponse response, Object text) {
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        return writeStringHttpResponse(response, text.toString());
    }

    /**
     * Writes the output string into the HttpServletResponse and sets the content length header.
     * @param response HttpServletResponse
     * @param output String to write
     * @return String output
     */
    protected static String writeStringHttpResponse(HttpServletResponse response, String output) {
        if (output != null) {
            try {
                response.getWriter().write(output);
                response.setContentLength(output.length());
            }
            catch (IOException ex) {
                logger.error("Failed to write output to HttpResponse!", ex);
            }
        }
        return output;
    }

    /**
     * Determines the highest quality media type that is accepted by the request and attempts
     * to establish a hierarchy of media types to be returned if the type is wild-carded.
     * That hierarchy is as follows: JSON, XML, HTML.
     * @param request HttpServletRequest
     * @return matched MediaType
     */
    public static MediaType getRequestedMediaType(HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");
        try {
            List<MediaType> mediaTypes = MediaType.parseMediaTypes(acceptHeader);
            if (mediaTypes != null && !mediaTypes.isEmpty()) {
                MediaType mediaType = mediaTypes.get(0);
                if (mediaType != null) {
                    if (mediaType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                        return MediaType.APPLICATION_JSON;
                    }
                    else if (mediaType.isCompatibleWith(MediaType.APPLICATION_XML) ||
                            mediaType.isCompatibleWith(MediaType.TEXT_XML)) {
                        return MediaType.APPLICATION_XML;
                    }
                    else if (mediaType.isCompatibleWith(MediaType.APPLICATION_XHTML_XML) ||
                            mediaType.isCompatibleWith(MediaType.TEXT_HTML)) {
                        return MediaType.TEXT_HTML;
                    }
                    else {
                        return mediaType;
                    }
                }
            }
        }
        catch(IllegalArgumentException ex) {
            logger.warn("Failed to parse media type for the given Accept header: {}", acceptHeader);
        }
        return MediaType.TEXT_PLAIN;
    }
}