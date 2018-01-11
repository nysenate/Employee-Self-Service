package gov.nysenate.ess.core.model.policy;

import java.time.LocalDateTime;

public class Policy {

    private String title;
    private String link;
    private Boolean active;
    private Integer policyId;
    private LocalDateTime effectiveDateTime;

    public Policy() {}

    public Policy(String title, String link, Boolean active, Integer policyId, LocalDateTime effectiveDateTime) {
        this.title = title;
        this.link = link;
        this.active = active;
        this.policyId = policyId;
        this.effectiveDateTime = effectiveDateTime;
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

    public LocalDateTime getEffectiveDateTime() {
        return effectiveDateTime;
    }

    public void setEffectiveDateTime(LocalDateTime effectiveDateTime) {
        this.effectiveDateTime = effectiveDateTime;
    }
}
