package gov.nysenate.ess.supply.error;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupplyErrorLogService {

    @Autowired private SqlErrorLogDao errorLogDao;

    public void saveError(String message) {
        errorLogDao.insertErrorMessage(message);
    }
}
