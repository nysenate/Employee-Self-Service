package gov.nysenate.ess.travel.fixtures;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class MileageRateWebsiteFixture {

    /**
     * Provides the html of the gsa website used to scrape mileage rates.
     * https://www.gsa.gov/travel/plan-book/transportation-airfare-rates-pov-rates/privately-owned-vehicle-pov-mileage-reimbursement-rates
     */
    public static String html() throws IOException {
        String path = MileageRateWebsiteFixture.class.getClassLoader().getResource("travel/GsaMileageRateWebsiteHtml.txt").getFile();
        return FileUtils.readFileToString(new File(path));
    }
}
