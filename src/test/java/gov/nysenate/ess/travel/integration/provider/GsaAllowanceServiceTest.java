package gov.nysenate.ess.travel.integration.provider;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.util.HttpUtils;
import gov.nysenate.ess.travel.fixtures.AddressFixture;
import gov.nysenate.ess.travel.fixtures.GsaApiResponseFixture;
import gov.nysenate.ess.travel.provider.gsa.GsaAllowanceService;
import gov.nysenate.ess.travel.provider.gsa.GsaApi;
import gov.nysenate.ess.travel.provider.gsa.GsaCache;
import gov.nysenate.ess.travel.provider.gsa.GsaResponseParser;
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
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@org.junit.experimental.categories.Category(IntegrationTest.class)
public class GsaAllowanceServiceTest extends BaseTest {

    private static final LocalDate DATE = LocalDate.of(2018, 1, 1);

    @Spy @Autowired private GsaCache gsaCache;
    @Autowired private GsaResponseParser responseParser;
    @Mock private HttpUtils httpUtils;
    private GsaApi gsaApi;
    private GsaAllowanceService service;

    @Before
    public void before() throws IOException {
        MockitoAnnotations.initMocks(this);
        gsaApi = new GsaApi("", responseParser, httpUtils);
        service = new GsaAllowanceService(gsaCache, gsaApi);
        gsaCache.evictCache();
    }

    @Test
    public void fetchesMealRateAndSaveToCache() throws IOException {
        when(httpUtils.urlToString(anyString())).thenReturn(GsaApiResponseFixture.fy2018_zip10008_response());

        Dollars expected = new Dollars("74");
        Dollars actual = service.fetchMealRate(DATE, AddressFixture.zip10008());

        Mockito.verify(gsaCache, times(1)).queryGsa(DATE, "10008"); // Attempted to query from cache.
        assertEquals(expected, actual); // Got correct value from gsaApi
        Mockito.verify(gsaCache, times(1)).saveToCache(anyObject()); // Verify response saved to cache.
    }

    @Test
    public void fetchLodgingRateAndSaveToCache() throws IOException {
        when(httpUtils.urlToString(anyString())).thenReturn(GsaApiResponseFixture.fy2018_zip10008_response());

        Dollars expected = new Dollars("164");
        Dollars actual = service.fetchLodgingRate(DATE, AddressFixture.zip10008());

        Mockito.verify(gsaCache, times(1)).queryGsa(DATE, "10008"); // Attempted to query from cache.
        assertEquals(expected, actual);
        Mockito.verify(gsaCache, times(1)).saveToCache(anyObject()); // Verify response saved to cache.
    }

    @Test
    public void requestFarInFuture_ReturnLatestYearWeHaveDataFor() {

    }
}
