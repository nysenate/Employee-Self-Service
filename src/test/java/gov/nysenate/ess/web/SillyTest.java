package gov.nysenate.ess.web;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.service.pec.external.everfi.assignment.EverfiAssignmentAndProgress;
import gov.nysenate.ess.core.service.pec.external.everfi.assignment.EverfiAssignmentsAndProgressRequest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;


/**
 * A sample file to run misc tests.
 */
@Category(gov.nysenate.ess.core.annotation.SillyTest.class)

public class SillyTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(SillyTest.class);

    @Autowired private EverfiApiClient client;

//    @Test
//    public void everfiApiClientDemo() throws IOException {
//
//        // Create the desired request and provide it an instance of the everfi api client.
//        EverfiAssignmentsAndProgressRequest request = new EverfiAssignmentsAndProgressRequest(client);
//
//        // Loop through all assigment and progress results.
//        List<EverfiAssignmentAndProgress> assignmentsAndProgress;
//        while (request != null) {
//            // Call method on the request and get back the model object created from Everfi json.
//            // Authentication and deserialization is handled by the request object.
//            assignmentsAndProgress = request.getAssignmentsAndProgress();
//            // Move on to the next 'page' of results.
//            request = request.next();
//        }
//    }
}
