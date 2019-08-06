package gov.nysenate.ess.time.service.attendance;

import com.google.common.collect.RangeSet;
import gov.nysenate.ess.core.service.personnel.EssCachedEmployeeInfoService;
import gov.nysenate.ess.time.dao.attendance.SqlTimeOffRequestDao;
import gov.nysenate.ess.time.model.attendance.TimeOffRequest;
import gov.nysenate.ess.time.model.attendance.TimeOffRequestComment;
import gov.nysenate.ess.time.model.attendance.TimeOffRequestNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class EssTimeOffRequestService implements TimeOffRequestService {

    @Autowired protected SqlTimeOffRequestDao sqlTimeOffRequestDao;
    @Autowired protected EssCachedEmployeeInfoService essCachedEmployeeInfoService;

    @Override
    public TimeOffRequest getTimeOffRequest(int requestId) throws TimeOffRequestNotFoundException {
        return sqlTimeOffRequestDao.getRequestById(requestId);
    }

    @Override
    public List<TimeOffRequest> getActiveRequestsForEmp(int empId) {
        List<TimeOffRequest> requests = sqlTimeOffRequestDao.getAllRequestsByEmpId(empId);
        requests.removeIf(request -> !isActive(request));
        return requests;
    }

    @Override
    public List<TimeOffRequest> getRequestsNeedingApproval(int supId) {
        List<TimeOffRequest> requests = sqlTimeOffRequestDao.getRequestsNeedingApproval(supId);
        requests.removeIf(request -> !isActive(request));
        return requests;
    }

    @Override
    public List<TimeOffRequest> getRequests(int empId, int supId, int year) {
        return sqlTimeOffRequestDao.getAllRequestsBySupEmpYear(supId, empId, year);
    }

    @Override
    public List<TimeOffRequest> getActiveRequestsForSup(int supId) {
        List<TimeOffRequest> requests = sqlTimeOffRequestDao.getAllRequestsBySupId(supId);
        requests.removeIf(request -> !isActive(request));
        return requests;
    }

    @Override
    public boolean updateRequest(TimeOffRequest request) {
        //update an existing request, or add a new request
        boolean updated = false;

        //update the timestamp of the request and of any comments with the request
        Timestamp now = getCurrentTimestamp();
        request.setTimestamp(now);
        if(request.getComments() != null) {
            for (TimeOffRequestComment comment : request.getComments()) {
                comment.setTimestamp(now);
            }
        }

        // Check if the request exists
        if(request.getRequestId() !=  -1) {
           updated = sqlTimeOffRequestDao.updateRequest(request);
        } else {
            int requestId = sqlTimeOffRequestDao.addNewRequest(request);
            if(requestId > 0) {updated = true;}
        }
        return updated;
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
        Date endDate = request.getEndDate();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = calendar.getTime();
        boolean dateIsInFuture = endDate.after(yesterday);

        //check that the end date and start date are within the employees active period
        LocalDate end = convertDateToLocalDate(endDate);
        LocalDate start = convertDateToLocalDate(request.getStartDate());
        boolean duringActiveTime = activeRange.contains(end) && activeRange.contains(start);

        return dateIsInFuture && duringActiveTime;
    }

    /**
     * Helper function to get an employees active work range
     * @param empId int
     * @return RangeSet<LocalDate> The range of dates an employee is active
     */
    private RangeSet<LocalDate> getActiveRange(int empId) {
        return essCachedEmployeeInfoService.getEmployeeActiveDatesService(empId);
    }

    /**
     * Helper function to convert a Date object to a LocalDate object
     * @param date Date
     * @return LocalDate - the new LocalDate object
     * Source: https://www.baeldung.com/java-date-to-localdate-and-localdatetime
     */
    private LocalDate convertDateToLocalDate(Date date) {
        return LocalDate.parse(date.toString());
    }

    /**
     * Helper function to get the current timestamp
     * @return Timestamp
     */
    public Timestamp getCurrentTimestamp() {
        Date now = new Date();
        long currentTime = now.getTime();
        return new Timestamp(currentTime);
    }
}
