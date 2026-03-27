package gov.nysenate.ess.core.model.pec.everfi;

public class EverfiEmployeeMapping {

    private Integer empId;
    private String everfiUuid;

    public EverfiEmployeeMapping(Integer empId, String everfiUuid) {
        this.empId = empId;
        this.everfiUuid = everfiUuid;
    }

    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public String getEverfiUuid() {
        return everfiUuid;
    }

    public void setEverfiUuid(String everfiUuid) {
        this.everfiUuid = everfiUuid;
    }
}
