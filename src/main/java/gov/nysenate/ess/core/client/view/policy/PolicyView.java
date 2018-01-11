package gov.nysenate.ess.core.client.view.policy;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.policy.Policy;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

@XmlRootElement
public class PolicyView implements ViewObject {

    private static final String policyDir = "/assets/policies/";

    private String title;
    private String url;
    private Boolean active;
    private Integer policyId;
    private LocalDateTime effectiveDateTime;

    protected PolicyView() {}

    public PolicyView(Policy policy, String ctxPath) {
        this.title = policy.getTitle();
        this.url = ctxPath + policyDir + policy.getFilename();
        this.active = policy.getActive();
        this.policyId = policy.getPolicyId();
        this.effectiveDateTime = policy.getEffectiveDateTime();
    }

    @XmlElement
    public String getTitle() {
        return title;
    }

    @XmlElement
    public String getUrl() {
        return url;
    }

    @XmlElement
    public Boolean getActive() {
        return active;
    }

    @XmlElement
    public Integer getPolicyId() {
        return policyId;
    }

    @XmlElement
    public LocalDateTime getEffectiveDateTime() {
        return effectiveDateTime;
    }

    @Override
    @XmlElement
    public String getViewType() {
        return "policy";
    }
}
