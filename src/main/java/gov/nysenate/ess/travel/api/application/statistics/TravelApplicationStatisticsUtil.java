package gov.nysenate.ess.travel.api.application.statistics;

import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.api.application.TravelApplicationView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TravelApplicationStatisticsUtil {

    private static EmployeeInfoService employeeInfoService;

    @Autowired
    public TravelApplicationStatisticsUtil(EmployeeInfoService employeeInfoService) {
        TravelApplicationStatisticsUtil.employeeInfoService = employeeInfoService;
    }

    public static List<TravelStatusCountDTO> getTravelStatusCount(List<TravelApplicationView> travelApplicationList) {
        Map<Integer, EmployeeStatusSummary> employeeMap = new HashMap<>();

        for (TravelApplicationView travelApplication : travelApplicationList) {
            int deptHead = travelApplication.getTravelerDeptHeadEmpId();

            // Fetch or create EmployeeStatusSummary
            EmployeeStatusSummary summary;
            if (employeeMap.containsKey(deptHead)) {
                summary = employeeMap.get(deptHead);
            } else {
                Employee employee = employeeInfoService.getEmployee(deptHead);
                EmployeeView employeeView = new EmployeeView(employee);
                summary = new EmployeeStatusSummary(0, "0", employeeView, new HashMap<>());
                employeeMap.put(deptHead, summary);
            }

            summary.countOfApplications++;

            BigDecimal currentExpense = new BigDecimal(travelApplication.getActiveAmendment().getTotalAllowance());
            BigDecimal totalExpense = new BigDecimal(summary.totalExpenses).add(currentExpense);
            summary.totalExpenses = totalExpense.toString();

            updateStatusSummary(summary.statusCountMap, travelApplication);
        }

        return buildTravelStatusCountDTOList(employeeMap);
    }

    private static void updateStatusSummary(Map<String, StatusSummary> statusSummaryMap, TravelApplicationView travelApplication) {
        String status = travelApplication.getStatus().getName();
        BigDecimal expense = new BigDecimal(travelApplication.getActiveAmendment().getTotalAllowance());

        StatusSummary summary;
        if (statusSummaryMap.containsKey(status)) {
            summary = statusSummaryMap.get(status);
        } else {
            summary = new StatusSummary(status, 0, "0", new ArrayList<>());
            statusSummaryMap.put(status, summary);
        }

        summary.setCount(summary.getCount() + 1);

        BigDecimal totalExpense = new BigDecimal(summary.getTotalExpenses()).add(expense);
        summary.setTotalExpenses(totalExpense.toString());

        summary.getTravelApplications().add(travelApplication);
    }

    private static List<TravelStatusCountDTO> buildTravelStatusCountDTOList(Map<Integer, EmployeeStatusSummary> employeeMap) {
        List<TravelStatusCountDTO> travelStatusCountDTOList = new ArrayList<>();

        for (Map.Entry<Integer, EmployeeStatusSummary> entry : employeeMap.entrySet()) {
            EmployeeStatusSummary summary = entry.getValue();
            List<StatusSummary> travelStatusCountList = new ArrayList<>();

            for (Map.Entry<String, StatusSummary> statusEntry : summary.statusCountMap.entrySet()) {
                travelStatusCountList.add(statusEntry.getValue());
            }

            travelStatusCountDTOList.add(new TravelStatusCountDTO(
                                                        entry.getKey(),
                                                        summary.employeeView,
                                                        summary.countOfApplications,
                                                        summary.totalExpenses,
                                                        travelStatusCountList
            ));
        }
        return travelStatusCountDTOList;
    }

    private static class EmployeeStatusSummary {
        int countOfApplications;
        String totalExpenses;
        EmployeeView employeeView;
        Map<String, StatusSummary> statusCountMap;

        public EmployeeStatusSummary(int countOfApplications, String totalExpenses, EmployeeView employeeView, Map<String, StatusSummary> statusCountMap) {
            this.countOfApplications = countOfApplications;
            this.totalExpenses = totalExpenses;
            this.employeeView = employeeView;
            this.statusCountMap = statusCountMap;
        }
    }
}
