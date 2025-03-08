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
public class TravelStatisticsByEmployeeUtil {

    private static EmployeeInfoService employeeInfoService;

    @Autowired
    public TravelStatisticsByEmployeeUtil(EmployeeInfoService employeeInfoService) {
        TravelStatisticsByEmployeeUtil.employeeInfoService = employeeInfoService;
    }

    public static List<TravelEmployeeStatisticsDTO> getTravelStatisticsByEmployee(List<TravelApplicationView> travelApplicationList) {
        Map<Integer, SummaryByEmployee> summaryByEmployeeMap = new HashMap<>();

        for (TravelApplicationView travelApplication : travelApplicationList) {
            int deptHeadEmpId = travelApplication.getTravelerDeptHeadEmpId();
            SummaryByEmployee summaryByEmployee;
            if (summaryByEmployeeMap.containsKey(deptHeadEmpId)) {
                summaryByEmployee = summaryByEmployeeMap.get(deptHeadEmpId);
            } else {
                Employee employee = employeeInfoService.getEmployee(deptHeadEmpId);
                EmployeeView employeeView = new EmployeeView(employee);
                summaryByEmployee = new SummaryByEmployee(0, "0", employeeView, new HashMap<>());
                summaryByEmployeeMap.put(deptHeadEmpId, summaryByEmployee);
            }

            summaryByEmployee.countOfApplications++;

            BigDecimal currentExpense = new BigDecimal(travelApplication.getActiveAmendment().getTotalAllowance());
            BigDecimal totalExpense = new BigDecimal(summaryByEmployee.totalExpenses).add(currentExpense);
            summaryByEmployee.totalExpenses = totalExpense.toString();

            updateStatusSummary(summaryByEmployee.applicationStatusSummaryMap, travelApplication);
        }

        return buildTravelEmployeeStatisticsDTOList(summaryByEmployeeMap);
    }

    private static void updateStatusSummary(Map<String, ApplicationStatusSummary> statusSummaryMap, TravelApplicationView travelApplication) {
        String status = travelApplication.getStatus().getName();
        BigDecimal expense = new BigDecimal(travelApplication.getActiveAmendment().getTotalAllowance());

        ApplicationStatusSummary summary;
        if (statusSummaryMap.containsKey(status)) {
            summary = statusSummaryMap.get(status);
        } else {
            summary = new ApplicationStatusSummary(status, 0, "0", new ArrayList<>());
            statusSummaryMap.put(status, summary);
        }

        summary.setCount(summary.getCount() + 1);

        BigDecimal totalExpense = new BigDecimal(summary.getTotalExpenses()).add(expense);
        summary.setTotalExpenses(totalExpense.toString());

        summary.getTravelApplications().add(travelApplication);
    }

    private static List<TravelEmployeeStatisticsDTO> buildTravelEmployeeStatisticsDTOList(Map<Integer, SummaryByEmployee> summaryByEmployeeMap) {
        List<TravelEmployeeStatisticsDTO> travelEmployeeStatisticsDTOList = new ArrayList<>();

        for (Map.Entry<Integer, SummaryByEmployee> entry : summaryByEmployeeMap.entrySet()) {
            SummaryByEmployee summaryByEmployee = entry.getValue();
            List<ApplicationStatusSummary> travelStatusSummaryList = new ArrayList<>();

            for (Map.Entry<String, ApplicationStatusSummary> statusEntry : summaryByEmployee.applicationStatusSummaryMap.entrySet()) {
                travelStatusSummaryList.add(statusEntry.getValue());
            }

            travelEmployeeStatisticsDTOList.add(new TravelEmployeeStatisticsDTO(
                    entry.getKey(),
                    summaryByEmployee.employeeView,
                    summaryByEmployee.countOfApplications,
                    summaryByEmployee.totalExpenses,
                    travelStatusSummaryList
            ));
        }
        return travelEmployeeStatisticsDTOList;
    }

    public static TravelApplicationStatisticsByEmployeeView buildTravelApplicationStatisticsByEmployeeView(List<TravelEmployeeStatisticsView> appStatsViews) {

        int count = 0;
        double totalExpenses = 0.0;
        for (TravelEmployeeStatisticsView travelEmployeeStatisticsView : appStatsViews) {
            count += travelEmployeeStatisticsView.getCount();
            totalExpenses += Double.parseDouble(travelEmployeeStatisticsView.getTotalExpenses());
        }
        String roundedValue = String.format("%.2f", totalExpenses);
        return new TravelApplicationStatisticsByEmployeeView(count, roundedValue, appStatsViews);
    }

    private static class SummaryByEmployee {
        int countOfApplications;
        String totalExpenses;
        EmployeeView employeeView;
        Map<String, ApplicationStatusSummary> applicationStatusSummaryMap;

        public SummaryByEmployee(int countOfApplications, String totalExpenses, EmployeeView employeeView, Map<String, ApplicationStatusSummary> applicationStatusSummaryMap) {
            this.countOfApplications = countOfApplications;
            this.totalExpenses = totalExpenses;
            this.employeeView = employeeView;
            this.applicationStatusSummaryMap = applicationStatusSummaryMap;
        }
    }
}
