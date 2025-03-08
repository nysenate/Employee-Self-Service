package gov.nysenate.ess.core.service.pec.external.knowbe4.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class KnowBe4AssignmentAndProgress {

    @JsonProperty("enrollment_id")
    private Integer enrollment_id;

    @JsonProperty("content_type")
    private String content_type;

    @JsonProperty("module_name")
    private String module_name;

    @JsonProperty("user")
    private KnowBe4AssignmentUser user;

    @JsonProperty("campaign_id")
    private Integer campaign_id;

    @JsonProperty("campaign_name")
    private String campaign_name;

    @JsonProperty("enrollment_date")
    private LocalDateTime enrollment_date;

    @JsonProperty("start_date")
    private LocalDateTime start_date;

    @JsonProperty("completion_date")
    private LocalDateTime completion_date;

    @JsonProperty("status")
    private String status;

    private Integer time_spent;

    @JsonProperty("policy_acknowledged")
    private boolean policy_acknowledged;

    public KnowBe4AssignmentAndProgress() {
    }

    public Integer getEnrollment_id() {
        return enrollment_id;
    }

    public void setEnrollment_id(Integer enrollment_id) {
        this.enrollment_id = enrollment_id;
    }

    public String getModule_name() {
        return module_name;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }

    public KnowBe4AssignmentUser getUser() {
        return user;
    }

    public void setUser(KnowBe4AssignmentUser user) {
        this.user = user;
    }

    public Integer getCampaign_id() {
        return campaign_id;
    }

    public void setCampaign_id(Integer campaign_id) {
        this.campaign_id = campaign_id;
    }

    public String getCampaign_name() {
        return campaign_name;
    }

    public void setCampaign_name(String campaign_name) {
        this.campaign_name = campaign_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean getPolicy_acknowledged() {
        return policy_acknowledged;
    }

    public void setPolicy_acknowledged(boolean policy_acknowledged) {
        this.policy_acknowledged = policy_acknowledged;
    }

    public LocalDateTime getCompletionDate() {
        return this.completion_date;
    }

    public void setCompletionDate(LocalDateTime completion_date) {
        this.completion_date = completion_date;
    }
}
