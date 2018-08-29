package gov.nysenate.ess.travel.application.allowances.mileage;

import java.util.UUID;

public interface MileageAllowanceDao {

    void insertMileageAllowances(UUID versionId, MileageAllowances mileageAllowances);

    MileageAllowances getMileageAllowance(UUID versionId);
}
