package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.application.allowances.Allowances;
import gov.nysenate.ess.travel.application.overrides.perdiem.PerDiemOverrides;
import gov.nysenate.ess.travel.application.route.Route;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A set of changes to a Application.
 */
public class Amendment {

    protected int id; // TODO init/add to constructors. Should be 0 if not stored in the db.
    protected int appId; // TODO initialize in constructors
    protected Version version;
    protected String purposeOfTravel;
    protected Route route;
    protected Allowances allowances;
    protected PerDiemOverrides perDiemOverrides;
    protected TravelApplicationStatus status;
    protected List<TravelAttachment> attachments;
    protected LocalDateTime createdDateTime; // TODO For the first amendment this should be the submitted date.
    protected Employee createdBy;

    public Amendment(int id, int appId, Version version, String purposeOfTravel, Route route, Allowances allowances,
                     PerDiemOverrides perDiemOverrides, TravelApplicationStatus status,
                     List<TravelAttachment> attachments, LocalDateTime createdDateTime, Employee createdBy) {
        this.id = id;
        this.appId = appId;
        this.version = version;
        this.purposeOfTravel = purposeOfTravel;
        this.route = route;
        this.allowances = allowances;
        this.perDiemOverrides = perDiemOverrides;
        this.status = status;
        this.attachments = attachments;
        this.createdDateTime = createdDateTime;
        this.createdBy = createdBy;
    }

    public Version version() {
        return version;
    }

    public void approve() {
        status.approve();
    }
}
