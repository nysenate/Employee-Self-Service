package gov.nysenate.ess.core.service.pec.external.knowbe4.assignment;

import gov.nysenate.ess.core.service.pec.external.knowbe4.KnowBe4ApiClient;
import gov.nysenate.ess.core.util.OutputUtils;

import java.io.IOException;
import java.util.Arrays;

public class KnowBe4GetAllTrainingEnrollmentsRequest {

    private static final String BASE_ENDPOINT = "/v1/training/enrollments?campaign_id=";
    private final Integer campaignID;
    private final KnowBe4ApiClient httpClient;
    private final String additionalParams = "&exclude_archived_users=true&include_campaign_id=true&per_page=500";
    private final String page = "&page=";
    private int pageNumber = 1;
    private KnowBe4AssignmentAndProgressResponse response;

    public KnowBe4GetAllTrainingEnrollmentsRequest(Integer campaignID, KnowBe4ApiClient httpClient, int pageNumber) {
        this.campaignID = campaignID;
        this.httpClient = httpClient;
        this.pageNumber = pageNumber;
    }

    public KnowBe4AssignmentAndProgressResponse fetch() throws IOException {
        String data = httpClient.get(endpoint());

        if (data.equalsIgnoreCase("[]")) {
            return null;
        }

        KnowBe4AssignmentAndProgress[] knowBe4AssignmentAndProgressResponses =
                OutputUtils.jsonToObject(data, KnowBe4AssignmentAndProgress[].class);

        this.response = new KnowBe4AssignmentAndProgressResponse(Arrays.asList(knowBe4AssignmentAndProgressResponses));
        this.response.setPage(this.pageNumber);

        return response;
    }

    public KnowBe4GetAllTrainingEnrollmentsRequest next(KnowBe4AssignmentAndProgressResponse response) throws IOException {
        if (response == null) {
            throw new IllegalStateException("'next()' can only be called after a successful call to 'fetch()'");
        }
        return new KnowBe4GetAllTrainingEnrollmentsRequest(campaignID, httpClient, response.getPage() + 1);
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    private String endpoint() {
        String builder = BASE_ENDPOINT + campaignID +
                additionalParams +
                page +
                pageNumber;
        return builder;
    }

}
