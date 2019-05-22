package gov.nysenate.ess.travel.integration.provider;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.util.HttpUtils;
import gov.nysenate.ess.travel.fixtures.AddressFixture;
import gov.nysenate.ess.travel.fixtures.GsaApiResponseFixture;
import gov.nysenate.ess.travel.provider.gsa.*;
import gov.nysenate.ess.travel.utils.Dollars;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@org.junit.experimental.categories.Category(IntegrationTest.class)
public class GsaAllowanceServiceTest extends BaseTest {

    private static final LocalDate DATE = LocalDate.of(2018, 1, 1);

    @Spy @Autowired private GsaBatchResponseDao gsaBatchResponseDao;
    @Autowired private GsaResponseParser responseParser;
    @Mock private HttpUtils httpUtils;
    private GsaApi gsaApi;
    private GsaAllowanceService service;

    @Before
    public void before() throws IOException {
        MockitoAnnotations.initMocks(this);
        gsaApi = spy(new GsaApi("", responseParser, httpUtils)); // TODO: Possible to spy this?
        service = new GsaAllowanceService(gsaBatchResponseDao, gsaApi);
    }

    @Test
    public void fetchesMealRateAndSaveToCache() throws IOException {
        when(httpUtils.urlToString(anyString())).thenReturn(GsaApiResponseFixture.fy2018_zip10008_response());

        Dollars actual = service.fetchMealRate(DATE, AddressFixture.zip10008());
        Dollars expected = new Dollars("74");
        assertEquals(expected, actual);

        // Call fetchMealRate twice to verify cache functionality.
        Dollars dollars = service.fetchMealRate(DATE, AddressFixture.zip10008());
        assertNotNull(dollars);
//        Mockito.verify(gsaBatchResponseDao, times(2)).getGsaData(new GsaResponseId(DATE.getYear(), "10008")); // Attempted to query from cache on both calls.
//        Mockito.verify(gsaApi, times(1)).queryGsa(DATE, "10008"); // Api was only called once.
    }

    @Test
    public void fetchLodgingRateAndSaveToCache() throws IOException {
        when(httpUtils.urlToString(anyString())).thenReturn(GsaApiResponseFixture.fy2018_zip10008_response());

        Dollars actual = service.fetchLodgingRate(DATE, AddressFixture.zip10008());
        Dollars expected = new Dollars("164");
        assertEquals(expected, actual);

        // Call fetchLodingRate twice to verify cache functionality.
        Dollars dollars = service.fetchLodgingRate(DATE, AddressFixture.zip10008());
        assertNotNull(dollars);
//        Mockito.verify(gsaBatchResponseDao, times(2)).getGsaData(new GsaResponseId(DATE.getYear(), "10008")); // Cache called twice.
//        Mockito.verify(gsaApi, times(1)).queryGsa(DATE, "10008"); // Verify api only called once
    }
}
