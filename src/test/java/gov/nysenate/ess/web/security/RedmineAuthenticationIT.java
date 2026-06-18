package gov.nysenate.ess.web.security;

import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.web.WebTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Category(IntegrationTest.class)
public class RedmineAuthenticationIT extends WebTest {

    private static final String INVALID_API_KEY = "invalid-key";
    private static final String REDMINE_SEARCH_ENDPOINT = "/api/v1/redmine/employee/search";
    private static final String REDMINE_STATUS_CHANGE_ENDPOINT = "/api/v1/redmine/statusChanges";
    private static final String NON_REDMINE_ENDPOINT = "/api/v1/employees/search";

    @Value("${auth.redmine.api.key}") private String redmineApiKey;

    @Test
    public void testRedmineEndpointWithInvalidKey() throws Exception {
        // Test that invalid API key is rejected
        mockMvc.perform(get(REDMINE_SEARCH_ENDPOINT)
                .header("X-API-Key", INVALID_API_KEY)
                .param("term", "test"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void testRedmineEndpointWithMissingKey() throws Exception {
        MvcResult result = mockMvc.perform(get(REDMINE_SEARCH_ENDPOINT)
                .param("term", "test"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assert responseContent.contains("Missing Redmine API key header");
    }

    @Test
    public void testRedmineEndpointWithEmptyKey() throws Exception {
        MvcResult result = mockMvc.perform(get(REDMINE_SEARCH_ENDPOINT)
                .header("X-API-Key", "")
                .param("term", "test"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assert responseContent.contains("Missing Redmine API key header");
    }

    @Test
    public void testRedmineStatusChangesEndpoint() throws Exception {
        mockMvc.perform(get(REDMINE_STATUS_CHANGE_ENDPOINT)
                .header("X-API-Key", redmineApiKey)
                .param("from", LocalDate.now().minusDays(1).toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void testNonRedmineEndpointNotAffected() throws Exception {
        // Regular API endpoints should not be accessible with Redmine auth
        mockMvc.perform(get(NON_REDMINE_ENDPOINT)
                .header("X-API-Key", redmineApiKey))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testNonRedmineUserAccess() throws Exception {
        // Standard employees should not have access to redmine endpoints
        performAuthenticated(get(REDMINE_STATUS_CHANGE_ENDPOINT))
                .andExpect(status().isUnauthorized());

        performAuthenticated(get(REDMINE_SEARCH_ENDPOINT))
                .andExpect(status().isUnauthorized());
    }
}