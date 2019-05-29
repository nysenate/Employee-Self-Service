package gov.nysenate.ess.core.controller.api;

import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ErrorResponse;
import gov.nysenate.ess.core.client.view.pec.video.PECVideoCodeSubmission;
import gov.nysenate.ess.core.dao.pec.PersonnelAssignedTaskDao;
import gov.nysenate.ess.core.dao.pec.video.PECVideoDao;
import gov.nysenate.ess.core.dao.pec.video.PECVideoNotFoundEx;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.auth.CorePermissionObject;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.video.IncorrectPECVideoCodeEx;
import gov.nysenate.ess.core.model.pec.video.PECVideo;
import gov.nysenate.ess.core.util.ShiroUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/personnel/task/video")
public class PECVideoApiCtrl extends BaseRestApiCtrl {

    private final PECVideoDao videoDao;
    private final PersonnelAssignedTaskDao assignedTaskDao;

    public PECVideoApiCtrl(PECVideoDao videoDao,
                           PersonnelAssignedTaskDao assignedTaskDao) {
        this.videoDao = videoDao;
        this.assignedTaskDao = assignedTaskDao;
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
    @RequestMapping(value = "/code", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public SimpleResponse submitVideoCodes(@RequestBody PECVideoCodeSubmission submission) {
        checkPermission(new CorePermission(submission.getEmpId(), CorePermissionObject.PERSONNEL_TASK, POST));

        ensureEmpIdExists(submission.getEmpId(), "empId");

        PECVideo video = getVideoFromIdParams(submission.getVideoId(), "videoId");

        validateCodeFormat(submission.getCodes(), video, "codes");

        video.verifyCodes(submission.getCodes());
        int authenticatedEmpId = ShiroUtils.getAuthenticatedEmpId();
        PersonnelTaskId taskId = new PersonnelTaskId(PersonnelTaskType.VIDEO_CODE_ENTRY, submission.getVideoId());
        assignedTaskDao.setTaskComplete(submission.getEmpId(), taskId, authenticatedEmpId);
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
        return new ErrorResponse(ErrorCode.INVALID_PEC_VIDEO_CODE);
    }

    private PECVideo getVideoFromIdParams(int videoId, String paramName) {
        try {
            return videoDao.getVideo(videoId);
        } catch (PECVideoNotFoundEx ex) {
            throw new InvalidRequestParamEx(
                    videoId,
                    paramName,
                    "int",
                    "Video id must correspond to active PEC Video."
            );
        }
    }

    private void validateCodeFormat(List<String> codeSubmission, PECVideo video, String codeParamName) {
        if (codeSubmission == null || codeSubmission.size() != video.getCodes().size()) {
            throw new InvalidRequestParamEx(
                    codeSubmission, codeParamName, "string list",
                    "Submitted code list must have the same number of codes as video specification."
            );
        }
    }
}
