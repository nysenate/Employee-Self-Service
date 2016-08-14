package gov.nysenate.ess.core.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.personnel.ResponsibilityCenter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;

@XmlRootElement
public class RespCenterView implements ViewObject
{
    protected boolean active;
    protected int code;
    protected String name;
    protected LocalDate effectiveDateBegin;
    protected LocalDate effectiveDateEnd;
    protected String agencyCode;
    protected String agencyName;
    protected RespCenterHeadView respCenterHead;

    public RespCenterView() {}

    public RespCenterView(ResponsibilityCenter respCtr) {
        if (respCtr != null) {
            this.active = respCtr.isActive();
            this.code = respCtr.getCode();
            this.name = respCtr.getName();
            this.effectiveDateBegin = respCtr.getEffectiveDateBegin();
            this.effectiveDateEnd = respCtr.getEffectiveDateEnd();
            if (respCtr.getAgency() != null) {
                this.agencyCode = respCtr.getAgency().getCode();
                this.agencyName = respCtr.getAgency().getName();
            }
            if (respCtr.getHead() != null) {
                this.respCenterHead = new RespCenterHeadView(respCtr.getHead());
            }
        }
    }

    @Override
    @XmlElement
    public String getViewType() {
        return "responsibility-center";
    }

    @XmlElement
    public boolean isActive() {
        return active;
    }

    @XmlElement
    public int getCode() {
        return code;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    @XmlElement
    public LocalDate getEffectiveDateBegin() {
        return effectiveDateBegin;
    }

    @XmlElement
    public LocalDate getEffectiveDateEnd() {
        return effectiveDateEnd;
    }

    @XmlElement
    public String getAgencyCode() {
        return agencyCode;
    }

    @XmlElement
    public String getAgencyName() {
        return agencyName;
    }

    @XmlElement
    public RespCenterHeadView getRespCenterHead() {
        return respCenterHead;
    }
}
