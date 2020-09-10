package gov.nysenate.ess.core.service.pec.external.everfi;

import gov.nysenate.ess.core.util.OutputUtils;

import java.io.IOException;
import java.util.List;

/**
 * Calls the Everfi User assignments and progress API.
 */
public class EverfiAssignmentsGetRequest {

    // TODO when next.scroll_id is null, we have iterated through all responses... how should we handle this?

    private static final String GET_ASSIGNMENTS_ENDPOINT = "/v1/progress/user_assignments";
    private EverfiApiClient httpClient;
    private String scrollId;
    private String since;
    private int limit;
    private EverfiAssignmentsResponse response;

    public EverfiAssignmentsGetRequest(EverfiApiClient httpClient) {
        this(httpClient, "", "", 100);
    }

    public EverfiAssignmentsGetRequest(EverfiApiClient httpClient, String scrollId, String since, int limit) {
        this.httpClient = httpClient;
        this.scrollId = scrollId;
        this.since = since;
        this.limit = limit;
    }

    public List<EverfiAssignment> getAssignments() throws IOException {
        String data = httpClient.get(endpoint());
        System.out.println(data);
        response = OutputUtils.jsonToObject(data, EverfiAssignmentsResponse.class);
        System.out.println(response);
        return response.getAssignments();
    }

    public EverfiAssignmentsGetRequest next() {
        return new EverfiAssignmentsGetRequest(httpClient, response.getNext().scroll_id, response.getNext().since, this.limit);
    }

    public String getSince() {
        return this.since;
    }

    private String endpoint() {
        StringBuilder builder = new StringBuilder(GET_ASSIGNMENTS_ENDPOINT);
        builder.append("?scroll_size=" + limit);
        if (!scrollId.equals("")) {
            builder.append("&scroll_id=" + scrollId);
        }
        if (!since.equals("")) {
            builder.append("&since=" + since);
        }

        return builder.toString();
    }
}
