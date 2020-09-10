package gov.nysenate.ess.core.service.pec.external.everfi.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EverfiAssignment {

    private int id;
    private String name;
    // TODO not sure what these are yet
//    @JsonProperty("training_period_id")
//    private String trainingPeriodId;
//    @JsonProperty("training_period_name")
//    private String trainingPeriodName;


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
