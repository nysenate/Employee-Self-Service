package gov.nysenate.ess.travel.provider.gsa;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.dao.DataAccessException;

public interface GsaBatchResponseDao {

    void handleNewData(GsaResponse gsaResponse) throws JsonProcessingException,
            NullPointerException, DataAccessException;

    GsaResponse getGsaData(GsaResponseId gsaResponseId);

}
