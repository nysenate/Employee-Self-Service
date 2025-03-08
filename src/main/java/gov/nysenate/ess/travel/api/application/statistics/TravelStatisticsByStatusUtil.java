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
public class TravelStatisticsByStatusUtil {

    private static EmployeeInfoService employeeInfoService;

    @Autowired
    public TravelStatisticsByStatusUtil(EmployeeInfoService employeeInfoService) {
        TravelStatisticsByStatusUtil.employeeInfoService = employeeInfoService;
    }

    public static List<TravelStatusStatisticsDTO> getTravelStatisticsByStatus(List<TravelApplicationView> travelApplicationList) {
        Map<String, SummaryByStatus> summaryByStatusMap = new HashMap<>();

        for (TravelApplicationView travelApplication : travelApplicationList) {
            String status = travelApplication.getStatus().getName();
            SummaryByStatus summaryByStatus;
            if (summaryByStatusMap.containsKey(status)) {
                summaryByStatus = summaryByStatusMap.get(status);
            } else {
                summaryByStatus = new SummaryByStatus(0, "0", new HashMap<>());
                summaryByStatusMap.put(status, summaryByStatus);
            }

            summaryByStatus.countOfApplications++;

            BigDecimal currentExpense = new BigDecimal(travelApplication.getActiveAmendment().getTotalAllowance());
            BigDecimal totalExpense = new BigDecimal(summaryByStatus.totalExpenses).add(currentExpense);
            summaryByStatus.totalExpenses = totalExpense.toString();

            updateEmployeeSummary(summaryByStatus.applicationEmployeeSummaryMap, travelApplication);
        }

        return buildTravelStatusStatisticsDTOList(summaryByStatusMap);
    }

    private static void updateEmployeeSummary(Map<Integer, ApplicationEmployeeSummary> employeeStatusSummaryMap, TravelApplicationView travelApplication) {
        int deptHeadEmpId = travelApplication.getTravelerDeptHeadEmpId();
        ApplicationEmployeeSummary applicationEmployeeSummary;
        if (employeeStatusSummaryMap.containsKey(deptHeadEmpId)) {
            applicationEmployeeSummary = employeeStatusSummaryMap.get(deptHeadEmpId);
        } else {
            Employee employee = employeeInfoService.getEmployee(deptHeadEmpId);
            EmployeeView employeeView = new EmployeeView(employee);
            applicationEmployeeSummary = new ApplicationEmployeeSummary(deptHeadEmpId, 0, "0", employeeView, new ArrayList<>());
            employeeStatusSummaryMap.put(deptHeadEmpId, applicationEmployeeSummary);
        }

        applicationEmployeeSummary.setCount(applicationEmployeeSummary.getCount() + 1);

        BigDecimal expense = new BigDecimal(travelApplication.getActiveAmendment().getTotalAllowance());
        BigDecimal totalExpense = new BigDecimal(applicationEmployeeSummary.getTotalExpenses()).add(expense);
        applicationEmployeeSummary.setTotalExpenses(totalExpense.toString());

        applicationEmployeeSummary.getTravelApplications().add(travelApplication);
    }

    private static List<TravelStatusStatisticsDTO> buildTravelStatusStatisticsDTOList(Map<String, SummaryByStatus> employeeMap) {
        List<TravelStatusStatisticsDTO> travelStatusStatisticsDTOList = new ArrayList<>();

        for (Map.Entry<String, SummaryByStatus> entry : employeeMap.entrySet()) {
            SummaryByStatus summary = entry.getValue();
            List<ApplicationEmployeeSummary> travelEmployeeSummaryList = new ArrayList<>();

            for (Map.Entry<Integer, ApplicationEmployeeSummary> statusEntry : summary.applicationEmployeeSummaryMap.entrySet()) {
                travelEmployeeSummaryList.add(statusEntry.getValue());
            }

            travelStatusStatisticsDTOList.add(new TravelStatusStatisticsDTO(
                    entry.getKey(),
                    summary.countOfApplications,
                    summary.totalExpenses,
                    travelEmployeeSummaryList
            ));
        }
        return travelStatusStatisticsDTOList;
    }

    public static TravelApplicationStatisticsByStatusView buildTravelApplicationStatisticsByStatusView(List<TravelStatusStatisticsView> appStatsViews) {

        int count = 0;
        double totalExpenses = 0.0;
        for (TravelStatusStatisticsView travelStatusStatisticsView : appStatsViews) {
            count += travelStatusStatisticsView.getCount();
            totalExpenses += Double.parseDouble(travelStatusStatisticsView.getTotalExpenses());
        }
        String roundedValue = String.format("%.2f", totalExpenses);
        return new TravelApplicationStatisticsByStatusView(count, roundedValue, appStatsViews);
    }

    private static class SummaryByStatus {
        int countOfApplications;
        String totalExpenses;
        Map<Integer, ApplicationEmployeeSummary> applicationEmployeeSummaryMap;

        public SummaryByStatus(int countOfApplications, String totalExpenses, Map<Integer, ApplicationEmployeeSummary> applicationEmployeeSummaryMap) {
            this.countOfApplications = countOfApplications;
            this.totalExpenses = totalExpenses;
            this.applicationEmployeeSummaryMap = applicationEmployeeSummaryMap;
        }
    }
}
