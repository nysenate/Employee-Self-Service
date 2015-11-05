package gov.nysenate.ess.core.client.response.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;

@XmlRootElement
@JsonSerialize(using = ViewObjectResponse.ViewObjectResponseJsonSerializer.class)
public final class ViewObjectResponse<ViewType extends ViewObject> extends BaseResponse
{
    @XmlElement public ViewType result;
    private String resultFieldName;

    public ViewObjectResponse() {}

    public ViewObjectResponse(ViewType result) {
        this(result, "result", "");
    }

    public ViewObjectResponse(ViewType result, String resultFieldName) {
        this(result, resultFieldName, "");
    }

    public ViewObjectResponse(ViewType result, String resultFieldName, String message) {
        this.result = result;
        this.resultFieldName = resultFieldName;
        if (result != null) {
            success = true;
            responseType = result.getViewType();
        }
        this.message = message;
    }

    public static class ViewObjectResponseJsonSerializer extends JsonSerializer<ViewObjectResponse>
    {
        @Override
        public void serialize(ViewObjectResponse vor, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
            jsonGenerator.writeStartObject();
            vor.serialize(vor, jsonGenerator, serializerProvider);
            jsonGenerator.writeObjectField(vor.resultFieldName, vor.result);
            jsonGenerator.writeEndObject();
        }
    }
}
