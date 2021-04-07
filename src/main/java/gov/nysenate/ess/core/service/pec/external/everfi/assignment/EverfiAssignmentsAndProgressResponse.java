package gov.nysenate.ess.core.service.pec.external.everfi.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EverfiAssignmentsAndProgressResponse {

    private EverfiAssignmentAndProgressResponseNext next;

    @JsonProperty("data")
    private List<EverfiAssignmentAndProgress> assignments;

    public EverfiAssignmentsAndProgressResponse() {
    }

    public EverfiAssignmentAndProgressResponseNext getNext() {
        return next;
    }

    public List<EverfiAssignmentAndProgress> getAssignmentsAndProgress() {
        return assignments;
    }

    @Override
    public String toString() {
        return "EverfiAssignmentsResponse{" +
                "next=" + next +
                ", assignments=" + assignments +
                '}';
    }

}
