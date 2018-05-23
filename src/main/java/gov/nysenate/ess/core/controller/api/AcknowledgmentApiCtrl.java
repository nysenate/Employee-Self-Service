package gov.nysenate.ess.core.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.ess.core.client.view.acknowledgment.AckDocView;
import gov.nysenate.ess.core.client.view.acknowledgment.AcknowledgmentView;
import gov.nysenate.ess.core.client.view.acknowledgment.DetailedAckDocView;
import gov.nysenate.ess.core.dao.acknowledgment.AckDocDao;
import gov.nysenate.ess.core.model.acknowledgment.*;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.auth.CorePermissionObject;
import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import gov.nysenate.ess.core.service.acknowledgment.AcknowledgmentReportService;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.core.util.ShiroUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/acknowledgment")
public class AcknowledgmentApiCtrl extends BaseRestApiCtrl {


    /**
     * The directory where ack docs are stored in the file system
     */
    private String ackDocDir;
    /**
     * The uri path where ack docs are requested
     */
    private String ackDocResPath;

    private final AckDocDao ackDocDao;

    private final AcknowledgmentReportService ackReportService;

    @Autowired
    public AcknowledgmentApiCtrl(AckDocDao ackDocDao,
                                 AcknowledgmentReportService ackReportService,
                                 @Value("${data.dir}") String dataDir,
                                 @Value("${data.ackdoc_subdir}") String ackDocSubdir,
                                 @Value("${resource.path}") String resPath
    ) {
        this.ackDocDao = ackDocDao;
        this.ackReportService = ackReportService;
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
     * (GET)    /api/v1/acknowledgment/documents
     *
     * @return ListViewResponse<AckDocView>
     */
    @RequestMapping(value = "/documents", method = {GET, HEAD})
    public ListViewResponse<AckDocView> getActiveAckDocs() {

        List<AckDoc> ackDocs = ackDocDao.getActiveAckDocs();

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
     * (GET)    /api/v1/acknowledgment/documents
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
     * (GET)    /api/v1/acknowledgment/documents/years
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
     * (GET)    /api/v1/acknowledgment/documents/{ackDocId}
     *
     * Path Variables:
     * @param ackDocId int - the id of the ack doc that will be retrieved
     *
     * @return ViewObjectResponse<AckDocView>
     */
    @RequestMapping(value = "/documents/{ackDocId:\\d+}", method = {GET, HEAD})
    public ViewObjectResponse<AckDocView> getAckDoc(@PathVariable int ackDocId) throws IOException {
        //check id exists
        AckDoc ackDoc = ackDocDao.getAckDoc(ackDocId);
        DetailedAckDocView detailedAckDocView = new DetailedAckDocView(ackDoc, ackDocResPath, ackDocDir);
        return new ViewObjectResponse<>(detailedAckDocView, "document");
    }

    /**
     * Acknowledgment Api
     * ------------------
     *
     * Returns a list of all acknowledgments for a single employee
     *
     * Usage:
     * (GET)    /api/v1/acknowledgment/acks
     *
     * @return ListViewResponse<AcknowledgmentView>
     */
    @RequestMapping(value = "/acks", method = {GET, HEAD})
    public ListViewResponse<AcknowledgmentView> getAcknowledgments(@RequestParam int empId) {
        checkPermission(new CorePermission(empId, CorePermissionObject.ACKNOWLEDGMENT, GET));

        List<Acknowledgment> acknowledgments = ackDocDao.getAllAcknowledgmentsForEmp(empId);

        List<AcknowledgmentView> ackViews = acknowledgments.stream()
                .map(AcknowledgmentView::new)
                .collect(toList());

        return ListViewResponse.of(ackViews, "acknowledgments");
    }

    /**
     * Acknowledge Api
     * ---------------
     *
     * Records an acknowledgment from an employee to the database
     *
     * Usage:
     * (GET)    /api/v1/acknowledgment/acks
     *
     * RequestParams
     * @param empId int - the employee id of the employee completeing an acknowledgement
     * @param ackDocId int - the ack doc id that the employee is acknowledging
     *
     *
     * @return SimpleResponse
     * */
    @RequestMapping(value = "/acks", method = POST)
    public SimpleResponse acknowledgeDocument(@RequestParam int empId,
                                              @RequestParam int ackDocId) {
        checkPermission(new CorePermission(empId, CorePermissionObject.ACKNOWLEDGMENT, POST));

        int authedEmpId = ShiroUtils.getAuthenticatedEmpId();
        boolean personnelAcked = authedEmpId != empId;

        //check id if exists. Will throw an AckNotFoundEx if the document does not exist
        ackDocDao.getAckDoc(ackDocId);

        //cant ack same doc twice throw error
        List<Acknowledgment> acknowledgments = ackDocDao.getAllAcknowledgmentsForEmp(empId);

        for (Acknowledgment acknowledgment : acknowledgments) {
            if (acknowledgment.getAckDocId() == ackDocId && acknowledgment.getEmpId() == empId) {
                throw new DuplicateAckEx(ackDocId);
            }
        }

        // Save the ack doc
        ackDocDao.insertAcknowledgment(new Acknowledgment(empId, ackDocId, LocalDateTime.now(), personnelAcked));

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
     * (GET)    /api/v1/acknowledgment/report/complete/{ackDocId}
     *
     * PathParams
     * @param ackDocId int - the ack doc id that the report is based off of
     * */
    @RequestMapping(value = "/report/complete/{ackDocId}", method = {GET, HEAD})
    public void getCompleteReportForAckDoc(@PathVariable int ackDocId,
                                           HttpServletResponse response) throws IOException {

        checkPermission(SimpleEssPermission.ACK_REPORT_GENERATION.getPermission());
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
                .withHeader("EmpId", "Name", "Email", "Resp Center","Document Title",
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
     * (GET)    /api/v1/acknowledgment/report/acks/emp
     *
     * RequestParams
     * @param empId int - the employee id of the employee whose acknowledgments will be retrieved
     *
     *
     * @return {@link EmpAckReport}
     */
    @RequestMapping(value = "/report/acks/emp", method = {GET, HEAD})
    public EmpAckReport getAllAcksFromEmployee(@RequestParam int empId) {
        checkPermission(SimpleEssPermission.ACK_REPORT_GENERATION.getPermission());
        return ackReportService.getAllAcksFromEmployee(empId);
    }

    /* --- Exception Handlers --- */

    @ExceptionHandler(AckDocNotFoundEx.class)
    @ResponseStatus(value = NOT_FOUND)
    @ResponseBody
    public ViewObjectErrorResponse handleAckDocNotFoundEx(AckDocNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.ACK_DOC_NOT_FOUND, ex.getAckDocid());
    }

    @ExceptionHandler(DuplicateAckEx.class)
    @ResponseStatus(value = BAD_REQUEST)
    @ResponseBody
    public ViewObjectErrorResponse handleDuplicateAckEx(DuplicateAckEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.DUPLICATE_ACK, ex.getAckDocid());
    }

}
