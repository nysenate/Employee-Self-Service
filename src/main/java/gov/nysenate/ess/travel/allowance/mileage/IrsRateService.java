package gov.nysenate.ess.travel.allowance.mileage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class IrsRateService {

    @Autowired private IrsRateDao irsRateDao;
    @Value("${travel.irs.link}") private String irsLink;

    @PostConstruct
    public void postConstruct() {
        // Ensure the database has an initialized value.
        try {
            this.initializeDatabase();
        } catch (IOException e ) {
            e.printStackTrace();
        }
    }

    public void initializeDatabase() throws IOException {
        Document doc = Jsoup.connect(irsLink).get();
        Elements rows = doc.select("article table tbody tr");
        for (Element e : rows) {
            String date = e.children().get(0).text();
            double rate = Double.parseDouble(e.children().get(1).text());
            if (date.length() == 4) {
                String startDate = date + "-01-01";
                String endDate = date + "-12-31";
                irsRateDao.insertIrsRate(startDate, endDate, rate);
            }
            else {
                List<String> dates = parseDate(date);
                String startDate = dates.get(0);
                String endDate = dates.get(1);
                irsRateDao.insertIrsRate(startDate, endDate, rate);
            }
        }
    }

    /**
     *
     * @param date String scraped from IRS website
     * @return List of LocalDates with start date and then end date
     */
    private List<String> parseDate(String date) {
        int n = date.length();
        String year = date.substring(n-4);

        int i = date.indexOf('-');
        String startMonth = dateNameToNumber(date.substring(0, i).replaceAll("[\\d.]", ""));

        String startDay = date.substring(0, i).replaceAll("[^\\d.]", "").replaceAll("\\.", "");
        if (startDay.length() != 2) {
            startDay = "0" + startDay;
        }

        int comma = date.indexOf(",");
        String endMonth = dateNameToNumber(date.substring(i+1, comma).replaceAll("[\\d.]", ""));

        String endDay = date.substring(i+1, comma).replaceAll("[^\\d.]", "").replaceAll("\\.", "");
        if (endDay.length() != 2) {
            endDay = "0" + endDay;
        }

        String start = year + "-" + startMonth + "-" + startDay;
        String end = year + "-" + endMonth + "-" + endDay;

        List<String> dateList = new ArrayList<>();
        dateList.add(start);
        dateList.add(end);

        return dateList;
    }

    private static String dateNameToNumber (String month) {
        month = month.replaceAll("\\s", "");
        switch (month) {
            case "Jan": return "01";
            case "Feb": return "02";
            case "Mar": return "03";
            case "Apr": return "04";
            case "May": return "05";
            case "June": return "06";
            case "July": return "07";
            case "Aug": return "08";
            case "Sep": return "09";
            case "Oct": return "10";
            case "Nov": return "11";
            case "Dec": return "12";
            default: return "00";
        }
    }

    public double webScrapeIrsRate() throws IOException {
        Document doc = Jsoup.connect(irsLink).get();
        String rate = doc.select("article table tbody tr td").get(1).text();
        return Double.parseDouble(rate);
    }

    @Scheduled(cron = "${scheduler.travel.scrape.cron}")
    public void scrapeAndUpdate() {
        double webVal = -1;
        try {
            webVal = webScrapeIrsRate();
        } catch (IOException e) {
            //do nothing
        }
        double dbVal = irsRateDao.getIrsRate(LocalDate.now());
    }
}
