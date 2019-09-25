package gov.nysenate.ess.time.service.attendance;

import com.google.common.collect.Range;
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
import org.apache.tomcat.jni.Local;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
    //The assertions for these tests are commented out
    //because the helpers are private functions. They were
    //only made public for manual testing purposes.

    @SillyTest
    public void isActiveTrueTest() {
        //set the end date to tomorrow
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        //get an active employee
        int empId = essCachedEmployeeInfoService.getActiveEmpIds().iterator().next();
        int supId = essCachedEmployeeInfoService.getEmployee(empId).getSupervisorId();

        //create request
        LocalDate startDate = LocalDate.now();
        TimeOffRequest request = new TimeOffRequest(empId, supId, TimeOffStatus.APPROVED,
                 startDate,  tomorrow, new ArrayList<>(), new ArrayList<>() );

        //Add and get back request
        int requestId = sqlTimeOffRequestDao.updateRequest(request);
        request.setRequestId(requestId);
        essTimeOffRequestService.updateRequest(request);
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
        int supId = essCachedEmployeeInfoService.getEmployee(empId).getSupervisorId();
        essCachedEmployeeInfoService.getEmployee(empId).isActive();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();
        TimeOffRequest request = new TimeOffRequest(empId, supId, TimeOffStatus.APPROVED,
                startDate,  endDate, null, null );

        //Add and get back request
        int requestId = sqlTimeOffRequestDao.updateRequest(request);
        TimeOffRequest retrievedRequest = sqlTimeOffRequestDao.getRequestById(requestId);

        //verify the request is not active
        //boolean isActive = essTimeOffRequestService.isActive(retrievedRequest);
        //assertFalse("Employee is not active, request should not be active.", isActive);
    }

    /* ***Test the main methods*** */

    @Test
    public void getRequestByIdTest() {
        //add a request and then retrieve it using the service
        LocalDate today = LocalDate.now();
        TimeOffRequest request = new TimeOffRequest(123, 456, TimeOffStatus.APPROVED,
                today, today, new ArrayList<>(), new ArrayList<>() );
        int requestId = sqlTimeOffRequestDao.updateRequest(request);
        request.setRequestId(requestId);
        essTimeOffRequestService.updateRequest(request);
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
    public void updateRequestTest() {
        LocalDate date = LocalDate.now();
        TimeOffRequest request = new TimeOffRequest(123, 456,
                TimeOffStatus.SAVED, date, date, null, null);
        int requestId = sqlTimeOffRequestDao.updateRequest(request);
        request.setRequestId(requestId);
        request.setStatus(TimeOffStatus.SUBMITTED);
        essTimeOffRequestService.updateRequest(request);
        request = sqlTimeOffRequestDao.getRequestById(requestId);
        assertEquals("The status of the request was not properly updated.",
                TimeOffStatus.SUBMITTED, request.getStatus());
    }

    @Test
    public void updateNewRequestTest() {
        LocalDate today = LocalDate.now();
        Range<LocalDate> range = Range.closed(today, today);
        TimeOffRequest request = new TimeOffRequest( 123, 456,
                TimeOffStatus.SAVED, today, today, new ArrayList<>(), new ArrayList<>());
        int requestsBefore = sqlTimeOffRequestDao.getAllRequestsByEmpId(123, range).size();
        int requestId = essTimeOffRequestService.updateRequest(request);
        int requestsAfter = sqlTimeOffRequestDao.getAllRequestsByEmpId(123, range).size();
        assertTrue("Request was not added.", requestId != -1);

        assertEquals("Request was not added to the database.", 1,requestsAfter-requestsBefore);
    }

    @Test
    public void getAllRequestsForEmpDateRange() {
        int empId = essCachedEmployeeInfoService.getActiveEmpIds().iterator().next();

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        Range<LocalDate> range = Range.closed(today, today);

        TimeOffRequest requestOne = new TimeOffRequest(empId, 456,
                TimeOffStatus.SAVED, yesterday, yesterday, new ArrayList<>(), new ArrayList<>());
        TimeOffRequest requestTwo= new TimeOffRequest(empId, 456,
                TimeOffStatus.SUBMITTED, today, today, new ArrayList<>(), new ArrayList<>());

        int numBefore = essTimeOffRequestService.getAllRequestForEmpDateRange(empId, range).size();
        essTimeOffRequestService.updateRequest(requestOne);
        essTimeOffRequestService.updateRequest(requestTwo);
        int numAfter = essTimeOffRequestService.getAllRequestForEmpDateRange(empId, range).size();

        assertEquals("One more request should have been returned after updating.",
                1,numAfter-numBefore);
    }

    @Test
    public void getRequestsNeedingApprovalTest() {

        int empId = essCachedEmployeeInfoService.getActiveEmpIds().iterator().next();

        LocalDate today = LocalDate.now();
        TimeOffRequest request = new TimeOffRequest(empId, 456,
                TimeOffStatus.SAVED, today, today, new ArrayList<>(), new ArrayList<>());
        //this request needs approval (i.e., it's status is submitted)
        TimeOffRequest requestApproval = new TimeOffRequest(empId, 456,
                TimeOffStatus.SUBMITTED, today, today, new ArrayList<>(), new ArrayList<>());

        int requestsBefore = essTimeOffRequestService.getRequestsNeedingApproval(456).size();

        int requestId = sqlTimeOffRequestDao.updateRequest(request);
        int requestApprovalId = sqlTimeOffRequestDao.updateRequest(requestApproval);
        request.setRequestId(requestId);
        requestApproval.setRequestId(requestApprovalId);
        //update requests so they have timestamps
        // (Currently they don't because we added them with the DAO)
        essTimeOffRequestService.updateRequest(request);
        essTimeOffRequestService.updateRequest(requestApproval);

        List<TimeOffRequest> requests = essTimeOffRequestService.getRequestsNeedingApproval(456);
        int requestsAfter = requests.size();

        assertEquals("One more request should have been returned.", 1, requestsAfter - requestsBefore);
    }

    @Test
    public void getActiveRequestsForSupTest() {
        int supId = 456;

        //get dates for today and tomorrow
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();

        //get three active employee
        Iterator<Integer> itr = essCachedEmployeeInfoService.getActiveEmpIds().iterator();
        int empIdOne = itr.next();
        int empIdTwo = itr.next();
        int empIdThree = itr.next();

        //create an active request for each employee
        TimeOffRequest requestOne = new TimeOffRequest(empIdOne, supId,
                TimeOffStatus.APPROVED, today, today, new ArrayList<>(), new ArrayList<>());
        TimeOffRequest requestTwo = new TimeOffRequest(empIdTwo, supId,
                TimeOffStatus.APPROVED, today.plusDays(5), today.plusDays(5), new ArrayList<>(), new ArrayList<>());
        TimeOffRequest requestThree = new TimeOffRequest(empIdThree, supId,
                TimeOffStatus.APPROVED, today.plusDays(365), today.plusDays(365), new ArrayList<>(), new ArrayList<>());
        //create an inactive request
        TimeOffRequest requestInactive = new TimeOffRequest(empIdOne, supId,
                TimeOffStatus.APPROVED, yesterday, yesterday, new ArrayList<>(), new ArrayList<>());

        int numBefore = essTimeOffRequestService.getActiveRequestsForSup(supId).size();

        //Add the four requests
        int requestIdOne = sqlTimeOffRequestDao.updateRequest(requestOne);
        int requestIdTwo = sqlTimeOffRequestDao.updateRequest(requestTwo);
        int requestIdThree = sqlTimeOffRequestDao.updateRequest(requestThree);
        int requestIdInactive = sqlTimeOffRequestDao.updateRequest(requestInactive);
        requestOne.setRequestId(requestIdOne);
        requestTwo.setRequestId(requestIdTwo);
        requestThree.setRequestId(requestIdThree);
        requestInactive.setRequestId(requestIdInactive);
        //update requests so they have timestamps
        // (Currently they don't because we added them with the DAO)
        essTimeOffRequestService.updateRequest(requestOne);
        essTimeOffRequestService.updateRequest(requestTwo);
        essTimeOffRequestService.updateRequest(requestThree);
        essTimeOffRequestService.updateRequest(requestInactive);

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