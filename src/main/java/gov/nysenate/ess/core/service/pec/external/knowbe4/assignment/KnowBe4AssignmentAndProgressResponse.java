package gov.nysenate.ess.core.service.pec.external.knowbe4.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class KnowBe4AssignmentAndProgressResponse {

    private String cusorValue;

    private String nextCursor;

    private List<KnowBe4AssignmentAndProgress> assignments;

    public KnowBe4AssignmentAndProgressResponse() {}

    public KnowBe4AssignmentAndProgressResponse(List<KnowBe4AssignmentAndProgress> assignments) {
        this.assignments = assignments;
    }

    public List<KnowBe4AssignmentAndProgress> getAssignmentsAndProgress() {
        return assignments;
    }

    public String getCusorValue() {
        return cusorValue;
    }

    public void setCusorValue(String cusorValue) {
        this.cusorValue = cusorValue;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }

    public List<KnowBe4AssignmentAndProgress> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<KnowBe4AssignmentAndProgress> assignments) {
        this.assignments = assignments;
    }
}
