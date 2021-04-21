package gov.nysenate.ess.core.service.pec.external.everfi.assignment;

public class EverfiAssignment {

    private int id;
    private String name;

    public EverfiAssignment() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "EverfiAssignmentAssignment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
