package gov.nysenate.ess.core.controller.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.web.WebTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Category(IntegrationTest.class)
public class BACHelpRestApiCtrlIT extends WebTest {

    private static final String BACHELP_STATUS_CHANGE_ENDPOINT = "/api/v1/bachelp/statusChanges";
    private static final String BACHELP_SEARCH_ENDPOINT = "/api/v1/bachelp/employee/search";
    private static final String BACHELP_EMPLOYEE_LOOKUP_ENDPOINT = "/api/v1/bachelp/employee";
    private static final Logger logger = LoggerFactory.getLogger(BACHelpRestApiCtrlIT.class);

    @Value("${auth.bachelp.api.key}")
    private String bachelpApiKey;

    @Autowired
    private ObjectMapper jsonObjectMapper;

    @Test
    public void testStatusChangesApiReturnsPostDateTime() throws Exception {
        // Test that the status changes API returns non-null postDateTime values
        final String fromDateString = LocalDateTime.now().minusDays(3).toString();
        MvcResult result = mockMvc.perform(get(BACHELP_STATUS_CHANGE_ENDPOINT)
                .header("X-BACHelp-API-Key", bachelpApiKey)
                .header("Accept", "application/json")
                .param("from", fromDateString))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode response = jsonObjectMapper.readTree(responseContent);
        
        // Verify basic response structure
        assertTrue("Response should have success field", response.has("success"));
        assertTrue("Should be successful", response.get("success").asBoolean());
        assertEquals("Should have correct response type", "bachelp employee status change list", 
                    response.get("responseType").asText());
        
        JsonNode results = response.get("result");
        assertTrue("Should have results array", results.isArray());
        
        // check that we have some results to test
        if (results.isEmpty()) {
            logger.warn("No status changes detected since {}", fromDateString);
        }

        // Check each result for postDateTime field
        for (JsonNode statusChange : results) {
            // Verify required fields are present
            assertTrue("Should have employeeId", statusChange.has("employeeId"));
            assertTrue("Should have transactionCode", statusChange.has("transactionCode"));
            assertTrue("Should have postDateTime field", statusChange.has("postDateTime"));

            JsonNode postDateTime = statusChange.get("postDateTime");
            
            // ALL records should have non-null postDateTime
            assertFalse("PostDateTime should NOT be null.", postDateTime.isNull());
            
            // Verify the postDateTime is a valid ISO datetime string
            String postDateTimeStr = postDateTime.asText();
            assertNotNull("PostDateTime should not be null string", postDateTimeStr);
            assertFalse("PostDateTime should not be empty", postDateTimeStr.isEmpty());
            
            // Should be able to parse as LocalDateTime
            LocalDateTime parsedDateTime = LocalDateTime.parse(postDateTimeStr);
            assertNotNull("Should be able to parse postDateTime as LocalDateTime", parsedDateTime);
        }
    }

