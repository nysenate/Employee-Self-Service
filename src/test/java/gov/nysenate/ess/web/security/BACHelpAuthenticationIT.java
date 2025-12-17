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
public class BACHelpAuthenticationIT extends WebTest {

    private static final String INVALID_API_KEY = "invalid-key";
    private static final String BACHELP_SEARCH_ENDPOINT = "/api/v1/bachelp/employee/search";
    private static final String BACHELP_STATUS_CHANGE_ENDPOINT = "/api/v1/bachelp/statusChanges";
    private static final String NON_BACHELP_ENDPOINT = "/api/v1/employees/search";

    @Value("${auth.bachelp.api.key}") private String bachelpApiKey;

    @Test
    public void testBACHelpEndpointWithInvalidKey() throws Exception {
        // Test that invalid API key is rejected
        mockMvc.perform(get(BACHELP_SEARCH_ENDPOINT)
                .header("X-BACHelp-API-Key", INVALID_API_KEY)
                .param("term", "test"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void testBACHelpEndpointWithMissingKey() throws Exception {
        MvcResult result = mockMvc.perform(get(BACHELP_SEARCH_ENDPOINT)
                .param("term", "test"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assert responseContent.contains("Missing BACHelp API key header");
    }

    @Test
    public void testBACHelpEndpointWithEmptyKey() throws Exception {
        MvcResult result = mockMvc.perform(get(BACHELP_SEARCH_ENDPOINT)
                .header("X-BACHelp-API-Key", "")
                .param("term", "test"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assert responseContent.contains("Missing BACHelp API key header");
    }

    @Test
    public void testBACHelpStatusChangesEndpoint() throws Exception {
        mockMvc.perform(get(BACHELP_STATUS_CHANGE_ENDPOINT)
                .header("X-BACHelp-API-Key", bachelpApiKey)
                .param("from", LocalDate.now().minusDays(1).toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void testNonBACHelpEndpointNotAffected() throws Exception {
        // Regular API endpoints should not be accessible with BACHelp auth
        mockMvc.perform(get(NON_BACHELP_ENDPOINT)
                .header("X-BACHelp-API-Key", bachelpApiKey))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testNonBACHelpUserAccess() throws Exception {
        // Standard employees should not have access to bachelp endpoints
        performAuthenticated(get(BACHELP_STATUS_CHANGE_ENDPOINT))
                .andExpect(status().isUnauthorized());

        performAuthenticated(get(BACHELP_SEARCH_ENDPOINT))
                .andExpect(status().isUnauthorized());
    }
}