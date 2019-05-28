package gov.nysenate.ess.core.client.view.pec.video;

import java.util.List;

/**
 * Structure for the request body of a video code submission.
 */
public class PECVideoCodeSubmission {

    private int empId;
    private int videoId;
    private List<String> codes;

    private PECVideoCodeSubmission() {}

    public int getEmpId() {
        return empId;
    }

    public int getVideoId() {
        return videoId;
    }

    public List<String> getCodes() {
        return codes;
    }
}
