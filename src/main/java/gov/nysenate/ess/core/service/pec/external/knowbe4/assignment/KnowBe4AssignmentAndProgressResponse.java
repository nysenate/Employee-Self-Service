package gov.nysenate.ess.core.service.pec.external.knowbe4.assignment;

import java.util.List;

public class KnowBe4AssignmentAndProgressResponse {

    private int page;

    private List<KnowBe4AssignmentAndProgress> assignments;

    public KnowBe4AssignmentAndProgressResponse() {
    }

    public KnowBe4AssignmentAndProgressResponse(List<KnowBe4AssignmentAndProgress> assignments) {
        this.assignments = assignments;
    }

    public List<KnowBe4AssignmentAndProgress> getAssignmentsAndProgress() {
        return assignments;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<KnowBe4AssignmentAndProgress> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<KnowBe4AssignmentAndProgress> assignments) {
        this.assignments = assignments;
    }
}
