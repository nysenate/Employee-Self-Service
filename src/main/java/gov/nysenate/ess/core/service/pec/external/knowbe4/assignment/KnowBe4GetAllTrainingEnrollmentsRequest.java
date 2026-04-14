package gov.nysenate.ess.core.service.pec.external.knowbe4.assignment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.ess.core.service.pec.external.knowbe4.KnowBe4ApiClient;
import gov.nysenate.ess.core.util.OutputUtils;

import java.io.IOException;
import java.util.Arrays;

public class KnowBe4GetAllTrainingEnrollmentsRequest {

    private static final String BASE_ENDPOINT = "/v1/training/enrollments?campaign_id=";
    private final Integer campaignID;
    private final KnowBe4ApiClient httpClient;
    private final String additionalParams = "&exclude_archived_users=true&include_campaign_id=true&include_employee_number=true&per_page=500";
    private final String cursorParam = "&cursor=";
    private String cursorValue = "true";

    /**
     * Cursor is a strange pagination tool, since the first request must be true and subsequent requests are integers.
     * See more here:
     * https://developer.knowbe4.com/rest/reporting#tag/Pagination
     */

    private KnowBe4AssignmentAndProgressResponse response;

    public KnowBe4GetAllTrainingEnrollmentsRequest(Integer campaignID, KnowBe4ApiClient httpClient, String cursorValue) {
        this.campaignID = campaignID;
        this.httpClient = httpClient;
        this.cursorValue = cursorValue;
    }

    public KnowBe4AssignmentAndProgressResponse fetch() throws IOException {
        String data = httpClient.get(endpoint());

        if (data.equalsIgnoreCase("[]")) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode assignmentJson = objectMapper.readTree(data).get("data");
        JsonNode pagination = objectMapper.readTree(data).get("pagination");

        KnowBe4AssignmentAndProgress[] knowBe4AssignmentAndProgressResponses =
                OutputUtils.jsonToObject(assignmentJson.toString(), KnowBe4AssignmentAndProgress[].class);

        KnowBe4Pagination knowBe4Pagination =
                OutputUtils.jsonToObject(pagination.toString(), KnowBe4Pagination.class);

        this.response = new KnowBe4AssignmentAndProgressResponse( Arrays.asList(knowBe4AssignmentAndProgressResponses) );
        this.response.setCusorValue(this.cursorValue);
        this.response.setNextCursor(knowBe4Pagination.getNextCursor());

        return response;
    }

    public KnowBe4GetAllTrainingEnrollmentsRequest next(KnowBe4AssignmentAndProgressResponse response) throws IOException {
        if ( response == null) {
            throw new IllegalStateException("'next()' can only be called after a successful call to 'fetch()'");
        }
        return new KnowBe4GetAllTrainingEnrollmentsRequest(campaignID, httpClient, response.getNextCursor());
    }

    public String getCursorValue() {
        return this.cursorValue;
    }

    public void setCursorValue(String cursorValue) {
        this.cursorValue = cursorValue;
    }

    private String endpoint() {
        StringBuilder builder = new StringBuilder(BASE_ENDPOINT);
        builder.append(campaignID);
        builder.append(additionalParams);
        builder.append(cursorParam);
        builder.append(cursorValue);
        return builder.toString();
    }

}
