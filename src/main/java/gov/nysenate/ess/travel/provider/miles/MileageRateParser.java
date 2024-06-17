package gov.nysenate.ess.travel.provider.miles;

import gov.nysenate.ess.core.util.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.google.common.base.Preconditions.checkArgument;

public final class MileageRateParser {
    private MileageRateParser() {}

    /**
     * Parses the gsa irs reimbursement rates website to extract the current {@link MileageRate}.
     * <a href="https://www.gsa.gov/travel/plan-book/transportation-airfare-rates-pov-rates-etc/privately-owned-vehicle-pov-mileage-reimbursement-rates">...</a>
     * @param content The HTML content of the website.
     * @return the parsed MileageRate.
     */
    public static MileageRate parseMileageRate(String content) {
        checkArgument(content != null, "content cannot be null.");
        checkArgument(!content.isEmpty(), "content cannot be an empty string.");
        Element autoRowEl = Jsoup.parse(content).getElementsByTag("table").first()
                .getElementsByTag("tbody").first().getElementsByTag("tr").get(1);
        String rate = autoRowEl.getElementsByTag("td").last().text().replace("$", "");

        // Parse rate's start date.
        Element startDateEl = autoRowEl.select("td strong").first();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        LocalDate startDate = LocalDate.parse(startDateEl.text(), dateFormat);
        return new MileageRate(startDate, DateUtils.THE_FUTURE, rate);
    }
}
