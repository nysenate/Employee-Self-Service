package gov.nysenate.ess.travel.provider.gsa;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.nysenate.ess.travel.provider.gsa.model.GsaInfo;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface GsaBatchResponseDao {

    void handleNewData(GsaResponse gsaResponse) throws JsonProcessingException,
            NullPointerException, DataAccessException;

    GsaResponse getGsaData(GsaResponseId gsaResponseId);

    void insertGsaArchiveData(List<GsaInfo> archivedGsaData) throws DataAccessException, JsonProcessingException;
}
