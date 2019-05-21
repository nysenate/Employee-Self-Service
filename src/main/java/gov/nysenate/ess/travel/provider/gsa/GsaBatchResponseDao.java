package gov.nysenate.ess.travel.provider.gsa;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface GsaBatchResponseDao {

    public void handleNewData(GsaResponse gsaResponse) throws JsonProcessingException;

    public void insertGsaData(GsaResponse gsaResponse) throws JsonProcessingException;

    public void updateGsaData(GsaResponse gsaResponse) throws JsonProcessingException;

    public GsaResponse getGsaData(GsaResponseId gsaResponseId);

}
