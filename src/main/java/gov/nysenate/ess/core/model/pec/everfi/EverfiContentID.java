package gov.nysenate.ess.core.model.pec.everfi;

/**
 * Object that Maps an everfi content ID in the database with its corresponding personnel task id
 */
public class EverfiContentID {

    private String id;
    private int taskID;

    public EverfiContentID(int taskID, String id) {
        this.id = id;
        this.taskID = taskID;
    }

    public String getID() {
        return id;
    }

    public void setID(String contentID) {
        this.id = contentID;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }
}
