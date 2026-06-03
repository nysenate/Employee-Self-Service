package gov.nysenate.ess.core.service.pec.external.everfi.user;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiException;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategory;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryService;
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

        EverfiUserClient client = new EverfiUserClient(
                everfiApiClient, new EverfiUserPayloadFactory(), new EverfiCategoryService(null));

        List<EverfiUser> users = client.fetchAll(2);

        assertEquals(3, users.size());
        assertEquals("user-1", users.get(0).getUuid());
        assertEquals("user-2", users.get(1).getUuid());
        assertEquals("user-3", users.get(2).getUuid());
    }

    @Test
    public void findByUuidReturnsNullOnNotFound() throws IOException {
        EverfiUserClient client = new EverfiUserClient(
                new NotFoundEverfiApiClient(), new EverfiUserPayloadFactory(), new EverfiCategoryService(null));

        EverfiUser user = client.findByUuid("missing-user");

        assertNull(user);
    }

    @Test
    public void findByUuidHydratesLabelsFromCategoryCache() throws IOException {
        StubEverfiApiClient everfiApiClient = new StubEverfiApiClient(Map.of(
                "/v1/admin/users/user-1?fields[users]=email,first_name,last_name,sso_id,employee_id,student_id,active,user_rule_set_roles,category_labels",
                singleUserResponse("user-1", 200)
        ));
        EverfiCategoryService categoryService = new EverfiCategoryService(null);
        categoryService.initialize(List.of(category("Department", categoryLabel(200, "HR", 10, "Department"))));
        EverfiUserClient client = new EverfiUserClient(
                everfiApiClient, new EverfiUserPayloadFactory(), categoryService);

        EverfiUser user = client.findByUuid("user-1");

        assertEquals(1, user.getCategoryLabels().size());
        assertEquals("HR", user.getCategoryLabels().get(0).getLabelName());
        assertEquals("Department", user.getCategoryLabels().get(0).getCategoryName());
        assertEquals(10, user.getCategoryLabels().get(0).getCategoryId());
    }

    @Test(expected = EverfiApiException.class)
    public void findByUuidRethrowsNonNotFoundApiErrors() throws IOException {
        EverfiUserClient client = new EverfiUserClient(
                new ErrorEverfiApiClient(), new EverfiUserPayloadFactory(), new EverfiCategoryService(null));

        client.findByUuid("broken-user");
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
        return userJson(uuid, "");
    }

    private static String userJson(String uuid, String labelRelationships) {
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
                + "\"relationships\":{\"category_labels\":{\"data\":[" + labelRelationships + "]}}"
                + "}";
    }

    private static String singleUserResponse(String uuid, int labelId) {
        String labelRelationship = "{\"id\":\"" + labelId + "\",\"type\":\"category_labels\"}";
        return "{\"data\":" + userJson(uuid, labelRelationship) + "}";
    }

    private static EverfiCategory category(String name, EverfiCategoryLabel... labels) {
        return new EverfiCategory(10, name, List.of(labels));
    }

    private static EverfiCategoryLabel categoryLabel(int labelId, String labelName, int categoryId, String categoryName) {
        EverfiCategoryLabel label = new EverfiCategoryLabel(labelId, labelName);
        label.setCategoryId(categoryId);
        label.setCategoryName(categoryName);
        return label;
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
