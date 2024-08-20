package gov.nysenate.ess.core.client.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.personnel.Agency;

public class AgencyView implements ViewObject {

    protected String code;
    @JsonProperty("isActive")
    protected boolean isActive;
    protected String shortName;
    protected String name;

    @JsonProperty("isSenatorAgency")
    protected boolean isSenatorAgency;

    public AgencyView() {
    }

    public AgencyView(Agency agency) {
        this.code = agency.getCode();
        this.isActive = agency.isActive();
        this.shortName = agency.getShortName();
        this.name = agency.getName();
    }

    public Agency toAgency() {
        Agency a = new Agency();
        a.setCode(code);
        a.setActive(isActive);
        a.setShortName(shortName);
        a.setName(name);
        return a;
    }

    public String getCode() {
        return code;
    }

    @JsonIgnore
    public boolean isActive() {
        return isActive;
    }

    public String getShortName() {
        return shortName;
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public boolean isSenatorAgency() {
        return isSenatorAgency;
    }

    @Override
    public String getViewType() {
        return "agency";
    }
}
