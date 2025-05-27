package gov.nysenate.ess.core.model.pec.everfi;

public class EverfiUserIDs {

    private Integer empID;
    private String everfiUUID;

    public EverfiUserIDs(Integer empID, String everfiUUID) {
        this.empID = empID;
        this.everfiUUID = everfiUUID;
    }

    public Integer getEmpID() {
        return empID;
    }

    public void setEmpID(Integer empID) {
        this.empID = empID;
    }

    public String getEverfiUUID() {
        return everfiUUID;
    }

    public void setEverfiUUID(String everfiUUID) {
        this.everfiUUID = everfiUUID;
    }
}
