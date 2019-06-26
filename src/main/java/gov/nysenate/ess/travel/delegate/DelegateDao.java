package gov.nysenate.ess.travel.delegate;

import java.time.LocalDate;
import java.util.List;

public interface DelegateDao {

    List<Delegate> activeDelegates(int principalId, LocalDate date);

    void saveDelegates(List<Delegate> delegates);
}
