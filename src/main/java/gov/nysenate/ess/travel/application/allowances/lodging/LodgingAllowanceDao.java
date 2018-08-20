package gov.nysenate.ess.travel.application.allowances.lodging;

import java.util.UUID;

public interface LodgingAllowanceDao {

    void insertLodgingAllowances(UUID versionId, LodgingAllowances lodgingAllowances);

    LodgingAllowances getLodgingAllowances(UUID versionId);
}
