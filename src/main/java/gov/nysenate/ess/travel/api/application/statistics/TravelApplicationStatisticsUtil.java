package gov.nysenate.ess.travel.api.application.statistics;

import gov.nysenate.ess.travel.request.app.TravelApplication;
import gov.nysenate.ess.travel.utils.Dollars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TravelApplicationStatisticsUtil {

    public static List<TravelStatusCountDTO> getTravelStatusCount(List<TravelApplication> travelApplicationList){

        Map<String, StatusSummary> statusCountMap = new HashMap<>();

        for (TravelApplication travelApplication : travelApplicationList) {
            String status = travelApplication.getStatus().status().name();
            Dollars totalExpenses = travelApplication.totalAllowance();
            List<TravelApplication> travelApplications = new ArrayList<>();
            if (statusCountMap.containsKey(status)) {
                StatusSummary summary = statusCountMap.get(status);
                summary.count++;
                summary.travelApplications.add(travelApplication);
            } else {
                travelApplications.add(travelApplication);
                statusCountMap.put(status, new StatusSummary(1, totalExpenses.toString(), travelApplications));
            }
        }

        List<TravelStatusCountDTO> travelStatusCountDTOList = new ArrayList<>();
        for (Map.Entry<String, StatusSummary> entry : statusCountMap.entrySet()) {
            travelStatusCountDTOList.add(
                    new TravelStatusCountDTO(entry.getKey(),
                                                entry.getValue().count,
                                                entry.getValue().totalExpenses.toString(),
                                                entry.getValue().travelApplications));
        }
        return travelStatusCountDTOList;
    }

    private static class StatusSummary {
        int count;
        String totalExpenses;
        List<TravelApplication> travelApplications;

        public StatusSummary(int count, String totalExpenses, List<TravelApplication> travelApplications) {
            this.count = count;
            this.totalExpenses = totalExpenses;
            this.travelApplications = travelApplications;
        }
    }

}
