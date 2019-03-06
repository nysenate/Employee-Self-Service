package gov.nysenate.ess.core.client.view;

import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.view.base.ViewObject;

public class ErrorCodeView implements ViewObject {

    private String name;
    private int code;
    private String message;

    public ErrorCodeView(ErrorCode errorCode) {
        this.name = errorCode.name();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getViewType() {
        return "error-code";
    }
}
