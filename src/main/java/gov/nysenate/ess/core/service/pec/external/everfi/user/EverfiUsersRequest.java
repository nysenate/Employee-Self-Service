package gov.nysenate.ess.core.service.pec.external.everfi.user;


import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.util.OutputUtils;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

class EverfiUsersRequest {

    private static final String USER_END_POINT = "/v1/admin/users";
    static final int DEFAULT_PAGE_SIZE = 100;
    static final int MAX_PAGE_SIZE = 100;
    private final EverfiApiClient everfiClient;
    private final int page;
    private final int limit;
    private EverfiUsersResponse response;

    EverfiUsersRequest(EverfiApiClient everfiClient) {
        this(everfiClient, 1, DEFAULT_PAGE_SIZE);
    }

    EverfiUsersRequest(EverfiApiClient everfiClient, int page, int limit) {
        Assert.notNull(everfiClient, "everfiClient must not be null");
        Assert.isTrue(page >= 1, "page must be greater than or equal to 1");
        Assert.isTrue(limit >= 1 && limit <= MAX_PAGE_SIZE,
                "limit must be between 1 and " + MAX_PAGE_SIZE + " inclusive");

        this.everfiClient = everfiClient;
        this.page = page;
        this.limit = limit;
    }

    List<EverfiUser> getUsers() throws IOException {
        String data = everfiClient.get(endpoint());
        response = OutputUtils.jsonToObject(data, EverfiUsersResponse.class);
        return response.getUsers();
    }

    EverfiUsersRequest next() {
        if (response == null) {
            throw new IllegalStateException("'next()' can only be called after a successful call to 'fetch()'");
        }
        if (response.getLinks().getNext() == null) {
            return null;
        }
        return new EverfiUsersRequest(everfiClient, page + 1, limit);
    }

    private String endpoint() {
        return UriComponentsBuilder.fromPath(USER_END_POINT)
                .queryParam("page[page]", page)
                .queryParam("page[per_page]", limit)
                .toUriString();
    }
}
