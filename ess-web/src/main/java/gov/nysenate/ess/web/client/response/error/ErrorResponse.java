package gov.nysenate.ess.web.client.response.error;

import gov.nysenate.ess.web.client.response.base.BaseResponse;

public class ErrorResponse extends BaseResponse
{
    protected ErrorCode errorCode;

    public ErrorResponse(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
        this.responseType = "error";
    }

    public ErrorResponse(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage() + "\n" + message;
        this.responseType = "error";
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
