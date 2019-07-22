package gov.nysenate.ess.travel.delegate;

import java.util.List;

public interface DelegationDao {

    List<Delegation> findByPrincipalEmpId(int principalId);

    void save(List<Delegation> delegations, int principalId);

    List<Delegation> findByDelegateEmpId(int delegateEmpId);
}
