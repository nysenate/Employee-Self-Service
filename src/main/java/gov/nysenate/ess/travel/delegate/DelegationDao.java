package gov.nysenate.ess.travel.delegate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DelegationDao {

    List<Delegation> findByPrincipalEmpId(int principalId);

    void save(List<Delegation> delegations, int principalId);

    Optional<Delegation> findByDelegateEmpId(int delegateEmpId, LocalDate date);
}
