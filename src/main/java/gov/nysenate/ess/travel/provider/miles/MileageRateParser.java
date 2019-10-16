package gov.nysenate.ess.travel.provider.miles;

import gov.nysenate.ess.core.util.DateUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class MileageRateParser {

    /**
     * Parses the gsa irs reimbursement rates website to extract the current {@link MileageRate}.
     * https://www.gsa.gov/travel/plan-book/transportation-airfare-rates-pov-rates-etc/privately-owned-vehicle-pov-mileage-reimbursement-rates
     *
     * @param content The HTML content of the website.
     * @return
     */
    public MileageRate parseMileageRate(String content) {
        checkArgument(content != null, "content cannot be null.");
        checkArgument(!content.isEmpty(), "content cannot be an empty string.");

        // Parse mileage rate.
        Document document = Jsoup.parse(content);
        Element bodyContentEl = document.getElementById("asto-content");
        Element tableBodyEl = bodyContentEl.getElementsByTag("tbody").first();
        Element autoRowEl = tableBodyEl.getElementsByTag("tr").get(1);
        Element autoRateEl = autoRowEl.getElementsByTag("td").last();
        String rate = autoRateEl.text().replace("$", "");

        // Parse rate's start date.
        Element startDateEl = autoRowEl.select("td strong").first();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        LocalDate startDate = LocalDate.parse(startDateEl.text(), dateFormat);

        return new MileageRate(startDate, DateUtils.THE_FUTURE, rate);
    }
}
