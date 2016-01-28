package gov.nysenate.ess.core.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RespCenterHeadView implements ViewObject
{
    protected boolean active;
    protected String code;
    protected String shortName;
    protected String name;
    protected String affiliateCode;

    public RespCenterHeadView() {}

    public RespCenterHeadView(ResponsibilityHead head) {
        if (head != null) {
            this.active = head.isActive();
            this.code = head.getCode();
            this.shortName = head.getShortName();
            this.name = head.getName();
            this.affiliateCode = head.getAffiliateCode();
        }
    }

    public ResponsibilityHead toResponsibilityHead() {
        ResponsibilityHead head = new ResponsibilityHead();
        head.setActive(active);
        head.setCode(code);
        head.setShortName(shortName);
        head.setName(name);
        head.setAffiliateCode(affiliateCode);
        return head;
    }

    @Override
    public String getViewType() {
        return "responsibility-center-head";
    }

    @XmlElement
    public boolean isActive() {
        return active;
    }

    @XmlElement
    public String getCode() {
        return code;
    }

    @XmlElement
    public String getShortName() {
        return shortName;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    @XmlElement
    public String getAffiliateCode() {
        return affiliateCode;
    }
}
