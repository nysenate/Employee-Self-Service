package gov.nysenate.ess.travel.api.application.statistics;

import java.util.List;

public class TravelApplicationStatistics {

    private Long totalExpenses;
    private List<TravelStatusCountDTO> appStatuses;

    public TravelApplicationStatistics(Long totalExpenses, List<TravelStatusCountDTO> appStatuses) {
        this.totalExpenses = totalExpenses;
        this.appStatuses = appStatuses;
    }

    @Override
    public String toString() {
        return "TravelApplicationStatistics{" +
                "totalExpenses=" + totalExpenses +
                ", appStatuses=" + appStatuses +
                '}';
    }

    public Long getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(Long totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public List<TravelStatusCountDTO> getAppStatuses() {
        return appStatuses;
    }

    public void setAppStatuses(List<TravelStatusCountDTO> appStatuses) {
        this.appStatuses = appStatuses;
    }
}
