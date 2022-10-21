package gov.nysenate.ess.core.controller.api;

import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ErrorResponse;
import gov.nysenate.ess.core.client.view.pec.video.PECVideoCodeSubmission;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.pec.task.detail.EthicsLiveCourseTaskDetailDao;
import gov.nysenate.ess.core.dao.pec.task.detail.VideoTaskDetailDao;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.auth.CorePermissionObject;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.ethics.EthicsLiveCourseTask;
import gov.nysenate.ess.core.model.pec.video.IncorrectPECVideoCodeEx;
import gov.nysenate.ess.core.model.pec.video.VideoTask;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskNotFoundEx;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import gov.nysenate.ess.core.util.ShiroUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.ETHICS_LIVE_COURSE;
import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.VIDEO_CODE_ENTRY;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/personnel/task/")
public class PECCodeApiCtrl extends BaseRestApiCtrl {

    private final PersonnelTaskService personnelTaskService;
    private final PersonnelTaskAssignmentDao assignedTaskDao;

    private final VideoTaskDetailDao videoTaskDetailDao;

    private final EthicsLiveCourseTaskDetailDao ethicsLiveCourseTaskDetailDao;

    public PECCodeApiCtrl(PersonnelTaskService personnelTaskService,
                          PersonnelTaskAssignmentDao assignedTaskDao,
                          VideoTaskDetailDao videoTaskDetailDao,
                          EthicsLiveCourseTaskDetailDao ethicsLiveCourseTaskDetailDao) {
        this.personnelTaskService = personnelTaskService;
        this.assignedTaskDao = assignedTaskDao;
        this.videoTaskDetailDao = videoTaskDetailDao;
        this.ethicsLiveCourseTaskDetailDao = ethicsLiveCourseTaskDetailDao;
    }

    /**
     * Ethics Live Course Code Submission API
     * --------------------------------------
     *
     * Submit codes to indicate that an employee watched a video.
     *
     * Usage:
     * (POST)    /api/v1/personnel/task/ethics/live/code
     *
     * Request body:
     * @param submission {@link PECVideoCodeSubmission}
     *
     * @return {@link SimpleResponse} if successful
     */
    @RequestMapping(value = "/ethics/live/code", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public SimpleResponse submitEthicsLiveCourseCodes(@RequestBody PECVideoCodeSubmission submission) {
        checkPermission(new CorePermission(submission.getEmpId(), CorePermissionObject.PERSONNEL_TASK, POST));

        ensureEmpIdExists(submission.getEmpId(), "empId");

        EthicsLiveCourseTask ethicsLiveCourseTask = (EthicsLiveCourseTask) getEthicsLiveCourseFromIdParams(submission.getTaskId(), "taskId");

        validateCodeFormat(submission.getCodes(), ethicsLiveCourseTask, "codes");

        ethicsLiveCourseTask.verifyCodes(submission.getCodes());
        int authenticatedEmpId = ShiroUtils.getAuthenticatedEmpId();
        assignedTaskDao.setTaskComplete(submission.getEmpId(), ethicsLiveCourseTask.getTaskId(), authenticatedEmpId);
        return new SimpleResponse(true, "codes submitted successfully", "code-submission-success");
    }

    /**
     * Video Code Submission API
     * -------------------------
     *
     * Submit codes to indicate that an employee watched a video.
     *
     * Usage:
     * (POST)    /api/v1/personnel/task/video/code
     *
     * Request body:
     * @param submission {@link PECVideoCodeSubmission}
     *
     * @return {@link SimpleResponse} if successful
     */
    @RequestMapping(value = "/video/code", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public SimpleResponse submitVideoCodes(@RequestBody PECVideoCodeSubmission submission) {
        checkPermission(new CorePermission(submission.getEmpId(), CorePermissionObject.PERSONNEL_TASK, POST));

        ensureEmpIdExists(submission.getEmpId(), "empId");

        VideoTask videoTask = (VideoTask) getVideoFromIdParams(submission.getTaskId(), "taskId");

        validateCodeFormat(submission.getCodes(), videoTask, "codes");

        videoTask.verifyCodes(submission.getCodes());
        int authenticatedEmpId = ShiroUtils.getAuthenticatedEmpId();
        assignedTaskDao.setTaskComplete(submission.getEmpId(), videoTask.getTaskId(), authenticatedEmpId);
        return new SimpleResponse(true, "codes submitted successfully", "code-submission-success");
    }

    /**
     * Handles submission of incorrect codes by returning a special error response.
     *
     * @param ex {@link IncorrectPECVideoCodeEx}
     * @return {@link ErrorResponse}
     */
    @ExceptionHandler(IncorrectPECVideoCodeEx.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleIncorrectCode(IncorrectPECVideoCodeEx ex) {
        return new ErrorResponse(ErrorCode.INVALID_PEC_CODE);
    }

    private VideoTask getVideoFromIdParams(int taskId, String paramName) {
        // Exception that may be thrown for multiple reasons
        InvalidRequestParamEx invalidTaskIdEx = new InvalidRequestParamEx(
                taskId,
                paramName,
                "int",
                "Task id must correspond to active Video code entry task."
        );
        try {
            PersonnelTask task = personnelTaskService.getPersonnelTask(taskId);

            if (!(task.getTaskType() == VIDEO_CODE_ENTRY)) {
                throw invalidTaskIdEx;
            }

            return videoTaskDetailDao.getTaskDetails(task);
        } catch (PersonnelTaskNotFoundEx ex) {
            throw invalidTaskIdEx;
        }
    }


    private void validateCodeFormat(List<String> codeSubmission, VideoTask video, String codeParamName) {
        if (codeSubmission == null || codeSubmission.size() != video.getCodes().size()) {
            throw new InvalidRequestParamEx(
                    codeSubmission, codeParamName, "string list",
                    "Submitted code list must have the same number of codes as video specification."
            );
        }
    }

    private EthicsLiveCourseTask getEthicsLiveCourseFromIdParams(int taskId, String paramName) {
        // Exception that may be thrown for multiple reasons
        InvalidRequestParamEx invalidTaskIdEx = new InvalidRequestParamEx(
                taskId,
                paramName,
                "int",
                "Task id must correspond to active Video code entry task."
        );
        try {
            PersonnelTask task = personnelTaskService.getPersonnelTask(taskId);

            if (!(task.getTaskType() == ETHICS_LIVE_COURSE)) {
                throw invalidTaskIdEx;
            }

            return ethicsLiveCourseTaskDetailDao.getTaskDetails(task);
        } catch (PersonnelTaskNotFoundEx ex) {
            throw invalidTaskIdEx;
        }
    }

    private void validateCodeFormat(List<String> codeSubmission, EthicsLiveCourseTask ethicsLiveCourseTask, String codeParamName) {
        if (codeSubmission == null || codeSubmission.size() != ethicsLiveCourseTask.getCodes().size()) {
            throw new InvalidRequestParamEx(
                    codeSubmission, codeParamName, "string list",
                    "Submitted code list must have the same number of codes as video specification."
            );
        }
    }
}
