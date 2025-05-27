package gov.nysenate.ess.core.service.pec.external.everfi.user;


import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.util.OutputUtils;

import java.io.IOException;
import java.util.List;

public class EverfiUsersRequest {

    private static final String USER_END_POINT = "/v1/admin/users";
    private final EverfiApiClient everfiClient;
    private final int page;
    private final int limit;
    private EverfiUsersResponse response;

    public EverfiUsersRequest(EverfiApiClient everfiClient) {
        this(everfiClient, 1, 100);
    }

    public EverfiUsersRequest(EverfiApiClient everfiClient, int page, int limit) {
        this.everfiClient = everfiClient;
        this.page = page;
        this.limit = limit > 0 && limit <= 100 ? limit : 100;
    }

    public List<EverfiUser> getUsers() throws IOException {
        String data = everfiClient.get(endpoint());
        response = OutputUtils.jsonToObject(data, EverfiUsersResponse.class);
        return response.getUsers();
    }

    public EverfiUsersRequest next() {
        if (response == null) {
            throw new IllegalStateException("'next()' can only be called after a successful call to 'fetch()'");
        }
        if (response.getLinks().getNext() == null) {
            return null;
        }
        return new EverfiUsersRequest(everfiClient, page + 1, limit);
    }

    private String endpoint() {
        return USER_END_POINT + "?page[page]="
                + page
                + "&page[per_page]="
                + limit;
    }
}
