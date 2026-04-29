package gov.nysenate.ess.core.service.pec.external.knowbe4.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class KnowBe4AssignmentAndProgressResponse {

    private String cursorValue;

    private String nextCursor;

    private List<KnowBe4AssignmentAndProgress> assignments;

    public KnowBe4AssignmentAndProgressResponse() {}

    public KnowBe4AssignmentAndProgressResponse(List<KnowBe4AssignmentAndProgress> assignments) {
        this.assignments = assignments;
    }

    public List<KnowBe4AssignmentAndProgress> getAssignmentsAndProgress() {
        return assignments;
    }

    public String getCursorValue() {
        return cursorValue;
    }

    public void setCursorValue(String cursorValue) {
        this.cursorValue = cursorValue;
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
