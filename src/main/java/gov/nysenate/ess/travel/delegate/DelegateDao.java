package gov.nysenate.ess.travel.delegate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DelegateDao {

    List<Delegate> activeDelegates(int principalId, LocalDate date);

    void saveDelegates(List<Delegate> delegates, int principalId);

    Optional<Delegate> delegateAssignedToEmp(int delegateEmpId, LocalDate date);
}
