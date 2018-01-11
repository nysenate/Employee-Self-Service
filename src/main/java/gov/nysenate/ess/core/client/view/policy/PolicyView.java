package gov.nysenate.ess.core.client.view.policy;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PolicyView implements ViewObject {

    private String title;
    private String link;
    private Boolean active;
    private Integer policyId;
    private LocalDateTime year;

    public PolicyView() {}

    public PolicyView(String title, String link, Boolean active, Integer policyId, LocalDateTime year) {
        this.title = title;
        this.link = link;
        this.active = active;
        this.policyId = policyId;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Integer policyId) {
        this.policyId = policyId;
    }

    public LocalDateTime getYear() {
        return year;
    }

    public void setYear(LocalDateTime year) {
        this.year = year;
    }

    @Override
    public String getViewType() {
        return "policy";
    }
}
