package gov.nysenate.ess.core.service.pec.external.everfi.assignment;

import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.util.OutputUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * Calls the Everfi User assignments and progress API.
 */
public class EverfiAssignmentsAndProgressRequest {

    private static final String GET_ASSIGNMENTS_ENDPOINT = "/v1/progress/user_assignments";
    private EverfiApiClient httpClient;
    private String scrollId;
    private String since;
    private int limit;
    private EverfiAssignmentsAndProgressResponse response;

    public EverfiAssignmentsAndProgressRequest(EverfiApiClient httpClient) {
        this(httpClient, "", "", 100);
    }

    public EverfiAssignmentsAndProgressRequest(EverfiApiClient httpClient, String scrollId, String since, int limit) {
        this.httpClient = httpClient;
        this.scrollId = scrollId;
        this.since = since;
        this.limit = limit;
    }

    public List<EverfiAssignmentAndProgress> getAssignmentsAndProgress() throws IOException {
        String data = httpClient.get(endpoint());
        System.out.println(data);
        response = OutputUtils.jsonToObject(data, EverfiAssignmentsAndProgressResponse.class);
        System.out.println(response);
        return response.getAssignmentsAndProgress();
    }

    public EverfiAssignmentsAndProgressRequest next() {
        // TODO when next.scroll_id is null, we have iterated through all responses... how should we handle this?
        if (StringUtils.isEmpty(response.getNext().getScrollId())) {
            // TODO Should save since value so we can restart there on next update
            return null;
        }
        return new EverfiAssignmentsAndProgressRequest(httpClient, response.getNext().getScrollId(), response.getNext().getSince(), this.limit);
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
