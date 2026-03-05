package gov.nysenate.ess.core.service.pec.external.everfi.assignment;

import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.util.OutputUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;

/**
 * Calls the Everfi User assignments and progress API.
 */
public class EverfiAssignmentsAndProgressRequest {

    private static final String USER_ASSIGNMENTS_PATH = "/v1/progress/user_assignments";
    private final String path;
    private final EverfiApiClient everfiApiClient;
    private final String scrollId;
    private final String since;
    private final int limit;
    private EverfiAssignmentsAndProgressResponse response;

    private EverfiAssignmentsAndProgressRequest(
            String path,
            EverfiApiClient everfiApiClient,
            String scrollId,
            String since,
            int limit) {

        Assert.hasText(path, "path must not be empty");
        Assert.notNull(everfiApiClient, "httpClient must not be null");
        Assert.isTrue(limit >= 1 && limit <= 1000,
                "limit must be between 1 and 1000 inclusive");

        this.path = path;
        this.everfiApiClient = everfiApiClient;
        this.scrollId = scrollId;
        this.since = since;
        this.limit = limit;
    }

    /**
     * Static Factory Constructor - constructs a request for getting all user assignments and progress.
     *
     * @param everfiApiClient
     * @param since           Optional, ISO date time string representing where we left off querying last time. Can be
     *                        used to prevent importing duplicate records.
     * @param limit           How many results to get in each request.
     * @return
     */
    public static EverfiAssignmentsAndProgressRequest allUserAssignments(EverfiApiClient everfiApiClient,
                                                                         String since, int limit) {
        return new EverfiAssignmentsAndProgressRequest(USER_ASSIGNMENTS_PATH, everfiApiClient, null, since, limit);
    }

    /**
     * Static Factory Constructor - constructs a request for getting a single user's assignments and progress.
     *
     * @param everfiApiClient
     * @param userUuid        The everfi user uuid.
     * @param since           Optional, ISO date time string representing where we left off querying last time. Can be
     *                        used to prevent importing duplicate records.
     * @param limit           How many results to get in each request.
     * @return
     */
    public static EverfiAssignmentsAndProgressRequest userAssignments(EverfiApiClient everfiApiClient, String userUuid,
                                                                      String since, int limit) {
        Assert.hasText(userUuid, "userUuid must not be empty");
        String endpoint = USER_ASSIGNMENTS_PATH + "/" + userUuid;
        return new EverfiAssignmentsAndProgressRequest(endpoint, everfiApiClient, null, since, limit);
    }

    /**
     * Get a page of results for this request
     *
     * @return A list of {@link EverfiAssignmentAndProgress} or an empty list if there are no more results.
     * @throws IOException
     */
    public List<EverfiAssignmentAndProgress> fetch() throws IOException {
        String data = everfiApiClient.get(buildRequestPath());
        response = OutputUtils.jsonToObject(data, EverfiAssignmentsAndProgressResponse.class);
        return response.getAssignmentsAndProgress();
    }

    /**
     * @return a new {@code EverfiAssignmentsAndProgressRequest} that can fetch the next page of results,
     * or null if there are no more pages to fetch.
     * @throws IllegalStateException if this method is called before a successful call to {@code fetch()}.
     */
    public EverfiAssignmentsAndProgressRequest next() {
        if (response == null) {
            throw new IllegalStateException("'next()' can only be called after a successful call to 'fetch()'");
        }
        if (response.getNext().getScrollId() == null) {
            return null;
        }
        return new EverfiAssignmentsAndProgressRequest(path, everfiApiClient, response.getNext().getScrollId(),
                response.getNext().getSince(), this.limit);
    }

    public String getSince() {
        return this.since;
    }

    private String buildRequestPath() {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath(path)
                .queryParam("scroll_size", limit);

        if (StringUtils.isNotBlank(scrollId)) builder.queryParam("scroll_id", scrollId);
        if (StringUtils.isNotBlank(since)) builder.queryParam("since", since);
        return builder.toUriString();
    }

}
