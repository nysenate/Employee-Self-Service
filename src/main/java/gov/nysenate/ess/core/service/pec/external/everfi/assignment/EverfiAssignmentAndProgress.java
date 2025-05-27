package gov.nysenate.ess.core.service.pec.external.everfi.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EverfiAssignmentAndProgress {

    private String id;

    @JsonProperty("user")
    public EverfiAssignmentUser user;

    @JsonProperty("past_due")
    private String pastDue;

    private List<EverfiAssignmentProgress> progress;
    private EverfiAssignment assignment;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("assigned_at")
    private String assignedAt;

    @JsonProperty("assignment_status")
    private String assignmentStatus;

    public EverfiAssignmentAndProgress() {
    }

    public String getId() {
        return id;
    }

    public EverfiAssignmentUser getUser() {
        return user;
    }

    public String getPastDue() {
        return pastDue;
    }

    public List<EverfiAssignmentProgress> getProgress() {
        return progress;
    }

    public EverfiAssignment getAssignment() {
        return assignment;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getAssignedAt() {
        return assignedAt;
    }

    public String getAssignmentStatus() {
        return assignmentStatus;
    }

    @Override
    public String toString() {
        return "EverfiProgressAndAssignment{" +
                "id='" + id + '\'' +
                ", user=" + user +
                ", pastDue='" + pastDue + '\'' +
                ", progress=" + progress +
                ", assignment=" + assignment +
                ", updatedAt='" + updatedAt + '\'' +
                ", assignedAt='" + assignedAt + '\'' +
                ", assignmentStatus='" + assignmentStatus + '\'' +
                '}';
    }
}
