package gov.nysenate.ess.core.model.pec.knowbe4;

public class KnowBe4AssignmentID {
    private int id;
    private int taskID;

    public KnowBe4AssignmentID(int taskID, int id) {
        this.id = id;
        this.taskID = taskID;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }
}
