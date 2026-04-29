package gov.nysenate.ess.core.service.pec.external.knowbe4.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KnowBe4AssignmentUser {

    @JsonProperty("id")
    Integer id;

    @JsonProperty("first_name")
    String first_name;

    @JsonProperty("last_name")
    String last_name;

    @JsonProperty("email")
    String email;

    @JsonProperty("employee_number")
    String employee_number;

    public KnowBe4AssignmentUser() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getEmployee_number() {
        return employee_number;
    }

    public void setEmployee_number(String employee_number) {
        this.employee_number = employee_number;
    }
}
