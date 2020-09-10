package gov.nysenate.ess.web;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiAssignment;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiAssignmentsGetRequest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertFalse;

/**
 * A sample file to run misc tests.
 */
@Category(gov.nysenate.ess.core.annotation.SillyTest.class)

public class SillyTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(SillyTest.class);

    @Autowired private EverfiApiClient client;

    @Test
    public void everfiApiClientDemo() throws IOException {

        // Create the desired request and provide it an instance of the everfi api client.
        // Here we are creating an request for the get user assignments and progress endpoint.
        // Each API endpoint would eventually have its own Request class.
        EverfiAssignmentsGetRequest request = new EverfiAssignmentsGetRequest(client);
        // Get the first 'page' of Assignment And Progress results.
        List<EverfiAssignment> assignments = request.getAssignments();
        // Get a new request, setup to call for the next 'page' of results.
        request = request.next();
        // This would get the 2nd 'page' of results.
        assignments = request.getAssignments();
    }
}
