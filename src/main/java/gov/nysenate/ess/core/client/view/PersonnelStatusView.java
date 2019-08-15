package gov.nysenate.ess.core.client.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.personnel.PersonnelStatus;

public class PersonnelStatusView implements ViewObject {

    private String name;
    @JsonProperty("cdenclv")
    private boolean cdenclv;
    private String description;
    @JsonProperty("employed")
    private boolean employed;
    @JsonProperty("timeEntryRequired")
    private boolean timeEntryRequired;
    private int effectDateOffset;

    public PersonnelStatusView() {}

    public PersonnelStatusView(PersonnelStatus ps) {
        this.name = ps.name();
        this.cdenclv = ps.isCDENCLV();
        this.description = ps.getDescription();
        this.employed = ps.isEmployed();
        this.timeEntryRequired = ps.isTimeEntryRequired();
        this.effectDateOffset = ps.getEffectDateOffset();
    }

    public PersonnelStatus toPersonnelStatus() {
        return PersonnelStatus.valueOf(name);
    }

    public String getName() {
        return name;
    }

    public boolean isCdenclv() {
        return cdenclv;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEmployed() {
        return employed;
    }

    public boolean isTimeEntryRequired() {
        return timeEntryRequired;
    }

    public int getEffectDateOffset() {
        return effectDateOffset;
    }

    @Override
    public String getViewType() {
        return "personnel-status";
    }
}
