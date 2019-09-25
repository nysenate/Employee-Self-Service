package gov.nysenate.ess.time.service.attendance;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import gov.nysenate.ess.core.service.personnel.EssCachedEmployeeInfoService;
import gov.nysenate.ess.time.dao.attendance.SqlTimeOffRequestDao;
import gov.nysenate.ess.time.model.attendance.TimeOffRequest;
import gov.nysenate.ess.time.model.attendance.TimeOffRequestComment;
import gov.nysenate.ess.time.model.attendance.TimeOffRequestNotFoundException;
import gov.nysenate.ess.time.model.attendance.TimeOffStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EssTimeOffRequestService implements TimeOffRequestService {

    @Autowired protected SqlTimeOffRequestDao sqlTimeOffRequestDao;
    @Autowired protected EssCachedEmployeeInfoService essCachedEmployeeInfoService;

    /**
     * {{@inheritDoc}}
     */
    @Override
    public TimeOffRequest getTimeOffRequest(int requestId) throws TimeOffRequestNotFoundException {
        return sqlTimeOffRequestDao.getRequestById(requestId);
    }

    /**
     * {{@inheritDoc}}
     */
    @Override
    public List<TimeOffRequest> getAllRequestForEmpDateRange(int empId, Range<LocalDate> dateRange) {
        return sqlTimeOffRequestDao.getAllRequestsByEmpId(empId, dateRange);

    }

    /**
     * {{@inheritDoc}}
     */
    @Override
    public List<TimeOffRequest> getRequestsNeedingApproval(int supId) {
        List<TimeOffRequest> requests = sqlTimeOffRequestDao.getRequestsNeedingApproval(supId);
        requests.removeIf(request -> (request.getStatus()!=TimeOffStatus.SUBMITTED));
        return requests;
    }

    /**
     * {{@inheritDoc}}
     */
    @Override
    public List<TimeOffRequest> getActiveRequestsForSup(int supId) {
        List<TimeOffRequest> requests = sqlTimeOffRequestDao.getAllRequestsBySupId(supId);
        requests.removeIf(request -> !isActive(request));
        return requests;
    }

    /**
     * {{@inheritDoc}}
     */
    @Override
    public int updateRequest(TimeOffRequest request) {
        //update the timestamp of the request and of any comments with the request
        LocalDateTime now = LocalDateTime.now();
        request.setTimestamp(now);
        if(request.getComments() != null) {
            for (TimeOffRequestComment comment : request.getComments()) {
                if(comment.getTimestamp() == null) {
                    comment.setTimestamp(now);
                }
            }
        }

        return sqlTimeOffRequestDao.updateRequest(request);
    }

    /* **** PRIVATE HELPER FUNCTIONS **** */

    /**
     * Helper function to determine whether a request is active
     * @param request TimeOffRequest
     * @return boolean true if the request is active, false otherwise
     */
    private boolean isActive(TimeOffRequest request) {
        RangeSet<LocalDate> activeRange = getActiveRange(request.getEmployeeId());

        //check that the end date hasn't happened yet
        LocalDate endDate = request.getEndDate();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        boolean dateIsInFuture = endDate.isAfter(yesterday);

        //check that the end date and start date are within the employees active period
        LocalDate startDate = request.getStartDate();
        boolean duringActiveTime = activeRange.contains(endDate) && activeRange.contains(startDate);

        //check that the request has been approved
        boolean approved = (request.getStatus() == TimeOffStatus.APPROVED);

        return dateIsInFuture && duringActiveTime && approved;
    }

    /**
     * Helper function to get an employees active work range
     * @param empId int
     * @return RangeSet<LocalDate> The range of dates an employee is active
     */
    private RangeSet<LocalDate> getActiveRange(int empId) {
        return essCachedEmployeeInfoService.getEmployeeActiveDatesService(empId);
    }
}
