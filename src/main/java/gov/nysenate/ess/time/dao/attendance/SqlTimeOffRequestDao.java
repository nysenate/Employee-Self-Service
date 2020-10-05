package gov.nysenate.ess.time.dao.attendance;

import com.google.common.collect.Range;
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


import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
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
    public List<TimeOffRequest> getAllRequestsByEmpId(int employeeId, Range<LocalDate> dateRange) {
        //get the request ids of all requests with the employeeID
        List<Integer> requestIds;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("employeeId", employeeId);
        params.addValue("startRange", Date.valueOf(dateRange.lowerEndpoint()));
        params.addValue("endRange", Date.valueOf(dateRange.upperEndpoint()));
        requestIds = localNamedJdbc.query(SqlTimeOffRequestQuery.SELECT_TIME_OFF_REQUEST_IDS_BY_EMPLOYEE_ID_DATE_RANGE.getSql(schemaMap()),
                params, (rs, rowNum) -> rs.getInt("request_id"));
        return getMultipleRequests(requestIds);
        //filter the requests by comparing their date range to the given data range.
        //If there is any overlap, keep the request in the list to be returned
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
    public List<TimeOffRequest> getActiveTimeOffRequests(int supervisorId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("supervisorId", supervisorId);
        List<Integer> requestIds;
        requestIds = localNamedJdbc.query(SqlTimeOffRequestQuery.SELECT_ACTIVE_TIME_OFF_REQUEST_IDS.getSql(schemaMap()),
                params, (rs, rowNum) -> rs.getInt("request_id"));
        return getMultipleRequests(requestIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int updateRequest(TimeOffRequest request) {
        int requestId = request.getRequestId();

        //If the request is new, add it to the database,
        //Otherwise, update the request.
        if(requestId == -1) {
            requestId = addNewRequest(request);
        } else {
            //remove old comments and days
            removeAllComments(request.getRequestId());
            removeAllDays(request.getRequestId());

            //update the other info for the request
            MapSqlParameterSource params = getAddRequestParams(request);
            params.addValue("requestId", request.getRequestId());
            int changed = localNamedJdbc.update(SqlTimeOffRequestQuery.UPDATE_REQUEST.getSql(schemaMap()), params);
            if(changed == 0) {
                requestId = -1;
            }
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
        }

        return requestId;
    }




    /* ***** PRIVATE HELPER FUNCTIONS ***** */

    /**
     * Helper function to add a request to the database.
     * This function is called by the updateRequest() function
     * when the request does not yet exist in the database.
     *
     * @param request TimeOffRequest
     * @return int - The requestId of the newly added request
     */
    private int addNewRequest(TimeOffRequest request) {
        MapSqlParameterSource params = getAddRequestParams(request);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String column = "request_id";
        String[] keyCols = {column};
        localNamedJdbc.update(SqlTimeOffRequestQuery.ADD_TIME_OFF_REQUEST.getSql(schemaMap()),
                params, keyHolder, keyCols);
        int requestId = (Integer) keyHolder.getKeys().get(column);

        //add each comment in the request
        if (request.getComments() != null) {
            for (TimeOffRequestComment comment : request.getComments()) {
                comment.setRequestId(requestId);
                addCommentToRequest(comment);
            }
        }

        //add each day in the request
        if (request.getDays() != null) {
            for (TimeOffRequestDay day : request.getDays()) {
                day.setRequestId(requestId);
                addDayToRequest(day);
            }
        }
        return requestId;
    }

    /**
     * Helper function to add a comment to the comment thread of a request
     *
     * @param comment TimeOffRequestComment
     */

    private void addCommentToRequest(TimeOffRequestComment comment) {
        MapSqlParameterSource params = getAddCommentParams(comment);
        localNamedJdbc.update(SqlTimeOffRequestQuery.ADD_COMMENT_TO_TIME_OFF_REQUEST.getSql(schemaMap()), params);
    }

    /**
     * Helper function to delete all comments for a request
     *
     * @param requestId int
     */
    private void removeAllComments(int requestId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("requestId", requestId);
        localNamedJdbc.update(SqlTimeOffRequestQuery.REMOVE_ALL_COMMENTS_FOR_REQUEST.getSql(schemaMap()), params);
    }

    /**
     * Helper function to add a day with time off to a request
     *
     * @param day TimeOffRequestDay
     */
    private void addDayToRequest(TimeOffRequestDay day) {
        MapSqlParameterSource params = getAddDayParams(day);
        localNamedJdbc.update(SqlTimeOffRequestQuery.ADD_DAY_TO_TIME_OFF_REQUEST.getSql(schemaMap()), params);
    }

    /**
     * Helping function to delete all days for a request
     *
     * @param requestId int
     */
    private void removeAllDays(int requestId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("requestId", requestId);
        localNamedJdbc.update(SqlTimeOffRequestQuery.REMOVE_ALL_DAYS_FOR_REQUEST.getSql(schemaMap()), params);
    }

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
        params.addValue("timestamp", toDate(comment.getTimestamp()));
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
        params.addValue("date", toDate(day.getDate()));
        params.addValue("workHours", day.getWorkHours().orElse(BigDecimal.ZERO));
        params.addValue("holidayHours", day.getHolidayHours().orElse(BigDecimal.ZERO));
        params.addValue("vacationHours", day.getVacationHours().orElse(BigDecimal.ZERO));
        params.addValue("personalHours", day.getPersonalHours().orElse(BigDecimal.ZERO));
        params.addValue("sickEmpHours", day.getSickEmpHours().orElse(BigDecimal.ZERO));
        params.addValue("sickFamHours", day.getSickFamHours().orElse(BigDecimal.ZERO));
        params.addValue("miscHours", day.getMiscHours().orElse(BigDecimal.ZERO));
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
        params.addValue("updateTimestamp", toDate(request.getTimestamp()));
        params.addValue("startDate",  toDate(request.getStartDate()));
        params.addValue("endDate",  toDate(request.getEndDate()));
        return params;
    }
}
