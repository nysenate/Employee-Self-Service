package gov.nysenate.ess.time.client.response;

import gov.nysenate.ess.time.client.view.accrual.AccrualsView;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RecordEntryInfo")
public class RecordEntryInfoResponse
{
    @XmlElement
    protected boolean status = false;

    @XmlElement
    protected AccrualsView accruals;

    public RecordEntryInfoResponse() {
//        this.accruals = new AccrualsView();
    }

    public boolean isStatus() {
        return status;
    }

    public AccrualsView getAccruals() {
        return accruals;
    }
}