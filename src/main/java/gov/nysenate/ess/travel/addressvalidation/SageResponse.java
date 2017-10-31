package gov.nysenate.ess.travel.addressvalidation;

import gov.nysenate.ess.core.model.unit.Address;

public class SageResponse {
    private String status;
    private String source;
    private String[] messages;
    private Address address;
    private boolean validated;
    private int statusCode;
    private String description;

    public SageResponse() {}

    public SageResponse(String status, String source, String[] messages,
                        Address address, boolean validated,
                        int statusCode, String description) {
        this.status = status;
        this.source = source;
        this.messages = messages;

        this.address = address;
        this.validated = validated;
        this.statusCode = statusCode;
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public String getSource() {
        return source;
    }

    public String[] getMessages() {
        return messages;
    }

    public Address getAddress() {
        return address;
    }

    public boolean isValidated() {
        return validated;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setMessages(String[] messages) {
        this.messages = messages;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
