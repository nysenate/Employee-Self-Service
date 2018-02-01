package gov.nysenate.ess.core.service.acknowledgment;

import gov.nysenate.ess.core.dao.acknowledgment.SqlAckDocDao;
import gov.nysenate.ess.core.model.acknowledgment.EmpAckReport;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AcknowledgmentReportService {

    @Autowired
    EmployeeInfoService employeeInfoService;

    @Autowired
    SqlAckDocDao sqlAckDocDao;

    private static final Logger logger = LoggerFactory.getLogger(AcknowledgmentReportService.class);

    public AcknowledgmentReportService() {}

    /*
    Returns a list of all acknowledgments for each employee
     */
    public ArrayList<EmpAckReport> getAllAcksFromEmployees() {
        ArrayList<EmpAckReport> completeAckReportList = getAllEmployeesAcks();
        removeEmpsWithNoAcks(completeAckReportList);
        return completeAckReportList;
    }

    /*
    This corresponds to the 2nd report, in which all acks from all employees are reported. W
    We only care about employees that have acked at least one document
     */
    private ArrayList<EmpAckReport> getAllEmployeesAcks() {
        Set<Employee> employees = employeeInfoService.getAllEmployees(true);

        ArrayList<EmpAckReport> completeAckReportList = new ArrayList<>();

        for (Employee emp : employees) {
            List<EmpAckReport> empAckReports = sqlAckDocDao.getAllAcksForEmpWithTimestampAndDocRef(emp.getEmployeeId());

            EmpAckReport finalEmpAckReport = new EmpAckReport(emp.getEmployeeId(), emp.getFirstName(),emp.getLastName(),
                    emp.getEmail(),emp.getWorkLocation());

            mergeAcksIntoFinalReport(finalEmpAckReport, empAckReports);

            completeAckReportList.add(finalEmpAckReport);
        }
        return completeAckReportList;
    }

    /*
    Merge rows of ack reports for a specific employee into the final report for that employee
     */
    private void mergeAcksIntoFinalReport(EmpAckReport finalReport,List<EmpAckReport> ackedReports) {
        for (int i=0; i<ackedReports.size();i++) {
            finalReport.getAckedTimeMap().putAll(ackedReports.get(i).getAckedTimeMap());
        }
    }

    /*
    For the 2nd report, We only care about employees with Acks.
    And so this removes all employees who have not acked anything at all
     */
    private void removeEmpsWithNoAcks(List<EmpAckReport> completeReports) {
        Iterator it = completeReports.iterator();
        while(it.hasNext()) {
            EmpAckReport empAckReport = (EmpAckReport) it.next();
            if (empAckReport.getAckedTimeMap().isEmpty()) {
                it.remove();
            }
        }
    }
}
