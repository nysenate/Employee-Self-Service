package gov.nysenate.ess.core.service.pec.external.everfi;


import java.io.IOException;

public class EverfiUsersApi {

    private static final String USER_END_POINT = "/v1/admin/users";
    private static final int limit = 2;

    public EverfiUsersApiResponse getUsers() throws IOException {
//        String data = get(USER_END_POINT + "?page[page]=1&page[per_page]=" + limit);
//        return OutputUtils.jsonToObject(data, EverfiUsersApiResponse.class);
        return null;
    }

    // TODO need pagination functionality.
}
