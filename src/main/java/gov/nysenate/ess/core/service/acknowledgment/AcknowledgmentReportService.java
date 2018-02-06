package gov.nysenate.ess.core.service.acknowledgment;

import gov.nysenate.ess.core.dao.acknowledgment.SqlAckDocDao;
import gov.nysenate.ess.core.model.acknowledgment.AckDoc;
import gov.nysenate.ess.core.model.acknowledgment.Acknowledgment;
import gov.nysenate.ess.core.model.acknowledgment.EmpAckReport;
import gov.nysenate.ess.core.model.acknowledgment.ReportAck;
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
    public EmpAckReport getAllAcksFromEmployee(int empId) {
        Set<Employee> employees = employeeInfoService.getAllEmployees(true);

        EmpAckReport finalEmpAckReport = new EmpAckReport();

        List<Acknowledgment> allEmpAcks = sqlAckDocDao.getAllAcknowledgmentsForEmp(empId);
        List<AckDoc> allAckDocs = sqlAckDocDao.getAllAckDocs();
        List<ReportAck> reportAcks = new ArrayList<ReportAck>();

        for (Employee emp : employees) {
            if (emp.getEmployeeId() == empId) {
                finalEmpAckReport.setEmpId(emp.getEmployeeId());
                finalEmpAckReport.setFirstName(emp.getFirstName());
                finalEmpAckReport.setLastName(emp.getLastName());
                finalEmpAckReport.setEmail(emp.getEmail());
                finalEmpAckReport.setWorkLocation(emp.getWorkLocation());
            }
        }

        for (AckDoc ackDoc: allAckDocs) {
            reportAcks.add(new ReportAck(empId,ackDoc.getId(),null,ackDoc.getTitle(),ackDoc.getEffectiveDateTime()));
        }

        for (ReportAck reportAck: reportAcks) {
            for (Acknowledgment ack: allEmpAcks) {
                if (ack.getAckDocId() == reportAck.getAckDocId()) {
                    reportAck.setAckTimestamp(ack.getTimestamp());
                }
            }
        }

        finalEmpAckReport.setAcks(reportAcks);

        return finalEmpAckReport;
    }

    /** {@inheritDoc} */
    @Override
    public ArrayList<EmpAckReport> getAllAcksForAckDocById(int ackDocId) {
        Set<Employee> employees = employeeInfoService.getAllEmployees(true);
        ArrayList<EmpAckReport> completeAckReportList = new ArrayList<>();

        List<ReportAck> empsWhoHaveAckedSpecificDoc = sqlAckDocDao.getAllAcksForAckDocById(ackDocId);

        for (Employee emp : employees) {
            EmpAckReport finalEmpAckReport = new EmpAckReport(emp.getEmployeeId(), emp.getFirstName(),emp.getLastName(),
                    emp.getEmail(),emp.getWorkLocation());
            ReportAck reportAck = determineEmpAck(empsWhoHaveAckedSpecificDoc, emp.getEmployeeId());
            finalEmpAckReport.getAcks().add(reportAck);
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
    private void mergeAcksIntoFinalReport(EmpAckReport finalReport,List<ReportAck> ackedReports) {
        finalReport.setAcks(ackedReports);
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
    private ReportAck determineEmpAck(List<ReportAck> allAckReports, int empId) {
        ReportAck reportAck = new ReportAck();
        Iterator it = allAckReports.iterator();
        while(it.hasNext()) {
            ReportAck AckReportFromAll = (ReportAck) it.next();
            if (AckReportFromAll.getEmpId() == empId) {
                reportAck = AckReportFromAll;
                it.remove();
            }
        }
        return reportAck;
    }
}
