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
public class AcknowledgmentReportService implements EssAcknowledgmentReportService{

    @Autowired
    EmployeeInfoService employeeInfoService;

    @Autowired
    SqlAckDocDao sqlAckDocDao;

    private static final Logger logger = LoggerFactory.getLogger(AcknowledgmentReportService.class);

    public AcknowledgmentReportService() {}

    /** {@inheritDoc} */
    @Override
    public ArrayList<EmpAckReport> getAllAcksFromEmployees() {
        ArrayList<EmpAckReport> completeAckReportList = getAllEmployeesAcks();
        removeEmpsWithNoAcks(completeAckReportList);
        return completeAckReportList;
    }

    /*
    This corresponds to the 2nd report, in which . W
    We only care about employees that have acked at least one document
     */

    /**
     * All acks from all employees are reported, If an employee did not acknowledge a single document,
     * then they are excluded from this report
     * @return {@link ArrayList<EmpAckReport>}
     */
    private ArrayList<EmpAckReport> getAllEmployeesAcks() {
        Set<Employee> employees = employeeInfoService.getAllEmployees(true);

        ArrayList<EmpAckReport> completeAckReportList = new ArrayList<>();

        List<EmpAckReport> completeAckReports = sqlAckDocDao.getAllAcksForEmpWithTimestampAndDocRef();

        for (Employee emp : employees) {
            EmpAckReport finalEmpAckReport = new EmpAckReport(emp.getEmployeeId(), emp.getFirstName(),emp.getLastName(),
                    emp.getEmail(),emp.getWorkLocation());

            ArrayList<EmpAckReport> empAckReports = determineEmpAcks(completeAckReports, emp.getEmployeeId());

            mergeAcksIntoFinalReport(finalEmpAckReport, empAckReports);

            completeAckReportList.add(finalEmpAckReport);
        }
        return completeAckReportList;
    }

    /** {@inheritDoc} */
    @Override
    public ArrayList<EmpAckReport> getAllAcksForAckDocById(int ackDocId) {
        Set<Employee> employees = employeeInfoService.getAllEmployees(true);
        ArrayList<EmpAckReport> completeAckReportList = new ArrayList<>();

        List<EmpAckReport> empsWhoHaveAckedSpecificDoc = sqlAckDocDao.getAllAcksForAckDocById(ackDocId);

        for (Employee emp : employees) {
            EmpAckReport finalEmpAckReport = new EmpAckReport(emp.getEmployeeId(), emp.getFirstName(),emp.getLastName(),
                    emp.getEmail(),emp.getWorkLocation());

            EmpAckReport empAckReport = determineEmpAck(empsWhoHaveAckedSpecificDoc, emp.getEmployeeId());

            if (!empAckReport.getAckedTimeMap().isEmpty()) {
                finalEmpAckReport.getAckedTimeMap().putAll(empAckReport.getAckedTimeMap());
            }

            completeAckReportList.add(finalEmpAckReport);
        }

        return completeAckReportList;
    }

    /**
     * Merge ack reports for a specific employee into the final report for that employee
     *
     * {@link EmpAckReport}
     * @param finalReport - The final Report for a given employee
     * @param ackedReports - List of all acknowledgments for that employee
     */
    private void mergeAcksIntoFinalReport(EmpAckReport finalReport,List<EmpAckReport> ackedReports) {
        for (int i=0; i<ackedReports.size();i++) {
            finalReport.getAckedTimeMap().putAll(ackedReports.get(i).getAckedTimeMap());
        }
    }

    /**
     * Finds the employee acknowledgments amongst every other employees acknowledgment
     * This removes the ack once its found to improve efficiency
     *
     * {@link EmpAckReport}
     * @param allAckReports List of all EmpAckReports
     * @param empId The employee id whose EmpAckReports we want to find
     * @returnthe EmpAckReports of the empid that was passed in
     */
    private ArrayList<EmpAckReport> determineEmpAcks(List<EmpAckReport> allAckReports, int empId) {
        ArrayList<EmpAckReport> empAckReports = new ArrayList<>();
        Iterator it = allAckReports.iterator();
        while(it.hasNext()) {
            EmpAckReport empAckReport = (EmpAckReport) it.next();
            if (empAckReport.getEmpId() == empId) {
                empAckReports.add(empAckReport);
                it.remove();
            }
        }
        return empAckReports;
    }

    /**
     * Finds the employee acknowledgment amongst every other employees acknowledgment
     * This removes the ack once its found to improve efficiency
     *
     * {@link EmpAckReport}
     * @param allAckReports List of all EmpAckReports
     * @param empId The employee id whose EmpAckReport we want to find
     * @return the EmpAckReport of the empid that was passed in
     */
    private EmpAckReport determineEmpAck(List<EmpAckReport> allAckReports, int empId) {
        EmpAckReport empAckReport = new EmpAckReport();
        Iterator it = allAckReports.iterator();
        while(it.hasNext()) {
            EmpAckReport empAckReportFromAll = (EmpAckReport) it.next();
            if (empAckReportFromAll.getEmpId() == empId) {
                empAckReport = empAckReportFromAll;
                it.remove();
            }
        }
        return empAckReport;
    }

    /**
     * This method takes in a list of {@link List<EmpAckReport>}
     * and removes all employees with no acknowledgments
     *
     * @param completeReports List of all employee reports
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
