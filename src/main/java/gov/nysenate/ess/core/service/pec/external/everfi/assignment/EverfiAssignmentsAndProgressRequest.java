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

    private static final String BASE_ENDPOINT = "/v1/progress/user_assignments";
    private final String fullEndpoint;
    private final EverfiApiClient httpClient;
    private final String scrollId;
    private final String since;
    private final int limit;
    private EverfiAssignmentsAndProgressResponse response;

    private EverfiAssignmentsAndProgressRequest(String fullEndpoint, EverfiApiClient httpClient, String scrollId,
                                               String since, int limit) {
        this.fullEndpoint = fullEndpoint;
        this.httpClient = httpClient;
        this.scrollId = scrollId;
        this.since = since;
        this.limit = limit;
    }

    public static EverfiAssignmentsAndProgressRequest allUserAssignments(EverfiApiClient everfiClient) {
        return EverfiAssignmentsAndProgressRequest.allUserAssignments(everfiClient, "", 100);
    }

    /**
     * Static Factory Constructor - constructs a request for getting all user assignments and progress.
     * @param everfiClient
     * @param since Optional, ISO date time string representing where we left off querying last time. Can be
     *              used to prevent importing duplicate records.
     * @param limit How many results to get in each request.
     * @return
     */
    public static EverfiAssignmentsAndProgressRequest allUserAssignments(EverfiApiClient everfiClient,
                                                                         String since, int limit) {
        return new EverfiAssignmentsAndProgressRequest(BASE_ENDPOINT, everfiClient, null, since, limit);
    }

    public static EverfiAssignmentsAndProgressRequest userAssignments(EverfiApiClient everfiClient, String userUuid) {
        return EverfiAssignmentsAndProgressRequest.userAssignments(everfiClient, userUuid, "", 100);
    }

    /**
     * Static Factory Constructor - constructs a request for getting a single user's assignments and progress.
     * @param everfiClient
     * @param userUuid The everfi user uuid.
     * @param since Optional, ISO date time string representing where we left off querying last time. Can be
     *              used to prevent importing duplicate records.
     * @param limit How many results to get in each request.
     * @return
     */
    public static EverfiAssignmentsAndProgressRequest userAssignments(EverfiApiClient everfiClient, String userUuid,
                                                                      String since, int limit) {
        String endpoint = BASE_ENDPOINT + "/" + userUuid;
        return new EverfiAssignmentsAndProgressRequest(endpoint, everfiClient, null, since, limit);
    }

    /**
     * Get a page of results for this request
     * @return A list of {@link EverfiAssignmentAndProgress} or an empty list if there are no more results.
     * @throws IOException
     */
    public List<EverfiAssignmentAndProgress> fetch() throws IOException {
        String data = httpClient.get(endpoint());
        response = OutputUtils.jsonToObject(data, EverfiAssignmentsAndProgressResponse.class);
        return response.getAssignmentsAndProgress();
    }

    /**
     * @return a new {@code EverfiAssignmentsAndProgressRequest} that can fetch the next page of results,
     *         or null if there are no more pages to fetch.
     * @throws IllegalStateException if this method is called before a successful call to {@code fetch()}.
     */
    public EverfiAssignmentsAndProgressRequest next() {
        if (response == null) {
            throw new IllegalStateException("'next()' can only be called after a successful call to 'fetch()'");
        }
        if (response.getNext().getScrollId() == null) {
            return null;
        }
        return new EverfiAssignmentsAndProgressRequest(fullEndpoint, httpClient, response.getNext().getScrollId(),
                response.getNext().getSince(), this.limit);
    }

    public String getSince() {
        return this.since;
    }

    private String endpoint() {
        StringBuilder builder = new StringBuilder(fullEndpoint);
        builder.append("?scroll_size=" + limit);
        if (StringUtils.isNotEmpty(scrollId)) {
            builder.append("&scroll_id=" + scrollId);
        }
        if (StringUtils.isNotEmpty(since)) {
            builder.append("&since=" + since);
        }

        return builder.toString();
    }
}
