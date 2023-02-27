package gov.nysenate.ess.core.controller.api;

import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentNotFoundEx;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.auth.CorePermissionObject;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.pec.acknowledgment.AckDoc;
import gov.nysenate.ess.core.model.pec.acknowledgment.AckDocNotFoundEx;
import gov.nysenate.ess.core.model.pec.acknowledgment.DuplicateAckEx;
import gov.nysenate.ess.core.service.pec.notification.PECNotificationService;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskNotFoundEx;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import gov.nysenate.ess.core.service.pec.task.TaskPDFSignatureService;
import gov.nysenate.ess.core.util.ShiroUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.DOCUMENT_ACKNOWLEDGMENT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/personnel/task/acknowledgment")
public class AcknowledgmentApiCtrl extends BaseRestApiCtrl {


    /** The directory where ack docs are stored in the file system */
    private final String ackDocDir;

    /** The uri path where ack docs are requested */
    private final String ackDocResPath;

    @Value("${data.dir}") String dataDir;
    @Value("${data.ackdoc_subdir}") String ackDocSubDir;

    private final PersonnelTaskService taskService;
    private final PersonnelTaskAssignmentDao assignmentDao;
    private final TaskPDFSignatureService signatureService;

    private final PECNotificationService pecNotificationService;

    @Autowired
    public AcknowledgmentApiCtrl(PersonnelTaskService taskService,
                                 PersonnelTaskAssignmentDao assignmentDao,
                                 TaskPDFSignatureService signatureService,
                                 PECNotificationService pecNotificationService,
                                 @Value("${data.dir}") String dataDir,
                                 @Value("${data.ackdoc_subdir}") String ackDocSubdir,
                                 @Value("${resource.path}") String resPath
    ) {
        this.taskService = taskService;
        this.assignmentDao = assignmentDao;
        this.signatureService = signatureService;
        this.pecNotificationService = pecNotificationService;
        this.ackDocDir = dataDir + ackDocSubdir;
        this.ackDocResPath = resPath + ackDocSubdir;
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
     * @param empId int - the employee id of the employee completing an acknowledgement
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
        pecNotificationService.sendCompletionEmail(empId, taskId);
        return new SimpleResponse(true, "Document Acknowledged", "document-acknowledged");
    }

    /**
     * Signed PDF download api
     *
     * Usage:
     *      * (GET)    /api/v1/personnel/task/acknowledgment/download/
     *
     * Employees and Personnel should be able to download a signed copy of their completed acknowledgments.
     *
     *
     * @return
     */
    @RequestMapping(value = "/download", method = GET)
    public ResponseEntity<Resource> downloadAcknowledgedDocument(HttpServletRequest request, @RequestParam int empId, @RequestParam int taskId) throws IOException {
        File signatureFile =  signatureService.createEmployeeSignatureForTask(empId,taskId);
        // Try to determine file's content type

        Resource resource = loadFileAsResource(signatureFile);

        String contentType = null;
        contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
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

    private boolean getAckDoc(int taskId, String paramName) {
        PersonnelTask task = taskService.getPersonnelTask(taskId);
        if (!(task.getTaskType() == DOCUMENT_ACKNOWLEDGMENT)) {
            throw new InvalidRequestParamEx(taskId, paramName, "int",
                    "id must reference a personnel task of type ack doc");
        }
        return true;
    }

    private Resource loadFileAsResource(File file) throws IOException {
        String fileName = file.getName();
        try {
            Path filePath = file.toPath().toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new IOException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new IOException("File not found " + fileName, ex);
        }
    }

}
