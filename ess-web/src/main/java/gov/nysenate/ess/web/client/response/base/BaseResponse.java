package gov.nysenate.ess.web.client.response.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;

@XmlRootElement(name = "BaseResponse")
public abstract class BaseResponse
{
    @XmlElement
    public boolean success = false;
    @XmlElement
    public String message = "";
    @XmlElement
    public String responseType = "default";

    public void setMessage(String message) {
        this.message = message;
    }

    public void serialize(BaseResponse br, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeBooleanField("success", br.success);
        jsonGenerator.writeStringField("message", br.message);
        jsonGenerator.writeStringField("responseType", br.responseType);
    }
}