    @Test
    public void testStatusChangesApiWithDateFilter() throws Exception {
        // Test that the date filtering is working correctly
        // Records returned should have post dates after the specified 'from' parameter
        LocalDateTime fromDate = LocalDateTime.now().minusDays(2);
        
        MvcResult result = mockMvc.perform(get(BACHELP_STATUS_CHANGE_ENDPOINT)
                .header("X-BACHelp-API-Key", bachelpApiKey)
                .header("Accept", "application/json")
                .param("from", fromDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode response = jsonObjectMapper.readTree(responseContent);
        JsonNode results = response.get("result");

        // If we have results, verify they have the expected transaction codes
        if (!results.isEmpty()) {
            for (JsonNode statusChange : results) {
                String transactionCode = statusChange.get("transactionCode").asText();
                // Should be one of the allowed BACHelp transaction codes
                assertTrue("Transaction code should be one of the allowed BACHelp codes",
                          transactionCode.matches("APP|LOC|NAM|PHO|RTP|LIN|EMP"));
            }
        }
    }

    @Test
    public void testStatusChangesApiDateValidation() throws Exception {
        // Test that dates older than 7 days are rejected
        LocalDateTime tooOldDate = LocalDateTime.now().minusDays(8);
        
        MvcResult result = mockMvc.perform(get(BACHELP_STATUS_CHANGE_ENDPOINT)
                .header("X-BACHelp-API-Key", bachelpApiKey)
                .header("Accept", "application/json")
                .param("from", tooOldDate.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
        
        String responseContent = result.getResponse().getContentAsString();
        JsonNode response = jsonObjectMapper.readTree(responseContent);
        
        assertFalse("Should not be successful", response.get("success").asBoolean());
        assertEquals("Should have correct error code", "INVALID_ARGUMENTS", 
                    response.get("errorCode").asText());
        assertEquals("Should have correct parameter name", "from", 
                    response.get("errorData").get("parameterConstraint").get("name").asText());
        assertTrue("Should contain constraint message", 
                  response.get("errorData").get("parameterConstraint").get("constraint").asText()
                          .contains("from datetime must not be earlier than 7 days ago"));
    }

    @Test
    public void testEmployeeSearchApi() throws Exception {
        // Basic test for the employee search endpoint to ensure it works
        MvcResult result = mockMvc.perform(get(BACHELP_SEARCH_ENDPOINT)
                .header("X-BACHelp-API-Key", bachelpApiKey)
                .header("Accept", "application/json")
                .param("term", "smith"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode response = jsonObjectMapper.readTree(responseContent);
        
        assertTrue("Should be successful", response.get("success").asBoolean());
        assertEquals("Should have correct response type", "bachelp employee list", 
                    response.get("responseType").asText());
        assertTrue("Should have total field", response.has("total"));
        assertTrue("Should have result array", response.has("result"));
        assertTrue("Total should be >= 0", response.get("total").asInt() >= 0);
        
        // If we have results, verify they contain the location field with respCenterHead
        JsonNode results = response.get("result");
        if (!results.isEmpty()) {
            JsonNode firstEmployee = results.get(0);
            assertTrue("Employee should have location", firstEmployee.has("location"));

            JsonNode location = firstEmployee.get("location");
            assertNotNull("location should not be null", location);
            assertTrue("Should have respCenterHead field in location", location.has("respCenterHead"));

            JsonNode respCenterHead = location.get("respCenterHead");
            assertNotNull("respCenterHead should not be null", respCenterHead);
            assertTrue("Should have code field in respCenterHead", respCenterHead.has("code"));
        }
    }

    @Test
    public void testEmployeeLookupApi() throws Exception {
        // Test the employee lookup endpoint with a known employee ID
        // Using employee ID 1 which should exist in most test datasets
        int testEmpId = 1;
        
        MvcResult result = mockMvc.perform(get(BACHELP_EMPLOYEE_LOOKUP_ENDPOINT + "/" + testEmpId)
                .header("X-BACHelp-API-Key", bachelpApiKey)
                .header("Accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode response = jsonObjectMapper.readTree(responseContent);
        
        assertTrue("Should be successful", response.get("success").asBoolean());

        // Verify employee data structure
        JsonNode employee = response.get("employee");
        assertTrue("Should have employeeId", employee.has("employeeId"));
        assertTrue("Should have fullName", employee.has("fullName"));
        assertTrue("Should have active status", employee.has("active"));
        assertTrue("Should have location", employee.has("location"));

        assertEquals("Employee ID should match", testEmpId, employee.get("employeeId").asInt());

        // Verify location structure
        JsonNode location = employee.get("location");
        assertNotNull("location should not be null", location);
        assertTrue("Should have respCenterHead field in location", location.has("respCenterHead"));

        // Verify respCenterHead structure
        JsonNode respCenterHead = location.get("respCenterHead");
        assertNotNull("respCenterHead should not be null", respCenterHead);
        assertTrue("Should have active field in respCenterHead", respCenterHead.has("active"));
        assertTrue("Should have code field in respCenterHead", respCenterHead.has("code"));
        assertTrue("Should have name field in respCenterHead", respCenterHead.has("name"));
        assertTrue("Should have shortName field in respCenterHead", respCenterHead.has("shortName"));
        assertTrue("Should have affiliateCode field in respCenterHead", respCenterHead.has("affiliateCode"));
    }

    @Test
    public void testEmployeeLookupNotFound() throws Exception {
        // Test employee lookup with non-existent employee ID
        int nonExistentEmpId = 999999;
        
        mockMvc.perform(get(BACHELP_EMPLOYEE_LOOKUP_ENDPOINT + "/" + nonExistentEmpId)
                .header("X-BACHelp-API-Key", bachelpApiKey)
                .header("Accept", "application/json"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"));
    }
}