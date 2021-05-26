package gov.nysenate.ess.travel.request.app.dao;

import gov.nysenate.ess.travel.request.amendment.Version;
import gov.nysenate.ess.travel.request.app.PurposeOfTravel;

import java.time.LocalDateTime;

class AmendmentRepositoryView {

    public int amendmentId;
    public int appId;
    public Version version;
    public PurposeOfTravel pot;
    public LocalDateTime createdDateTime;
    public int createdByEmpId;
    // select with amdId: route, allowances, mpds, lpds, attachments

    public AmendmentRepositoryView() {
    }
}
