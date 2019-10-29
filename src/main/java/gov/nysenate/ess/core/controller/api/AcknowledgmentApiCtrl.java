package gov.nysenate.ess.core.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.ess.core.client.view.pec.acknowledgment.AckDocView;
import gov.nysenate.ess.core.client.view.pec.acknowledgment.DetailedAckDocView;
import gov.nysenate.ess.core.dao.acknowledgment.AckDocDao;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentNotFoundEx;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.auth.CorePermissionObject;
import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.pec.acknowledgment.AckDoc;
import gov.nysenate.ess.core.model.pec.acknowledgment.AckDocNotFoundEx;
import gov.nysenate.ess.core.model.pec.acknowledgment.DuplicateAckEx;
import gov.nysenate.ess.core.model.pec.acknowledgment.EmpAckReport;
import gov.nysenate.ess.core.service.acknowledgment.AcknowledgmentReportService;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskNotFoundEx;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.core.util.ShiroUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/personnel/task/acknowledgment")
public class AcknowledgmentApiCtrl extends BaseRestApiCtrl {


    /** The directory where ack docs are stored in the file system */
    private final String ackDocDir;

    /** The uri path where ack docs are requested */
    private final String ackDocResPath;

    private final PersonnelTaskService taskService;
    private final AckDocDao ackDocDao;
    private final AcknowledgmentReportService ackReportService;
    private final PersonnelTaskAssignmentDao assignmentDao;

    @Autowired
    public AcknowledgmentApiCtrl(PersonnelTaskService taskService,
                                 AckDocDao ackDocDao,
                                 AcknowledgmentReportService ackReportService,
                                 PersonnelTaskAssignmentDao assignmentDao,
                                 @Value("${data.dir}") String dataDir,
                                 @Value("${data.ackdoc_subdir}") String ackDocSubdir,
                                 @Value("${resource.path}") String resPath
    ) {
        this.taskService = taskService;
        this.ackDocDao = ackDocDao;
        this.ackReportService = ackReportService;
        this.assignmentDao = assignmentDao;
        this.ackDocDir = dataDir + ackDocSubdir;
        this.ackDocResPath = resPath + ackDocSubdir;
    }

    /**
     * Active Acknowledged Document List API
     * -------------------------------------
     *
     * Returns a list of all active ack docs
     *
     * Usage:
     * (GET)    /api/v1/personnel/task/acknowledgment/documents
     *
     * @return ListViewResponse<AckDocView>
     */
    @RequestMapping(value = "/documents", method = {GET, HEAD})
    public ListViewResponse<AckDocView> getActiveAckDocs() {

        List<AckDoc> ackDocs = ackDocDao.getAckDocs(true);

        List<AckDocView> ackDocViews = ackDocs.stream()
                .map(ackDoc -> new AckDocView(ackDoc, ackDocResPath))
                .collect(toList());

        return ListViewResponse.of(ackDocViews, "documents");
    }

    /**
     * Ack. Docs For Year API
     * ----------------------
     *
     * Returns all ack docs in a single calendar year regardless of active status.
     *
     * Usage:
     * (GET)    /api/v1/personnel/task/acknowledgment/documents
     *
     * Request Params
     * @param year int - the year for which all ack docs will be retrieved
     *
     * @return ListViewResponse<AckDocView>
     * */
    @RequestMapping(value="/documents", params = {"year"})
    public ListViewResponse<AckDocView> getAckDocsForYear(@RequestParam int year) {
        List<AckDoc> ackDocs = ackDocDao.getAckDocsForYear(year);
        List<AckDocView> ackDocViews = ackDocs.stream()
                .map(ackDoc -> new AckDocView(ackDoc, ackDocResPath))
                .collect(toList());
        return ListViewResponse.of(ackDocViews, "documents");
    }

    /**
     * Acknowledged Document Active Years
     * ----------------------------------
     *
     * Returns a list of all years that contain an ack doc regardless of its active status.
     *
     * Usage:
     * (GET)    /api/v1/personnel/task/acknowledgment/documents/years
     *
     *
     * @return String
     * */
    @RequestMapping(value = "/documents/years", method = {GET, HEAD})
    public ListViewResponse<Integer> getAllYearsContainingAckDocs() {
        List<Integer> allYearsContainingAckDocs = ackDocDao.getAllYearsContainingAckDocs();
        return ListViewResponse.ofIntList(allYearsContainingAckDocs, "ackDocYears");
    }

    /**
     * Acknowledged Document Api
     * -------------------------
     *
     * Returns a specific Ack doc by its ID
     *
     * Usage:
     * (GET)    /api/v1/personnel/task/acknowledgment/documents/{ackDocId}
     *
     * Path Variables:
     * @param ackDocId int - the id of the ack doc that will be retrieved
     *
     * @return ViewObjectResponse<AckDocView>
     */
    @RequestMapping(value = "/documents/{ackDocId:\\d+}", method = {GET, HEAD})
    public ViewObjectResponse<AckDocView> getAckDoc(@PathVariable int ackDocId) throws IOException {
        //check id exists
        AckDoc ackDoc = getAckDoc(ackDocId, "ackDocId");
        DetailedAckDocView detailedAckDocView = new DetailedAckDocView(ackDoc, ackDocResPath, ackDocDir);
        return new ViewObjectResponse<>(detailedAckDocView, "document");
    }

