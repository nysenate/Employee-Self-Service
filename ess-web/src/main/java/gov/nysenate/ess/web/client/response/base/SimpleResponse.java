package gov.nysenate.ess.web.client.response.base;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
public class SimpleResponse extends BaseResponse
{
    public SimpleResponse() {}

    public SimpleResponse(boolean success, String message, String type) {
        this.success = success;
        this.message = message;
        this.responseType = type;
    }
}
