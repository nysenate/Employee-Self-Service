package gov.nysenate.ess.core.client.view.pec.video;

import java.util.List;

/**
 * Structure for the request body of a video code submission.
 */
public class PECCodeSubmission {

    private int empId;
    private int taskId;
    private List<String> codes;

    private String trainingDate;

    private PECCodeSubmission() {}

    public int getEmpId() {
        return empId;
    }

    public int getTaskId() {
        return taskId;
    }

    public List<String> getCodes() {
        return codes;
    }

    public String getTrainingDate() {
        return trainingDate;
    }

}
