package gov.nysenate.ess.time.dao.attendance;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.config.DatabaseConfig;
import gov.nysenate.ess.time.model.attendance.TimeOffRequest;
import gov.nysenate.ess.time.model.attendance.TimeOffRequestComment;
import gov.nysenate.ess.time.model.attendance.TimeOffRequestDay;
import gov.nysenate.ess.time.model.attendance.TimeOffStatus;
import gov.nysenate.ess.time.model.payroll.MiscLeaveType;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
@Transactional(DatabaseConfig.localTxManager)
public class SqlTimeOffRequestDaoTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(SqlTimeOffRequestDaoTest.class);

    @Autowired
    private SqlTimeOffRequestDao sqlTimeOffRequestDao;

    @Test
    public void addRequestTest() {
        LocalDate date = LocalDate.now();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, date.atStartOfDay(), date, date, null, null);
        //attempt adding the request to the database
        int numBefore = sqlTimeOffRequestDao.getAllRequestsBySupId(456).size();
        sqlTimeOffRequestDao.updateRequest(request);
        int numAfter = sqlTimeOffRequestDao.getAllRequestsBySupId(456).size();
        assertEquals("The wrong number of requests were added.", 1, numAfter - numBefore);
    }

    @Test
    public void addCommentTest() {
        LocalDate date = LocalDate.now();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, date.atStartOfDay(), date, date, new ArrayList<>(), new ArrayList<>());
        int requestId = sqlTimeOffRequestDao.updateRequest(request);
        String text = "I am a comment";
        TimeOffRequestComment comment = new TimeOffRequestComment(requestId, requestId, text);
        int commentsBefore = sqlTimeOffRequestDao.getRequestById(requestId).getComments().size();
        List<TimeOffRequestComment> newComments = request.getComments();
        newComments.add(comment);
        request.setComments(newComments);
        request.setRequestId(requestId);
        sqlTimeOffRequestDao.updateRequest(request);
        int commentsAfter = sqlTimeOffRequestDao.getRequestById(requestId).getComments().size();
        assertEquals("Number of comments added is incorrect.", 1, commentsAfter - commentsBefore);
    }

    @Test
    public void addDayTest() {
        LocalDate date = LocalDate.now();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, date.atStartOfDay(), date, date, new ArrayList<>(), new ArrayList<>());
        int requestId = sqlTimeOffRequestDao.updateRequest(request);
        LocalDate dateOne = LocalDate.now();
        LocalDate dateTwo = LocalDate.now().plusDays(1);
        List<TimeOffRequestDay> days = new ArrayList<>();
        TimeOffRequestDay dayOne = new TimeOffRequestDay(requestId, dateOne, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.valueOf(7), BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, null);
        TimeOffRequestDay dayTwo = new TimeOffRequestDay(requestId, dateTwo, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.valueOf(7), BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, null);
        days.add(dayOne);
        days.add(dayTwo);
        request.setDays(days);
        request.setRequestId(requestId);
        int daysBefore = sqlTimeOffRequestDao.getRequestById(requestId).getDays().size();
        //add day one and day two to the request before updating
        sqlTimeOffRequestDao.updateRequest(request);
        int daysAfter = sqlTimeOffRequestDao.getRequestById(requestId).getDays().size();
        assertEquals("Wrong number of days added to request.", 2, daysAfter-daysBefore);
    }

    @Test
    public void getRequestsByEmpIdWhenNoneTest() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Range<LocalDate> dateRange = Range.closed(today, tomorrow);
        List<TimeOffRequest> requests = sqlTimeOffRequestDao.getAllRequestsByEmpId(789, dateRange);
        assertEquals("No requests should have been returned.", 0, requests.size());
    }

    @Test
    public void getRequestByEmpIdStandardIntersectionTest() {
        LocalDate today = LocalDate.now();
        LocalDate rEnd = today.plusDays(3);
        Range<LocalDate> dateRange = Range.closed(today, rEnd);
        LocalDate startDateOne = today.plusDays(2);
        LocalDate endDateOne = today.plusDays(4);
        LocalDate startDateTwo = today.plusDays(-1);
        LocalDate endDateTwo = today.plusDays(1);
        LocalDate startDateThree = today.plusDays(1);
        LocalDate endDateThree = today.plusDays(2);
        LocalDate startDateFour = today.plusDays(-2);
        LocalDate endDateFour = today.plusDays(-1);

        TimeOffRequest requestOne = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, today.atStartOfDay(), startDateOne, endDateOne, new ArrayList<>(), new ArrayList<>());
        TimeOffRequest requestTwo = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, today.atStartOfDay(), startDateTwo, endDateTwo, new ArrayList<>(), new ArrayList<>());
        TimeOffRequest requestThree = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, today.atStartOfDay(), startDateThree, endDateThree, new ArrayList<>(), new ArrayList<>());
        TimeOffRequest requestFour = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, today.atStartOfDay(), startDateFour, endDateFour, new ArrayList<>(), new ArrayList<>());

        List<TimeOffRequest> resultsBefore = sqlTimeOffRequestDao.getAllRequestsByEmpId(123, dateRange);

        sqlTimeOffRequestDao.updateRequest(requestOne);
        sqlTimeOffRequestDao.updateRequest(requestTwo);
        sqlTimeOffRequestDao.updateRequest(requestThree);
        int id4 = sqlTimeOffRequestDao.updateRequest(requestFour);

        List<TimeOffRequest> results = sqlTimeOffRequestDao.getAllRequestsByEmpId(123, dateRange);
        List<Integer> ids = new ArrayList<>();
        for(TimeOffRequest result: results) {
            ids.add(result.getRequestId());
        }
        assertFalse("The fourth request should not have been added.", ids.contains(id4));
        assertEquals("An incorrect number of requests was retrieved.", 3, results.size() - resultsBefore.size());
    }

    @Test
    public void getRequestByEmpIdEndPointsTest() {
        LocalDate today = LocalDate.now();
        LocalDate rEnd = today.plusDays(3);
        Range<LocalDate> dateRange = Range.closed(today, rEnd);
        LocalDate startDateOne = today;
        LocalDate endDateOne = today.plusDays(4);
        LocalDate startDateTwo = today.plusDays(-1);
        LocalDate endDateTwo = today.plusDays(3);
        LocalDate startDateThree = today.plusDays(3);
        LocalDate endDateThree = today.plusDays(4);
        LocalDate startDateFour = today.plusDays(-1);
        LocalDate endDateFour = today;

        TimeOffRequest requestOne = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, today.atStartOfDay(), startDateOne, endDateOne, new ArrayList<>(), new ArrayList<>());
        TimeOffRequest requestTwo = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, today.atStartOfDay(), startDateTwo, endDateTwo, new ArrayList<>(), new ArrayList<>());
        TimeOffRequest requestThree = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, today.atStartOfDay(), startDateThree, endDateThree, new ArrayList<>(), new ArrayList<>());
        TimeOffRequest requestFour = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, today.atStartOfDay(), startDateFour, endDateFour, new ArrayList<>(), new ArrayList<>());

        List<TimeOffRequest> resultsBefore = sqlTimeOffRequestDao.getAllRequestsByEmpId(123, dateRange);

        int id1 = sqlTimeOffRequestDao.updateRequest(requestOne);
        int id2 = sqlTimeOffRequestDao.updateRequest(requestTwo);
        int id3 = sqlTimeOffRequestDao.updateRequest(requestThree);
        int id4 = sqlTimeOffRequestDao.updateRequest(requestFour);

        List<TimeOffRequest> results = sqlTimeOffRequestDao.getAllRequestsByEmpId(123, dateRange);
        List<Integer> ids = new ArrayList<>();
        for(TimeOffRequest result: results) {
            ids.add(result.getRequestId());
        }
        assertTrue("All four requests should have been added.",
                ids.contains(id4) && ids.contains(id3) && ids.contains(id2) && ids.contains(id1));
        assertEquals("An incorrect number of requests was retrieved.", 4, results.size() - resultsBefore.size());
    }

    @Test
    public void getRequestByEmpIdCompleteOverlap() {
        LocalDate today = LocalDate.now();
        LocalDate rEnd = today.plusDays(3);
        Range<LocalDate> dateRange = Range.closed(today, rEnd);
        LocalDate startDateOne = today;
        LocalDate endDateOne = rEnd;
        LocalDate startDateTwo = today.plusDays(-1);
        LocalDate endDateTwo = today.plusDays(4);

        TimeOffRequest requestOne = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, today.atStartOfDay(), startDateOne, endDateOne, new ArrayList<>(), new ArrayList<>());
        TimeOffRequest requestTwo = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, today.atStartOfDay(), startDateTwo, endDateTwo, new ArrayList<>(), new ArrayList<>());

        List<TimeOffRequest> resultsBefore = sqlTimeOffRequestDao.getAllRequestsByEmpId(123, dateRange);

        int id1 = sqlTimeOffRequestDao.updateRequest(requestOne);
        int id2 = sqlTimeOffRequestDao.updateRequest(requestTwo);

        List<TimeOffRequest> results = sqlTimeOffRequestDao.getAllRequestsByEmpId(123, dateRange);
        List<Integer> ids = new ArrayList<>();
        for(TimeOffRequest result: results) {
            ids.add(result.getRequestId());
        }
        assertTrue("Both requests should have been added.", ids.contains(id2) && ids.contains(id1));
        assertEquals("An incorrect number of requests was retrieved.", 2, results.size() - resultsBefore.size());
    }

    @Test
    public void getActiveRequestsWhenNoneTest() {
        List<TimeOffRequest> requests = sqlTimeOffRequestDao.getActiveTimeOffRequests(456);
        assertEquals("No requests should have been returned.", 0, requests.size());
    }

    @Test
    public void getActiveRequestsTest() {
        int numBefore = sqlTimeOffRequestDao.getRequestsNeedingApproval(456).size();
        //create three requests with same supervisor, two with status APPROVED
        LocalDate date = LocalDate.now();
        LocalDateTime timeOne = LocalDateTime.of(2019,01,01,0,0,0);
        TimeOffRequest request = new TimeOffRequest(-1, 1, 456,
                TimeOffStatus.APPROVED, timeOne, date, date, new ArrayList<>(), new ArrayList<>());
        sqlTimeOffRequestDao.updateRequest(request);

        LocalDateTime timeTwo = LocalDateTime.of(2020,01,01,0,0,0);
        TimeOffRequest requestTwo = new TimeOffRequest(-1, 2, 456,
                TimeOffStatus.APPROVED, timeTwo, date, date, new ArrayList<>(), new ArrayList<>());
        sqlTimeOffRequestDao.updateRequest(requestTwo);

        LocalDateTime timeThree = LocalDateTime.of(2020,01,01,0,0,0);
        TimeOffRequest requestThree = new TimeOffRequest(-1, 3, 456,
                TimeOffStatus.SAVED, timeThree, date, date, new ArrayList<>(), new ArrayList<>());
        sqlTimeOffRequestDao.updateRequest(requestThree);

        //get the requests needing approval for supervisor 456
        List<TimeOffRequest> requests = sqlTimeOffRequestDao.getActiveTimeOffRequests(456);
        List<Integer> EmpIds = new ArrayList<>();
        for(TimeOffRequest r: requests) {
            EmpIds.add(r.getEmployeeId());
        }
        int numAfter = requests.size();

        //verify the requests returned are correct
        assertEquals("The wrong number of requests were returned.", 2, numAfter-numBefore);
        assertTrue("Employee 1's request was not returned.", EmpIds.contains(1));
        assertTrue("Employee 2's request was not returned.", EmpIds.contains(2));
    }

    @Test
    public void getRequestsBySupIdWhenNoneTest() {
        List<TimeOffRequest> requests = sqlTimeOffRequestDao.getAllRequestsBySupId(789);
        assertEquals("No requests should have been returned.", 0, requests.size());
    }

    @Test
    public void getRequestsBySupIdTest() {
        LocalDate date = LocalDate.now();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, date.atStartOfDay(), date, date, new ArrayList<>(), new ArrayList<>());

        int numBefore = sqlTimeOffRequestDao.getAllRequestsBySupId(456).size();
        sqlTimeOffRequestDao.updateRequest(request);
        int numAfter = sqlTimeOffRequestDao.getAllRequestsBySupId(456).size();
        assertEquals("Incorrect number of requests were returned.", 1, numAfter - numBefore);
    }

    @Test
    public void requestCommentRowMapperTest() {
        //create a request and a comment for that request
        LocalDate date = LocalDate.now();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, date.atStartOfDay(), date, date, new ArrayList<>(), new ArrayList<>());
        int requestId = sqlTimeOffRequestDao.updateRequest(request);

        String text = "I am a comment";
        TimeOffRequestComment comment = new TimeOffRequestComment(requestId, 123, text);
        List<TimeOffRequestComment> commentList = new ArrayList<>();
        commentList.add(comment);
        request.setComments(commentList);
        request.setRequestId(requestId);

        //add and then retrieve the comment
        sqlTimeOffRequestDao.updateRequest(request);
        List<TimeOffRequestComment> retrievedComments = sqlTimeOffRequestDao.getRequestById(requestId).getComments();
        TimeOffRequestComment retrievedComment = retrievedComments.get(0);

        //verify information in comment
        assertEquals("The text of the comment was not mapped back properly.", text, retrievedComment.getText());
        assertEquals("The authorId of the comment was not mapped back properly.", 123, retrievedComment.getAuthorId());
        assertEquals("The requestId of the comment was not mapped back properly.", requestId, retrievedComment.getRequestId());

    }

    @Test
    public void requestDayRowMapperTest() {
        //create a request and a day for that request
        LocalDate date = LocalDate.now();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, date.atStartOfDay(), date, date, new ArrayList<>(), new ArrayList<>());
        int requestId = sqlTimeOffRequestDao.updateRequest(request);

        LocalDate dateOne = LocalDate.now();
        TimeOffRequestDay dayOne = new TimeOffRequestDay(requestId, dateOne, BigDecimal.valueOf(1), BigDecimal.valueOf(2),
                BigDecimal.valueOf(3), BigDecimal.valueOf(4),BigDecimal.valueOf(5),BigDecimal.valueOf(6),BigDecimal.valueOf(7),
                MiscLeaveType.JURY_LEAVE);

        //add and then retrieve the day
        List<TimeOffRequestDay> dayList = new ArrayList<>();
        dayList.add(dayOne);
        request.setDays(dayList);
        request.setRequestId(requestId);
        sqlTimeOffRequestDao.updateRequest(request);
        List<TimeOffRequestDay> days = sqlTimeOffRequestDao.getRequestById(requestId).getDays();
        TimeOffRequestDay retrievedDay = days.get(0);

        //verify the information in the retrieved day
        assertEquals("The requestId of the day was not mapped back properly.", requestId, retrievedDay.getRequestId());
        assertEquals("The date of the day was not mapped back properly.", dateOne, retrievedDay.getDate());
        assertEquals("The workHours of the day was not mapped back properly.",
                BigDecimal.valueOf(1), retrievedDay.getWorkHours().orElse(BigDecimal.ZERO));
        assertEquals("The holidayHours of the day was not mapped back properly.",
                BigDecimal.valueOf(2), retrievedDay.getHolidayHours().orElse(BigDecimal.ZERO));
        assertEquals("The vacationHours of the day was not mapped back properly.",
                BigDecimal.valueOf(3), retrievedDay.getVacationHours().orElse(BigDecimal.ZERO));
        assertEquals("The personalHours of the day was not mapped back properly.",
                BigDecimal.valueOf(4), retrievedDay.getPersonalHours().orElse(BigDecimal.ZERO));
        assertEquals("The sickEmpHours of the day was not mapped back properly.",
                BigDecimal.valueOf(5), retrievedDay.getSickEmpHours().orElse(BigDecimal.ZERO));
        assertEquals("The sickFamHours of the day was not mapped back properly.",
                BigDecimal.valueOf(6),retrievedDay.getSickFamHours().orElse(BigDecimal.ZERO));
        assertEquals("The miscHours of the day was not mapped back properly.",
                BigDecimal.valueOf(7),retrievedDay.getMiscHours().orElse(BigDecimal.ZERO));
        assertEquals("The miscType of the day was not mapped back properly.",MiscLeaveType.JURY_LEAVE,retrievedDay.getMiscType());
    }

    @Test
    public void requestRowMapperTest() {
        //Create a request, and then two days and two comments to the request
        LocalDate date = LocalDate.now();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, date.atStartOfDay(), date, date, null, null);
        int requestId = sqlTimeOffRequestDao.updateRequest(request);
        request.setRequestId(requestId);

        LocalDate dateOne = LocalDate.now();
        LocalDate dateTwo = LocalDate.now().plusDays(1);
        TimeOffRequestDay dayOne = new TimeOffRequestDay(requestId, dateOne, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.valueOf(7), BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, null);
        TimeOffRequestDay dayTwo = new TimeOffRequestDay(requestId, dateTwo, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.valueOf(7), BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, null);

        List<TimeOffRequestDay> dayList = new ArrayList<>();
        dayList.add(dayOne);
        dayList.add(dayTwo);
        request.setDays(dayList);

        String text = "I am a comment";
        TimeOffRequestComment commentOne = new TimeOffRequestComment(requestId, 123, text);
        List<TimeOffRequestComment> commentList = new ArrayList<>();
        commentList.add(commentOne);
        request.setComments(commentList);

        sqlTimeOffRequestDao.updateRequest(request);

        //retrieve request and verify the information
        TimeOffRequest retrievedRequest = sqlTimeOffRequestDao.getRequestById(requestId);
        assertTrue("The first day was not mapped properly.",retrievedRequest.getDays().contains(dayOne));
        assertTrue("The second day was not mapped properly.",retrievedRequest.getDays().contains(dayTwo));
        assertTrue("The first comment was not mapped properly.",retrievedRequest.getComments().contains(commentOne));
        assertEquals("The requestId was not mapped back properly.", requestId, retrievedRequest.getRequestId());
        assertEquals("The supervisorId was not mapped back properly.", 456, retrievedRequest.getSupervisorId());
        assertEquals("The employeeId was not mapped back properly.", 123, retrievedRequest.getEmployeeId());
        assertEquals("The status was not mapped back properly.", TimeOffStatus.APPROVED, retrievedRequest.getStatus());
        assertEquals("The timestamp was not mapped back properly.", request.getTimestamp(), retrievedRequest.getTimestamp());
    }

    @Test
    public void removeAllCommentsTest() {
        //create a request
        LocalDate date = LocalDate.now();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, date.atStartOfDay(), date, date, null, null);
        int requestId = sqlTimeOffRequestDao.updateRequest(request);
        request.setRequestId(requestId);

        //create comments
        List<TimeOffRequestComment> commentList = new ArrayList<>();
        LocalDateTime t1 = LocalDate.now().atStartOfDay();
        LocalDateTime t2 = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime t3 = LocalDate.now().plusDays(2).atStartOfDay();
        TimeOffRequestComment commentOne = new TimeOffRequestComment(requestId, 123, t1,"one");
        TimeOffRequestComment commentTwo = new TimeOffRequestComment(requestId, 123, t2,"two");
        TimeOffRequestComment commentThree = new TimeOffRequestComment(requestId, 123, t3,"three");
        commentList.add(commentOne);
        commentList.add(commentTwo);
        commentList.add(commentThree);
        request.setComments(commentList);
        sqlTimeOffRequestDao.updateRequest(request);

        //delete comments
        request.setComments(new ArrayList<>());
        sqlTimeOffRequestDao.updateRequest(request);

        //verify that the comments were deleted
        TimeOffRequest retrievedRequest = sqlTimeOffRequestDao.getRequestById(requestId);
        assertEquals("Comments were not deleted properly.", 0, retrievedRequest.getComments().size());

    }

    @Test
    public void removeAllDaysTest() {
        //create a request
        LocalDate date = LocalDate.now();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, date.atStartOfDay(), date, date, null, null);
        int requestId = sqlTimeOffRequestDao.updateRequest(request);
        request.setRequestId(requestId);

        //create days
        List<TimeOffRequestDay> dayList = new ArrayList<>();
        LocalDate dateOne = LocalDate.now();
        LocalDate dateTwo = LocalDate.now().plusDays(1);
        TimeOffRequestDay dayOne = new TimeOffRequestDay(requestId, dateOne, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.valueOf(7), BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, null);
        TimeOffRequestDay dayTwo = new TimeOffRequestDay(requestId, dateTwo, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.valueOf(7), BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, null);
        dayList.add(dayOne);
        dayList.add(dayTwo);
        request.setDays(dayList);
        sqlTimeOffRequestDao.updateRequest(request);

        //delete days
        request.setDays(new ArrayList<>());
        sqlTimeOffRequestDao.updateRequest(request);

        //verify that the days were deleted
        TimeOffRequest retrievedRequest = sqlTimeOffRequestDao.getRequestById(requestId);
        assertEquals("Days were not deleted properly.", 0, retrievedRequest.getDays().size());
    }

    @Test
    public void  updateRequestTest() {
        //create a request
        LocalDate date = LocalDate.now();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.SAVED, date.atStartOfDay(), date, date, new ArrayList<>(), new ArrayList<>());
        int requestId = sqlTimeOffRequestDao.updateRequest(request);
        request.setRequestId(requestId);

        //make changes to the request
        LocalDate newDate = LocalDate.now().plusDays(5);
        request.setEndDate(newDate);
        request.setStartDate(newDate);
        request.setStatus(TimeOffStatus.APPROVED);
        LocalDateTime t1 = LocalDate.now().atStartOfDay();
        LocalDateTime t2 = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime t3 = LocalDate.now().plusDays(2).atStartOfDay();
        TimeOffRequestComment commentOne = new TimeOffRequestComment(requestId, 123, t1,"one");
        TimeOffRequestComment commentTwo = new TimeOffRequestComment(requestId, 123, t2,"two");
        TimeOffRequestComment commentThree = new TimeOffRequestComment(requestId, 123, t3,"three");
        List<TimeOffRequestComment> comments = new ArrayList<>();
        comments.add(commentOne);
        comments.add(commentTwo);
        comments.add(commentThree);
        request.setComments(comments);
        LocalDateTime newTimestamp = LocalDateTime.now();
        request.setTimestamp(newTimestamp);

        //update the request
        sqlTimeOffRequestDao.updateRequest(request);
        TimeOffRequest retrievedRequest = sqlTimeOffRequestDao.getRequestById(requestId);

        //verify the changes
        assertEquals("Start date wasn't updated properly.", newDate, retrievedRequest.getStartDate());
        assertEquals("End date wasn't updated properly.", newDate, retrievedRequest.getEndDate());
        assertEquals("Status wasn't updated properly.", TimeOffStatus.APPROVED, retrievedRequest.getStatus());
        assertEquals("Timestamp date wasn't updated properly.", newTimestamp, retrievedRequest.getTimestamp());
        assertEquals("Comments were not updated properly.", comments, retrievedRequest.getComments());
        assertEquals("Supervisor ID should not have changed.", 456, retrievedRequest.getSupervisorId());
        assertEquals("Employee ID should not have changed.", 123, retrievedRequest.getEmployeeId());
    }

    @Test
    public void addRequestWhenListsNotNullTest() {
        //create a request
        LocalDate date = LocalDate.now();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.SAVED, date.atStartOfDay(), date, date, null, null);

        //create a list of comments and set request comments to the list
        LocalDateTime t1 = LocalDate.now().atStartOfDay();
        LocalDateTime t2 = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime t3 = LocalDate.now().plusDays(2).atStartOfDay();
        TimeOffRequestComment commentOne = new TimeOffRequestComment(-1, 123, t1,"one");
        TimeOffRequestComment commentTwo = new TimeOffRequestComment(-1, 123, t2,"two");
        TimeOffRequestComment commentThree = new TimeOffRequestComment(-1, 123, t3,"three");
        List<TimeOffRequestComment> comments = new ArrayList<>();
        comments.add(commentOne);
        comments.add(commentTwo);
        comments.add(commentThree);
        request.setComments(comments);

        //create a list of days and set request days to the list
        //create days
        LocalDate dateOne = LocalDate.now();
        LocalDate dateTwo = LocalDate.now().plusDays(1);
        TimeOffRequestDay dayOne = new TimeOffRequestDay(-1, dateOne, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.valueOf(7), BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, null);
        TimeOffRequestDay dayTwo = new TimeOffRequestDay(-1, dateTwo, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.valueOf(7), BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, MiscLeaveType.JURY_LEAVE);
        List<TimeOffRequestDay> days = new ArrayList<>();
        days.add(dayOne);
        days.add(dayTwo);
        request.setDays(days);

        //add the request to the database and then retrieve it
        int requestId = sqlTimeOffRequestDao.updateRequest(request);

        //verify the comments and days of the request
        assertEquals("The comments of the request were not added properly.", comments,
                sqlTimeOffRequestDao.getRequestById(requestId).getComments());
        assertEquals("The days of the request were not added properly.", days,
                sqlTimeOffRequestDao.getRequestById(requestId).getDays());
    }

    @Test
    public void updateRequestReturnsCorrectRequestIdTest() {
        //create a request
        LocalDate date = LocalDate.now();
        LocalDateTime timeOne = LocalDateTime.of(2019,01,01,0,0,0);
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.SAVED, timeOne, date, date, new ArrayList<>(), new ArrayList<>());
        int requestIdOne = sqlTimeOffRequestDao.updateRequest(request);

        //create a request
        LocalDate dateTwo = LocalDate.now();
        LocalDateTime timeTwo = LocalDateTime.of(2019,01,01,0,0,0);
        TimeOffRequest requestTwo = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.SAVED, timeTwo, date, date, new ArrayList<>(), new ArrayList<>());
        int requestIdTwo = sqlTimeOffRequestDao.updateRequest(requestTwo);

        //verify the requestIds
        assertTrue("The requestId for request two is incorrect.", requestIdOne < requestIdTwo);
    }

    @Test
    public void getRequestsNeedingApprovalTest() {
        int numBefore = sqlTimeOffRequestDao.getRequestsNeedingApproval(456).size();
        //create three requests with same supervisor, two with status submitted
        LocalDate date = LocalDate.now();
        LocalDateTime timeOne = LocalDateTime.of(2019,01,01,0,0,0);
        TimeOffRequest request = new TimeOffRequest(-1, 1, 456,
                TimeOffStatus.SUBMITTED, timeOne, date, date, new ArrayList<>(), new ArrayList<>());
        int requestIdOne = sqlTimeOffRequestDao.updateRequest(request);

        LocalDateTime timeTwo = LocalDateTime.of(2020,01,01,0,0,0);
        TimeOffRequest requestTwo = new TimeOffRequest(-1, 2, 456,
                TimeOffStatus.SUBMITTED, timeTwo, date, date, new ArrayList<>(), new ArrayList<>());
        int requestIdTwo = sqlTimeOffRequestDao.updateRequest(requestTwo);

        LocalDateTime timeThree = LocalDateTime.of(2020,01,01,0,0,0);
        TimeOffRequest requestThree = new TimeOffRequest(-1, 3, 456,
                TimeOffStatus.SAVED, timeThree, date, date, new ArrayList<>(), new ArrayList<>());
        int requestIdThree = sqlTimeOffRequestDao.updateRequest(requestThree);

        //get the requests needing approval for supervisor 456
        List<TimeOffRequest> requests = sqlTimeOffRequestDao.getRequestsNeedingApproval(456);
        List<Integer> EmpIds = new ArrayList<>();
        for(TimeOffRequest r: requests) {
            EmpIds.add(r.getEmployeeId());
        }
        int numAfter = requests.size();

        //verify the requests returned are correct
        assertEquals("The wrong number of requests were returned.", 2, numAfter-numBefore);
        assertTrue("Employee 1's request was not returned.", EmpIds.contains(1));
        assertTrue("Employee 2's request was not returned.", EmpIds.contains(2));
    }


}

