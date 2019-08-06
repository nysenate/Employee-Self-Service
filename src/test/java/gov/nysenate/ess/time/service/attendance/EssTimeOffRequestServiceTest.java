package gov.nysenate.ess.time.service.attendance;

import com.google.common.collect.RangeSet;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.core.config.DatabaseConfig;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EssCachedEmployeeInfoService;
import gov.nysenate.ess.time.dao.attendance.SqlTimeOffRequestDao;
import gov.nysenate.ess.time.model.attendance.TimeOffRequest;
import gov.nysenate.ess.time.model.attendance.TimeOffStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
@Transactional(DatabaseConfig.localTxManager)
public class EssTimeOffRequestServiceTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(EssTimeOffRequestServiceTest.class);

    @Autowired private SqlTimeOffRequestDao sqlTimeOffRequestDao;
    @Autowired private EssTimeOffRequestService essTimeOffRequestService;
    @Autowired private EssCachedEmployeeInfoService essCachedEmployeeInfoService;

    /* ***Test the helper functions*** */
    @SillyTest
    public void timestampTest() {
        Timestamp ts = essTimeOffRequestService.getCurrentTimestamp();
        logger.info("TIMESTAMP {}",ts);
    }

    @SillyTest
    public void isActiveTrueTest() {
        //set the end date to tomorrow
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        //get an active employee
        int empId = essCachedEmployeeInfoService.getActiveEmpIds().iterator().next();
        int supId = essCachedEmployeeInfoService.getEmployee(empId).getSupervisorId();

        //create request
        Date startDate = new Date();
        TimeOffRequest request = new TimeOffRequest(empId, supId, TimeOffStatus.APPROVED,
                 startDate,  tomorrow, null, null );

        //Add and get back request (So the start and end date are properly formatted)
        int requestId = sqlTimeOffRequestDao.addNewRequest(request);
        TimeOffRequest retrievedRequest = sqlTimeOffRequestDao.getRequestById(requestId);

        //verify that the request is not active
        //boolean isActive = essTimeOffRequestService.isActive(retrievedRequest);
        //assertTrue("Request should be active.", isActive);
    }

    @SillyTest
    public void isActiveFalseTest() {
        //get an inactive employee
        Iterator<Employee> itr = essCachedEmployeeInfoService.getAllEmployees(false).iterator();
        Employee emp = itr.next();
        while(emp.isActive()) {
            emp = itr.next();
        }
        int empId = emp.getEmployeeId();

        //create a request
        RangeSet<LocalDate> activeDates = essCachedEmployeeInfoService.getEmployeeActiveDatesService(empId);
        logger.info("{}", activeDates);
        int supId = essCachedEmployeeInfoService.getEmployee(empId).getSupervisorId();
        essCachedEmployeeInfoService.getEmployee(empId).isActive();
        Date startDate = new Date();
        Date endDate = new Date(); //2099-01-01
        TimeOffRequest request = new TimeOffRequest(empId, supId, TimeOffStatus.APPROVED,
                startDate,  endDate, null, null );

        //Add and get back request (So the start and end date are properly formatted)
        int requestId = sqlTimeOffRequestDao.addNewRequest(request);
        TimeOffRequest retrievedRequest = sqlTimeOffRequestDao.getRequestById(requestId);

        //verify the request is not active
        //boolean isActive = essTimeOffRequestService.isActive(retrievedRequest);
        //assertFalse("Employee is not active, request should not be active.", isActive);
    }

    /* ***Test the main methods*** */

    @Test
    public void getRequestByIdTest() {
        //add a request and then retrieve it using the service
        Date today = new Date();
        TimeOffRequest request = new TimeOffRequest(123, 456, TimeOffStatus.APPROVED,
                today, today, new ArrayList<>(), new ArrayList<>() );
        int requestId = sqlTimeOffRequestDao.addNewRequest(request);
        TimeOffRequest retrievedRequest = essTimeOffRequestService.getTimeOffRequest(requestId);

        //verify that the retrieved request is correct
        assertEquals("Employee Id of retrieved request does not match the original request.",
                request.getEmployeeId(), retrievedRequest.getEmployeeId());
        assertEquals("Supervisor Id of retrieved request does not match the original request.",
                request.getSupervisorId(), retrievedRequest.getSupervisorId());
        assertEquals("Status of retrieved request does not match the original request.",
                request.getStatus(), retrievedRequest.getStatus());
        assertEquals("Comments of retrieved request does not match the original request.",
                request.getComments(), retrievedRequest.getComments());
        assertEquals("Employee Id of retrieved request does not match the original request.",
                request.getDays(), retrievedRequest.getDays());
    }

    @Test
    public void getRequestsBySupEmpYearTest() {
        Date dateOne = new Date(1564632000000L);
        Date dateTwo = new Date(1564718400000L);
        TimeOffRequest requestOne = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, new Timestamp(dateOne.getTime()), dateOne, dateOne, null, null);
        TimeOffRequest requestTwo = new TimeOffRequest(-1, 123, 456,
                TimeOffStatus.APPROVED, new Timestamp(dateTwo.getTime()), dateTwo, dateTwo, null, null);
        int numRequestsBefore = essTimeOffRequestService.getRequests(123,456,2019).size();
        sqlTimeOffRequestDao.addNewRequest(requestOne);
        sqlTimeOffRequestDao.addNewRequest(requestTwo);
        int numRequestsAfter = essTimeOffRequestService.getRequests(123,456,2019).size();
        assertEquals("Two requests should have been returned.", 2, numRequestsAfter-numRequestsBefore);
    }

    @Test
    public void updateRequestTest() {
        Date date = new Date();
        TimeOffRequest request = new TimeOffRequest(123, 456,
                TimeOffStatus.SAVED, date, date, null, null);
        int requestId = sqlTimeOffRequestDao.addNewRequest(request);
        request = sqlTimeOffRequestDao.getRequestById(requestId);
        request.setStatus(TimeOffStatus.SUBMITTED);
        essTimeOffRequestService.updateRequest(request);
        request = sqlTimeOffRequestDao.getRequestById(requestId);
        assertEquals("The status of the request was not properly updated.",
                TimeOffStatus.SUBMITTED, request.getStatus());
    }

    @Test
    public void updateNewRequestTest() {
        Date today = new Date();
        TimeOffRequest request = new TimeOffRequest( 123, 456,
                TimeOffStatus.SAVED, today, today, null, null);
        int requestsBefore = sqlTimeOffRequestDao.getAllRequestsByEmpId(123).size();
        boolean updated = essTimeOffRequestService.updateRequest(request);
        int requestsAfter = sqlTimeOffRequestDao.getAllRequestsByEmpId(123).size();
        assertTrue("Request was not added.", updated);

        assertEquals("Request was not added to the database.", 1,requestsAfter-requestsBefore);
    }

    @Test
    public void getActiveRequestsForEmpTest() {
        //set the end date to tomorrow
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = calendar.getTime();

        //get an active employee
        int empId = essCachedEmployeeInfoService.getActiveEmpIds().iterator().next();

        //create two requests, one active, one not active
        int requestsBefore = essTimeOffRequestService.getActiveRequestsForEmp(empId).size();
        Date today = new Date();
        TimeOffRequest requestActive = new TimeOffRequest(empId, 456,
                TimeOffStatus.SAVED, today, today, null, null);
        TimeOffRequest requestInactive = new TimeOffRequest(empId, 456,
                TimeOffStatus.SAVED, yesterday, yesterday, null, null);
        int requestId = sqlTimeOffRequestDao.addNewRequest(requestActive);
        sqlTimeOffRequestDao.addNewRequest(requestInactive);

        //get the active requests for employee
        List<TimeOffRequest> requests = essTimeOffRequestService.getActiveRequestsForEmp(empId);
        int requestsAfter = requests.size();

        //verify only one request was gotten
        assertEquals("One more request should have been returned.", 1, requestsAfter - requestsBefore);
        //assertEquals("Incorrect request was returned.", requestId, requests.get(0).getRequestId() );
    }

    @Test
    public void getRequestsNeedingApprovalTest() {

        int empId = essCachedEmployeeInfoService.getActiveEmpIds().iterator().next();

        Date today = new Date();
        TimeOffRequest request = new TimeOffRequest(empId, 456,
                TimeOffStatus.SAVED, today, today, null, null);
        //this request needs approval (i.e., it's status is submitted)
        TimeOffRequest requestApproval = new TimeOffRequest(empId, 456,
                TimeOffStatus.SUBMITTED, today, today, null, null);

        int requestsBefore = essTimeOffRequestService.getRequestsNeedingApproval(456).size();

        int requestId = sqlTimeOffRequestDao.addNewRequest(request);
        int requestApprovalId = sqlTimeOffRequestDao.addNewRequest(requestApproval);

        List<TimeOffRequest> requests = essTimeOffRequestService.getRequestsNeedingApproval(456);
        int requestsAfter = requests.size();

        assertEquals("One more request should have been returned.", 1, requestsAfter - requestsBefore);
        //assertEquals("Incorrect request was returned.", requests.get(0).getRequestId(), requestApprovalId);
    }

    @Test
    public void getActiveRequestsForSupTest() {
        int supId = 456;

        //get dates for today and tomorrow
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = calendar.getTime();
        Date today = new Date();

        //get three active employee
        Iterator<Integer> itr = essCachedEmployeeInfoService.getActiveEmpIds().iterator();
        int empIdOne = itr.next();
        int empIdTwo = itr.next();
        int empIdThree = itr.next();

        //create an active request for each employee
        TimeOffRequest requestOne = new TimeOffRequest(empIdOne, supId,
                TimeOffStatus.SAVED, today, today, null, null);
        TimeOffRequest requestTwo = new TimeOffRequest(empIdTwo, supId,
                TimeOffStatus.SAVED, today, today, null, null);
        TimeOffRequest requestThree = new TimeOffRequest(empIdThree, supId,
                TimeOffStatus.SAVED, today, today, null, null);
        //create an inactive request
        TimeOffRequest requestInactive = new TimeOffRequest(empIdOne, supId,
                TimeOffStatus.APPROVED, yesterday, yesterday, null, null);

        int numBefore = essTimeOffRequestService.getActiveRequestsForSup(supId).size();

        //Add the four requests
        int requestIdOne = sqlTimeOffRequestDao.addNewRequest(requestOne);
        int requestIdTwo = sqlTimeOffRequestDao.addNewRequest(requestTwo);
        int requestIdThree = sqlTimeOffRequestDao.addNewRequest(requestThree);
        sqlTimeOffRequestDao.addNewRequest(requestInactive);

        //get the active requests for supervisor
        List<TimeOffRequest> requests = essTimeOffRequestService.getActiveRequestsForSup(supId);
        int numAfter = requests.size();
        List<Integer> ids = new ArrayList<>();
        for(TimeOffRequest tor: requests) {
            ids.add(tor.getRequestId());
        }

        //verify only one request was gotten
        assertEquals("Three more request should have been returned.", 3, numAfter-numBefore);
        assertTrue("RequestOne was not returned.", ids.contains(requestIdOne));
        assertTrue("RequestTwo was not returned.", ids.contains(requestIdTwo));
        assertTrue("RequestThree was not returned.", ids.contains(requestIdThree));
    }
}