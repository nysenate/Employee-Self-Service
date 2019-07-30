package gov.nysenate.ess.time.dao.attendance;

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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
        Date date = new Date();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, new Timestamp(date.getTime()), date, date, null, null);
        //attempt adding the request to the database
        int numBefore = sqlTimeOffRequestDao.getAllRequestsByEmpId(123).size();
        sqlTimeOffRequestDao.addNewRequest(request);
        int numAfter = sqlTimeOffRequestDao.getAllRequestsByEmpId(123).size();
        assertEquals("The wrong number of requests were added.", 1, numAfter - numBefore);
    }

    @Test
    public void addCommentTest() {
        Date date = new Date();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, new Timestamp(date.getTime()), date, date, null, null);
        sqlTimeOffRequestDao.addNewRequest(request);
        List<TimeOffRequest> requests = sqlTimeOffRequestDao.getAllRequestsByEmpId(123);
        TimeOffRequest retrievedRequest = requests.get(0);

        int requestId = retrievedRequest.getRequestId();
        String text = "I am a comment";
        TimeOffRequestComment comment = new TimeOffRequestComment(requestId, requestId, text);
        int commentsBefore = sqlTimeOffRequestDao.getRequestById(requestId).getComments().size();
        sqlTimeOffRequestDao.addCommentToRequest(comment);
        int commentsAfter = sqlTimeOffRequestDao.getRequestById(requestId).getComments().size();
        List<TimeOffRequestComment> retrievedComments = sqlTimeOffRequestDao.getRequestById(requestId).getComments();
        assertEquals("Number of comments added is incorrect.", 1, commentsAfter - commentsBefore);
    }

    @Test
    public void addDayTest() {
        Date date = new Date();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, new Timestamp(date.getTime()), date, date, null, null);
        sqlTimeOffRequestDao.addNewRequest(request);
        List<TimeOffRequest> requests = sqlTimeOffRequestDao.getAllRequestsByEmpId(123);
        TimeOffRequest retrievedRequest = requests.get(0);

        int requestId = retrievedRequest.getRequestId();
        Date dateOne = new Date(1564632000000L);
        Date dateTwo = new Date(1564718400000L);
        TimeOffRequestDay dayOne = new TimeOffRequestDay(requestId, dateOne, 0, 0,
                7, 0,0,0,0, null);
        TimeOffRequestDay dayTwo = new TimeOffRequestDay(requestId, dateTwo, 0,0,
                7,0,0,0,0,null);

        int daysBefore = sqlTimeOffRequestDao.getRequestById(requestId).getDays().size();
        sqlTimeOffRequestDao.addDayToRequest(dayOne);
        sqlTimeOffRequestDao.addDayToRequest(dayTwo);
        int daysAfter = sqlTimeOffRequestDao.getRequestById(requestId).getDays().size();
        assertEquals("Wrong number of days added to request.", 2, daysAfter-daysBefore);
    }

    @Test
    public void getRequestsByEmpIdWhenNoneTest() {
        List<TimeOffRequest> requests = sqlTimeOffRequestDao.getAllRequestsByEmpId(789);
        assertEquals("No requests should have been returned.", 0, requests.size());
    }

    @Test
    public void getRequestsBySupIdWhenNoneTest() {
        List<TimeOffRequest> requests = sqlTimeOffRequestDao.getAllRequestsBySupId(789);
        assertEquals("No requests should have been returned.", 0, requests.size());
    }

    @Test
    public void getRequestsBySupIdTest() {
        Date date = new Date();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, new Timestamp(date.getTime()), date, date, null, null);

        int numBefore = sqlTimeOffRequestDao.getAllRequestsBySupId(456).size();
        sqlTimeOffRequestDao.addNewRequest(request);
        int numAfter = sqlTimeOffRequestDao.getAllRequestsBySupId(456).size();
        assertEquals("Incorrect number of requests were returned.", 1, numAfter - numBefore);
    }

    @Test
    public void getRequestsBySupEmpYearWhenNoneTest() {
        List<TimeOffRequest> requests = sqlTimeOffRequestDao.getAllRequestsBySupEmpYear(456, 123, 2000);
        assertEquals("No requests should have been returned.", 0, requests.size());
    }

    @Test
    public void getRequestBySupEmpYearTest() {
        Date dateOne = new Date(1564632000000L);
        Date dateTwo = new Date(1564718400000L);
        TimeOffRequest requestOne = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, new Timestamp(dateOne.getTime()), dateOne, dateOne, null, null);
        TimeOffRequest requestTwo = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, new Timestamp(dateTwo.getTime()), dateTwo, dateTwo, null, null);
        int numBefore = sqlTimeOffRequestDao.getAllRequestsBySupEmpYear(456, 123, 2019).size();
        sqlTimeOffRequestDao.addNewRequest(requestOne);
        sqlTimeOffRequestDao.addNewRequest(requestTwo);
        int numAfter = sqlTimeOffRequestDao.getAllRequestsBySupEmpYear(456, 123, 2019).size();
        assertEquals("Incorrect number of requests were returned.", 2, numAfter - numBefore);
    }

    @Test
    public void requestCommentRowMapperTest() {
        //create a request and a comment for that request
        Date date = new Date();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, new Timestamp(date.getTime()), date, date, null, null);
        sqlTimeOffRequestDao.addNewRequest(request);
        List<TimeOffRequest> requests = sqlTimeOffRequestDao.getAllRequestsByEmpId(123);
        TimeOffRequest retrievedRequest = requests.get(0);
        int requestId = retrievedRequest.getRequestId();
        String text = "I am a comment";
        TimeOffRequestComment comment = new TimeOffRequestComment(requestId, 123, text);

        //add and then retrieve the comment
        sqlTimeOffRequestDao.addCommentToRequest(comment);
        List<TimeOffRequestComment> retrievedComments = sqlTimeOffRequestDao.getRequestById(requestId).getComments();
        TimeOffRequestComment retrievedComment = retrievedComments.get(0);

        //verify information in comment
        assertEquals("The text of the comment was not mapped back properly.", text, retrievedComment.getText());
        assertEquals("The authorId of the comment was not mapped back properly.", 123, retrievedComment.getAuthorId());
        assertTrue("The timestamp of the comment was not mapped back properly.", !retrievedComment.getTimestamp().equals(null));
        assertEquals("The requestId of the comment was not mapped back properly.", requestId, retrievedComment.getRequestId());

    }

    @Test
    public void requestDayRowMapperTest() {
        //create a request and a day for that request
        Date date = new Date();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, new Timestamp(date.getTime()), date, date, null, null);
        sqlTimeOffRequestDao.addNewRequest(request);
        List<TimeOffRequest> requests = sqlTimeOffRequestDao.getAllRequestsByEmpId(123);
        TimeOffRequest retrievedRequest = requests.get(0);
        int requestId = retrievedRequest.getRequestId();
        Date dateOne = new Date(1564632000000L);
        TimeOffRequestDay dayOne = new TimeOffRequestDay(requestId, dateOne, 1, 2,
                3, 4,5,6,7, MiscLeaveType.JURY_LEAVE);

        //add and then retrieve the day
        sqlTimeOffRequestDao.addDayToRequest(dayOne);
        List<TimeOffRequestDay> days = sqlTimeOffRequestDao.getRequestById(requestId).getDays();
        TimeOffRequestDay retrievedDay = days.get(0);

        //verify the information in the retrieved day
        assertEquals("The requestId of the day was not mapped back properly.", requestId, retrievedDay.getRequestId());
        assertEquals("The date of the day was not mapped back properly.", dateOne, retrievedDay.getDate());
        assertEquals("The workHours of the day was not mapped back properly.", 1, retrievedDay.getWorkHours());
        assertEquals("The holidayHours of the day was not mapped back properly.",2, retrievedDay.getHolidayHours());
        assertEquals("The vacationHours of the day was not mapped back properly.",3, retrievedDay.getVacationHours());
        assertEquals("The personalHours of the day was not mapped back properly.",4, retrievedDay.getPersonalHours());
        assertEquals("The sickEmpHours of the day was not mapped back properly.",5, retrievedDay.getSickEmpHours());
        assertEquals("The sickFamHours of the day was not mapped back properly.",6,retrievedDay.getSickFamHours());
        assertEquals("The miscHours of the day was not mapped back properly.",7,retrievedDay.getMiscHours());
        assertEquals("The miscType of the day was not mapped back properly.",MiscLeaveType.JURY_LEAVE,retrievedDay.getMiscType());
    }

    @Test
    public void requestRowMapperTest() {
        //Create a request, and then two days and two comments to the request
        Date date = new Date();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, new Timestamp(date.getTime()), date, date, null, null);
        int requestId = sqlTimeOffRequestDao.addNewRequest(request);
        request.setRequestId(requestId);

        Date dateOne = new Date(1564632000000L);
        Date dateTwo = new Date(1564718400000L);
        TimeOffRequestDay dayOne = new TimeOffRequestDay(requestId, dateOne, 0, 0,
                7, 0,0,0,0, null);
        TimeOffRequestDay dayTwo = new TimeOffRequestDay(requestId, dateTwo, 0,0,
                7,0,0,0,0,null);

        sqlTimeOffRequestDao.addDayToRequest(dayOne);
        sqlTimeOffRequestDao.addDayToRequest(dayTwo);

        String text = "I am a comment";
        TimeOffRequestComment commentOne = new TimeOffRequestComment(requestId, 123, text);

        sqlTimeOffRequestDao.addCommentToRequest(commentOne);

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
        Date date = new Date();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, new Timestamp(date.getTime()), date, date, null, null);
        int requestId = sqlTimeOffRequestDao.addNewRequest(request);
        request.setRequestId(requestId);

        //create comments
        Timestamp t1 = new Timestamp(1564372800000L);
        Timestamp t2 = new Timestamp(1564459200000L);
        Timestamp t3 = new Timestamp(1564545600000L);
        TimeOffRequestComment commentOne = new TimeOffRequestComment(requestId, 123, t1,"one");
        TimeOffRequestComment commentTwo = new TimeOffRequestComment(requestId, 123, t2,"two");
        TimeOffRequestComment commentThree = new TimeOffRequestComment(requestId, 123, t3,"three");
        sqlTimeOffRequestDao.addCommentToRequest(commentOne);
        sqlTimeOffRequestDao.addCommentToRequest(commentTwo);
        sqlTimeOffRequestDao.addCommentToRequest(commentThree);

        //delete comments
        sqlTimeOffRequestDao.removeAllComments(requestId);

        //verify that the comments were deleted
        TimeOffRequest retrievedRequest = sqlTimeOffRequestDao.getRequestById(requestId);
        assertEquals("Comments were not deleted properly.", 0, retrievedRequest.getComments().size());

    }

    @Test
    public void removeAllDaysTest() {
        //create a request
        Date date = new Date();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, new Timestamp(date.getTime()), date, date, null, null);
        int requestId = sqlTimeOffRequestDao.addNewRequest(request);
        request.setRequestId(requestId);

        //create days
        Date dateOne = new Date(1564632000000L);
        Date dateTwo = new Date(1564718400000L);
        TimeOffRequestDay dayOne = new TimeOffRequestDay(requestId, dateOne, 0, 0,
                7, 0,0,0,0, null);
        TimeOffRequestDay dayTwo = new TimeOffRequestDay(requestId, dateTwo, 0,0,
                7,0,0,0,0, MiscLeaveType.JURY_LEAVE);
        sqlTimeOffRequestDao.addDayToRequest(dayOne);
        sqlTimeOffRequestDao.addDayToRequest(dayTwo);

        //delete days
        sqlTimeOffRequestDao.removeAllDays(requestId);

        //verify that the days were deleted
        TimeOffRequest retrievedRequest = sqlTimeOffRequestDao.getRequestById(requestId);
        assertEquals("Days were not deleted properly.", 0, retrievedRequest.getDays().size());
    }

    @Test
    public void  updateRequestTest() {
        //create a request
        Date date = new Date();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.SAVED, new Timestamp(date.getTime()), date, date, null, null);
        int requestId = sqlTimeOffRequestDao.addNewRequest(request);
        request.setRequestId(requestId);

        //make changes to the request
        Date newDate = new Date(946702800000L);
        request.setEndDate(newDate);
        request.setStartDate(newDate);
        request.setStatus(TimeOffStatus.APPROVED);
        Timestamp t1 = new Timestamp(1564372800000L);
        Timestamp t2 = new Timestamp(1564459200000L);
        Timestamp t3 = new Timestamp(1564545600000L);
        TimeOffRequestComment commentOne = new TimeOffRequestComment(requestId, 123, t1,"one");
        TimeOffRequestComment commentTwo = new TimeOffRequestComment(requestId, 123, t2,"two");
        TimeOffRequestComment commentThree = new TimeOffRequestComment(requestId, 123, t3,"three");
        List<TimeOffRequestComment> comments = new ArrayList<>();
        comments.add(commentOne);
        comments.add(commentTwo);
        comments.add(commentThree);
        request.setComments(comments);
        Timestamp newTimestamp = new Timestamp(1564977600000L);
        request.setTimestamp(newTimestamp);

        //update the request
        sqlTimeOffRequestDao.updateRequest(request);
        TimeOffRequest retrievedRequest = sqlTimeOffRequestDao.getRequestById(requestId);

        //verify the changes
        assertEquals("Start date wasn't updated properly.", newDate, retrievedRequest.getStartDate());
        assertEquals("End date wasn't updated properly.", newDate, retrievedRequest.getEndDate());
        assertEquals("Status wasn't updated properly.", TimeOffStatus.APPROVED, retrievedRequest.getStatus());
        assertEquals("Timestamp date wasn't updated properly.", newTimestamp, retrievedRequest.getTimestamp());
        assertEquals("Comments date wasn't updated properly.", comments, retrievedRequest.getComments());
        assertEquals("Supervisor ID should not have changed.", 456, retrievedRequest.getSupervisorId());
        assertEquals("Employee ID should not have changed.", 123, retrievedRequest.getEmployeeId());
    }

    @Test
    public void addRequestWhenListsNotNullTest() {
        //create a request
        Date date = new Date();
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.SAVED, new Timestamp(date.getTime()), date, date, null, null);

        //create a list of comments and set request comments to the list
        Timestamp t1 = new Timestamp(1564372800000L);
        Timestamp t2 = new Timestamp(1564459200000L);
        Timestamp t3 = new Timestamp(1564545600000L);
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
        Date dateOne = new Date(1564632000000L);
        Date dateTwo = new Date(1564718400000L);
        TimeOffRequestDay dayOne = new TimeOffRequestDay(-1, dateOne, 0, 0,
                7, 0,0,0,0, null);
        TimeOffRequestDay dayTwo = new TimeOffRequestDay(-1, dateTwo, 0,0,
                7,0,0,0,0, MiscLeaveType.JURY_LEAVE);
        List<TimeOffRequestDay> days = new ArrayList<>();
        days.add(dayOne);
        days.add(dayTwo);
        request.setDays(days);

        //add the request to the database and then retrieve it
        int requestId = sqlTimeOffRequestDao.addNewRequest(request);

        //verify the comments and days of the request
        assertEquals("The comments of the request were not added properly.", comments,
                sqlTimeOffRequestDao.getRequestById(requestId).getComments());
        assertEquals("The days of the request were not added properly.", days,
                sqlTimeOffRequestDao.getRequestById(requestId).getDays());
    }

    @Test
    public void addRequestReturnsCorrectRequestIdTest() {
        //create a request
        Date date = new Date();         //Timestamp is 01/01/2019
        TimeOffRequest request = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.SAVED, new Timestamp(1546318800000L), date, date, null, null);
        int requestIdOne = sqlTimeOffRequestDao.addNewRequest(request);

        //create a request
        Date dateTwo = new Date();      //Timestamp is 01/01/2020
        TimeOffRequest requestTwo = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.SAVED, new Timestamp(1577854800000L), date, date, null, null);
        int requestIdTwo = sqlTimeOffRequestDao.addNewRequest(requestTwo);

        //verify the requestIds
        assertTrue("The requestId for request two is incorrect.", requestIdOne < requestIdTwo);
    }
}

