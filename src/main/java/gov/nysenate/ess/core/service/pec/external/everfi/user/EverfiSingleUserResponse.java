package gov.nysenate.ess.core.service.pec.external.everfi.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EverfiSingleUserResponse {

    @JsonProperty("data")
    private EverfiUser user;
    private EverfiResponseMeta meta;

    public EverfiUser getUser() {
        return user;
    }

    public EverfiResponseMeta getMeta() {
        return meta;
    }

    @Override
    public String toString() {
        return "EverfiUsersResponse{" +
                "users=" + user +
                ", meta=" + meta +
                '}';
    }

}
