package gov.nysenate.ess.travel.provider.gsa;

import gov.nysenate.ess.travel.utils.Dollars;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;

@Service
public class GsaLocalAllowanceService {

    private SqlGsaBatchResponseDao sqlGsaBatchResponseDao;


    public GsaLocalAllowanceService(SqlGsaBatchResponseDao sqlGsaBatchResponseDao) {
        this.sqlGsaBatchResponseDao = sqlGsaBatchResponseDao;
    }

    @NotNull
    public Dollars fetchLodgingRate(LocalDate date, String zip5) throws DataAccessException {
        GsaResponse gsaResponse = sqlGsaBatchResponseDao.getGsaRow(zip5);
        Month month = date.getMonth();
        return new Dollars(gsaResponse.getLodgingRates().get(month));
    }

    @NotNull
    public String fetchMealsRate(String zip5) throws DataAccessException {
        GsaResponse gsaResponse = sqlGsaBatchResponseDao.getGsaRow(zip5);
        return gsaResponse.getMealTier();
    }
}
