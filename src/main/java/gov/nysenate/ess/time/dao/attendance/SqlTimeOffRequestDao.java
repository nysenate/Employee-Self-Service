package gov.nysenate.ess.time.dao.attendance;

import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.time.dao.attendance.mapper.TimeOffRequestCommentRowMapper;
import gov.nysenate.ess.time.dao.attendance.mapper.TimeOffRequestDayRowMapper;
import gov.nysenate.ess.time.dao.attendance.mapper.TimeOffRequestRowMapper;
import gov.nysenate.ess.time.model.attendance.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class SqlTimeOffRequestDao extends SqlBaseDao implements TimeOffRequestDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlTimeOffRequestDao.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public TimeOffRequest getRequestById(int requestId) throws TimeOffRequestNotFoundException {
        TimeOffRequest result;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("requestId", requestId);
        //get the request
        try {
            result = localNamedJdbc.queryForObject(SqlTimeOffRequestQuery.SELECT_TIME_OFF_REQUEST_BY_REQUEST_ID.getSql(schemaMap()),
                    params, new TimeOffRequestRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            logger.warn("Retrieve time off request id {} error {}", requestId, ex.getMessage());
            throw new TimeOffRequestNotFoundException(requestId);
        }

        //get the days for the request
        List<TimeOffRequestDay> days = localNamedJdbc.query(SqlTimeOffRequestQuery.SELECT_DAYS_BY_REQUEST_ID.getSql(schemaMap()),
                params, new TimeOffRequestDayRowMapper());

        //get the comments for the request
        List<TimeOffRequestComment> comments = localNamedJdbc.query(SqlTimeOffRequestQuery.SELECT_COMMENTS_BY_REQUEST_ID.getSql(schemaMap()),
                params, new TimeOffRequestCommentRowMapper());

        result.setDays(days);
        result.setComments(comments);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TimeOffRequest> getAllRequestsByEmpId(int employeeId) {
        //get the request ids of all requests with the employeeID
        List<Integer> requestIds;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("employeeId", employeeId);
        requestIds = localNamedJdbc.query(SqlTimeOffRequestQuery.SELECT_TIME_OFF_REQUEST_IDS_BY_EMPLOYEE_ID.getSql(schemaMap()),
                params, (rs, rowNum) -> rs.getInt("request_id"));
        return getMultipleRequests(requestIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TimeOffRequest> getAllRequestsBySupId(int supervisorId) {
        //get the request ids of all requests with the supervisorID
        List<Integer> requestIds;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("supervisorId", supervisorId);
        requestIds = localNamedJdbc.query(SqlTimeOffRequestQuery.SELECT_TIME_OFF_REQUEST_IDS_BY_SUPERVISOR_ID.getSql(schemaMap()),
                params, (rs, rowNum) -> rs.getInt("request_id"));
        return getMultipleRequests(requestIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TimeOffRequest> getAllRequestsBySupEmpYear(int supervisorId, int employeeId, int year) {
        //get the request ids of all requests with the employeeID
        List<Integer> requestIds;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("supervisorId", supervisorId);
        params.addValue("employeeId", employeeId);
        params.addValue("year", year);
        requestIds = localNamedJdbc.query(SqlTimeOffRequestQuery.SELECT_TIME_OFF_REQUESTS_IDS_BY_EMP_SUP_YEAR.getSql(schemaMap()),
                params, (rs, rowNum) -> rs.getInt("request_id"));
        return getMultipleRequests(requestIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TimeOffRequest> getRequestsNeedingApproval(int supervisorId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("supervisorId", supervisorId);
        List<Integer> requestIds;
        requestIds = localNamedJdbc.query(SqlTimeOffRequestQuery.SELECT_TIME_OFF_REQUEST_IDS_NEEDING_APPROVAL_BY_SUP
                .getSql(schemaMap()), params, (rs, rowNum) -> rs.getInt("request_id"));
        return getMultipleRequests(requestIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCommentToRequest(TimeOffRequestComment comment) {
        MapSqlParameterSource params = getAddCommentParams(comment);
        localNamedJdbc.update(SqlTimeOffRequestQuery.ADD_COMMENT_TO_TIME_OFF_REQUEST.getSql(schemaMap()), params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllComments(int requestId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("requestId", requestId);
        localNamedJdbc.update(SqlTimeOffRequestQuery.REMOVE_ALL_COMMENTS_FOR_REQUEST.getSql(schemaMap()), params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addDayToRequest(TimeOffRequestDay day) {
        MapSqlParameterSource params = getAddDayParams(day);
        localNamedJdbc.update(SqlTimeOffRequestQuery.ADD_DAY_TO_TIME_OFF_REQUEST.getSql(schemaMap()), params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllDays(int requestId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("requestId", requestId);
        localNamedJdbc.update(SqlTimeOffRequestQuery.REMOVE_ALL_DAYS_FOR_REQUEST.getSql(schemaMap()), params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int addNewRequest(TimeOffRequest request) {
        MapSqlParameterSource params = getAddRequestParams(request);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String column = "request_id";
        String[] keyCols = {column};
        localNamedJdbc.update(SqlTimeOffRequestQuery.ADD_TIME_OFF_REQUEST.getSql(schemaMap()), params, keyHolder, keyCols);
        int requestId = (Integer) keyHolder.getKeys().get(column);

        //add each day in the request
        if (request.getComments() != null) {
            for (TimeOffRequestComment comment : request.getComments()) {
                comment.setRequestId(requestId);
                addCommentToRequest(comment);
            }
        }

        //add each comment in the request
        if (request.getDays() != null) {
            for (TimeOffRequestDay day : request.getDays()) {
                day.setRequestId(requestId);
                addDayToRequest(day);
            }
        }
        return requestId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateRequest(TimeOffRequest request) {
        //remove old comments and days
        removeAllComments(request.getRequestId());
        removeAllDays(request.getRequestId());

        //update the other info for the request
        MapSqlParameterSource params = getAddRequestParams(request);
        params.addValue("requestId", request.getRequestId());
        int changed = localNamedJdbc.update(SqlTimeOffRequestQuery.UPDATE_REQUEST.getSql(schemaMap()), params);
        //add in the new comments and days
        if(request.getComments() != null) {
            for (TimeOffRequestComment comment : request.getComments()) {
                comment.setRequestId(request.getRequestId());
                addCommentToRequest(comment);
            }
        }
        if(request.getDays() != null) {
            for (TimeOffRequestDay day : request.getDays()) {
                day.setRequestId(request.getRequestId());
                addDayToRequest(day);
            }
        }
        return (changed == 1);
    }


    /* ***** PRIVATE HELPER FUNCTIONS ***** */

    /**
     * Helper function that gets requests for multiple request ids
     *
     * @param requestIds List<Integer>
     * @return List<TimeOffRequest>
     */
    private List<TimeOffRequest> getMultipleRequests(List<Integer> requestIds) {
        List<TimeOffRequest> requests = new ArrayList<>();
        TimeOffRequest tempRequest;
        for (int id : requestIds) {
            tempRequest = getRequestById(id);
            requests.add(tempRequest);
        }
        return requests;
    }


    /**
     * Helper function to get the parameters need from a TimeOffRequestComment to add the comment
     * to a request
     *
     * @param comment TimeOffRequestComment
     * @return MapSqlParameterSource
     */
    private static MapSqlParameterSource getAddCommentParams(TimeOffRequestComment comment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("text", comment.getText());
        params.addValue("authorId", comment.getAuthorId());
        params.addValue("timestamp", comment.getTimestamp());
        params.addValue("requestId", comment.getRequestId());
        return params;
    }

    /**
     * Helper function to get the parameters needed from a TimeOffRequestDay to add
     * the day to a request
     *
     * @param day TimeOffRequestDay
     * @return MapSqlParameterSource
     */
    private static MapSqlParameterSource getAddDayParams(TimeOffRequestDay day) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("requestId", day.getRequestId());
        params.addValue("date", day.getDate());
        params.addValue("workHours", day.getWorkHours());
        params.addValue("holidayHours", day.getHolidayHours());
        params.addValue("vacationHours", day.getVacationHours());
        params.addValue("personalHours", day.getPersonalHours());
        params.addValue("sickEmpHours", day.getSickEmpHours());
        params.addValue("sickFamHours", day.getSickFamHours());
        params.addValue("miscHours", day.getMiscHours());
                                                    //Allow misc_type to be null
        params.addValue("miscType", Optional.ofNullable(day.getMiscType())
                                                        .map(Enum::name)
                                                        .orElse(null));
        return params;
    }

    /**
     * Helper function to get the parameters needed from a TimeOffRequest to
     * add it to the database
     *
     * @param request TimeOffRequest
     * @return MapSqlParameterSource
     */
    private static MapSqlParameterSource getAddRequestParams(TimeOffRequest request) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("employeeId", request.getEmployeeId());
        params.addValue("supervisorId", request.getSupervisorId());
        params.addValue("status", request.getStatus().getName());
        params.addValue("updateTimestamp", request.getTimestamp());
        params.addValue("startDate", request.getStartDate());
        params.addValue("endDate", request.getEndDate());
        return params;
    }
}
