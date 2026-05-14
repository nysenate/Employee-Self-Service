package gov.nysenate.ess.core.service.pec.external.everfi.user;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiException;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategorySnapshot;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category(UnitTest.class)
public class EverfiUserClientTest {

    @Test
    public void fetchAllAggregatesAllPages() throws IOException {
        StubEverfiApiClient everfiApiClient = new StubEverfiApiClient(Map.of(
                "/v1/admin/users?page%5Bpage%5D=1&page%5Bper_page%5D=2",
                usersResponse("user-1", "user-2", "/v1/admin/users?page[page]=2&page[per_page]=2"),
                "/v1/admin/users?page%5Bpage%5D=2&page%5Bper_page%5D=2",
                usersResponse("user-3", null, null)
        ));

        EverfiUserClient client = new EverfiUserClient(everfiApiClient, new EverfiUserPayloadFactory());

        List<EverfiUser> users = client.fetchAll(2, new EverfiCategorySnapshot(List.of()));

        assertEquals(3, users.size());
        assertEquals("user-1", users.get(0).getUuid());
        assertEquals("user-2", users.get(1).getUuid());
        assertEquals("user-3", users.get(2).getUuid());
    }

    @Test
    public void findByUuidReturnsNullOnNotFound() throws IOException {
        EverfiUserClient client = new EverfiUserClient(new NotFoundEverfiApiClient(), new EverfiUserPayloadFactory());

        EverfiUser user = client.findByUuid("missing-user", new EverfiCategorySnapshot(List.of()));

        assertNull(user);
    }

    @Test(expected = EverfiApiException.class)
    public void findByUuidRethrowsNonNotFoundApiErrors() throws IOException {
        EverfiUserClient client = new EverfiUserClient(new ErrorEverfiApiClient(), new EverfiUserPayloadFactory());

        client.findByUuid("broken-user", new EverfiCategorySnapshot(List.of()));
    }

    private static String usersResponse(String firstUuid, String secondUuid, String nextLink) {
        String secondUserJson = secondUuid == null ? "" : "," + userJson(secondUuid);
        String nextJson = nextLink == null ? "null" : "\"" + nextLink + "\"";
        return "{"
                + "\"data\":[" + userJson(firstUuid) + secondUserJson + "],"
                + "\"links\":{\"self\":\"/v1/admin/users\",\"first\":null,\"prev\":null,\"next\":" + nextJson + ",\"last\":null},"
                + "\"meta\":{\"total_count\":3,\"cursor_id\":0}"
                + "}";
    }

    private static String userJson(String uuid) {
        return "{"
                + "\"id\":\"" + uuid + "\","
                + "\"attributes\":{"
                + "\"active\":true,"
                + "\"created_at\":\"2024/01/01T01:00:00.000Z\","
                + "\"email\":\"" + uuid + "@example.com\","
                + "\"employee_id\":\"100\","
                + "\"first_name\":\"Test\","
                + "\"last_name\":\"User\""
                + "},"
                + "\"relationships\":{\"category_labels\":{\"data\":[]}}"
                + "}";
    }

    private static class StubEverfiApiClient extends EverfiApiClient {

        private final Map<String, String> responses = new HashMap<>();

        private StubEverfiApiClient(Map<String, String> responses) {
            super("https://example.com", null, null);
            this.responses.putAll(responses);
        }

        @Override
        public String get(String endpoint) {
            return responses.get(endpoint);
        }
    }

    private static class NotFoundEverfiApiClient extends EverfiApiClient {

        private NotFoundEverfiApiClient() {
            super("https://example.com", null, null);
        }

        @Override
        public String get(String endpoint) throws IOException {
            throw new EverfiApiException(404, "Not Found");
        }
    }

    private static class ErrorEverfiApiClient extends EverfiApiClient {

        private ErrorEverfiApiClient() {
            super("https://example.com", null, null);
        }

        @Override
        public String get(String endpoint) throws IOException {
            throw new EverfiApiException(500, "Internal Server Error");
        }
    }
}