    /**
     * Acknowledge Api
     * ---------------
     *
     * Records an acknowledgment from an employee
     *
     * Usage:
     * (POST)    /api/v1/personnel/task/acknowledgment
     *
     * RequestParams
     * @param empId int - the employee id of the employee completeing an acknowledgement
     * @param taskId int - the ack doc id that the employee is acknowledging
     *
     * @return SimpleResponse
     * */
    @RequestMapping(value = "", method = POST)
    public SimpleResponse acknowledgeDocument(@RequestParam int empId,
                                              @RequestParam int taskId) {
        checkPermission(new CorePermission(empId, CorePermissionObject.PERSONNEL_TASK, POST));

        int authedEmpId = ShiroUtils.getAuthenticatedEmpId();

        //check id if exists. Will throw an AckNotFoundEx if the document does not exist
        getAckDoc(taskId, "taskId");
        // Make sure empId is valid
        ensureEmpIdExists(empId, "empId");

        // Make sure this document hasn't already been ack'd.  If so, throw an error.
        try {
            PersonnelTaskAssignment assignment = assignmentDao.getTaskForEmp(empId, taskId);
            if (assignment.isCompleted()) {
                throw new DuplicateAckEx(taskId);
            }
        } catch (PersonnelTaskAssignmentNotFoundEx ignored) {}

        // Mark the acknowledgment task as completed
        assignmentDao.setTaskComplete(empId, taskId, authedEmpId);

        return new SimpleResponse(true, "Document Acknowledged", "document-acknowledged");
    }

    /**
     * AckDoc Report Api
     * -----------------
     *
     * Generates a csv file containing all active employees acknowledgment status on a particular ackdoc
     * This is the 1st report requested by Personnel
     *
     * Usage:
     * (GET)    /api/v1/personnel/task/acknowledgment/report/complete/{ackDocId}
     *
     * PathParams
     * @param ackDocId int - the ack doc id that the report is based off of
     * */
    @RequestMapping(value = "/report/complete/{ackDocId}", method = {GET, HEAD})
    public void getCompleteReportForAckDoc(@PathVariable int ackDocId,
                                           HttpServletResponse response) throws IOException {

        checkPermission(SimpleEssPermission.COMPLIANCE_REPORT_GENERATION.getPermission());
        AckDoc ackDoc = ackDocDao.getAckDoc(ackDocId);
        String csvFileName = ackDoc.getTitle()+"_"+ "SenateAckReport_" + LocalDateTime.now().withNano(0)+".csv";

        response.setContentType("text/csv");
        response.setStatus(200);

        // creates mock data
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                csvFileName);
        response.setHeader(headerKey, headerValue);

        CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT
                .withHeader("EmpId", "Name", "Email", "Resp Center", "Continuous Service", "Document Title",
                        "Document Effective Date Time", "Acknowledgment"));

        ObjectMapper mapper = OutputUtils.jsonMapper;
        for (EmpAckReport empAckReport: ackReportService.getAllAcksForAckDocById(ackDocId)) {
            LocalDateTime ackedTime = null;
            if (empAckReport.getAcks().get(0).getAck() != null) {
                ackedTime = empAckReport.getAcks().get(0).getAck().getTimestamp().withNano(0);
            }
            String respCenter = "";
            try {
                respCenter = empAckReport.getEmployee().getRespCenter().getHead().getShortName();
            }
            catch (Exception e) {
                //No need to do anything. This means that the employee does not have a responsibility center
            }

            csvPrinter.printRecord(
                    empAckReport.getEmployee().getEmployeeId(),
                    empAckReport.getEmployee().getFirstName() + " " + empAckReport.getEmployee().getLastName(),
                    empAckReport.getEmployee().getEmail(),
                    respCenter,
                    empAckReport.getEmployee().getSenateContServiceDate(),
                    empAckReport.getAcks().get(0).getAckDoc().getTitle(),
                    empAckReport.getAcks().get(0).getAckDoc().getEffectiveDateTime().withNano(0),
                    ackedTime);

        }
        csvPrinter.close();
    }

    /**
     * Employee Acknowledgment Report
     * ------------------------------
     *
     * Returns a json string of all acknowledgments the employee has acknowledged.
     * This is the 2nd report quested by Personnel
     *
     * Usage:
     * (GET)    /api/v1/personnel/task/acknowledgment/report/acks/emp
     *
     * RequestParams
     * @param empId int - the employee id of the employee whose acknowledgments will be retrieved
     *
     *
     * @return {@link EmpAckReport}
     */
    @RequestMapping(value = "/report/acks/emp", method = {GET, HEAD})
    public EmpAckReport getAllAcksFromEmployee(@RequestParam int empId) {
        checkPermission(SimpleEssPermission.COMPLIANCE_REPORT_GENERATION.getPermission());
        return ackReportService.getAllAcksFromEmployee(empId);
    }

    /* --- Exception Handlers --- */

    @ExceptionHandler(AckDocNotFoundEx.class)
    @ResponseStatus(value = NOT_FOUND)
    @ResponseBody
    public ViewObjectErrorResponse handleAckDocNotFoundEx(PersonnelTaskNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.ACK_DOC_NOT_FOUND, ex.getTaskId());
    }

    @ExceptionHandler(DuplicateAckEx.class)
    @ResponseStatus(value = BAD_REQUEST)
    @ResponseBody
    public ViewObjectErrorResponse handleDuplicateAckEx(DuplicateAckEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.DUPLICATE_ACK, ex.getAckDocid());
    }

    /* --- Internal Methods --- */

    private AckDoc getAckDoc(int taskId, String paramName) {
        PersonnelTask task = taskService.getPersonnelTask(taskId);
        if (!(task instanceof AckDoc)) {
            throw new InvalidRequestParamEx(taskId, paramName, "int",
                    "id must reference a personnel task of type ack doc");
        }
        return (AckDoc) task;
    }

}
