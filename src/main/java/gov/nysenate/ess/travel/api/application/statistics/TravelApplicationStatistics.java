package gov.nysenate.ess.travel.api.application.statistics;

import java.util.List;

public class TravelApplicationStatistics {

    private int count;
    private Long totalExpenses;
    private List<TravelStatusCountDTO> appStatuses;

    public TravelApplicationStatistics(int count, Long totalExpenses, List<TravelStatusCountDTO> appStatuses) {
        this.totalExpenses = totalExpenses;
        this.appStatuses = appStatuses;
        this.count = count;
    }

    @Override
    public String toString() {
        return "TravelApplicationStatistics{" +
                "count=" + count +
                ", totalExpenses=" + totalExpenses +
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
