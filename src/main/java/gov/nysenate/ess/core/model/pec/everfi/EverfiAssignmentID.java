package gov.nysenate.ess.core.model.pec.everfi;

/**
 * Object that Maps an everfi assignment ID in the database with its corresponding personnel task id
 */
public class EverfiAssignmentID {

    private int id;
    private int taskID;

    public EverfiAssignmentID(int taskID, int id) {
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
