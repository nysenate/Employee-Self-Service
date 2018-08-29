package gov.nysenate.ess.travel.provider.dod;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

@Service
public class DodService {

    private static final Logger logger = LoggerFactory.getLogger(DodService.class);

    @Autowired
    DodClient dodClient;

    public DodMealTier getNonConusMealInfo(String country, String city, LocalDate travelDate) throws IOException {

        Document doc = dodClient.connectToDod();
        dodClient.selectFromDodLandingForm(doc, country);
        Connection conn = dodClient.submitDodLandingForm(doc);
        return dodClient.gatherCityInfo(dodClient.connectToDodPerDiem(conn,country), city, travelDate);

    }

}
