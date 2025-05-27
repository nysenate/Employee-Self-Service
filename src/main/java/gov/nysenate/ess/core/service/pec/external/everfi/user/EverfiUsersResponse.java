package gov.nysenate.ess.core.service.pec.external.everfi.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EverfiUsersResponse {

    @JsonProperty("data")
    private List<EverfiUser> users;
    private EverfiResponseLinks links;
    private EverfiResponseMeta meta;

    public List<EverfiUser> getUsers() {
        return users;
    }

    public EverfiResponseLinks getLinks() {
        return links;
    }

    public EverfiResponseMeta getMeta() {
        return meta;
    }

    @Override
    public String toString() {
        return "EverfiUsersResponse{" +
                "users=" + users +
                ", links=" + links +
                ", meta=" + meta +
                '}';
    }
}
