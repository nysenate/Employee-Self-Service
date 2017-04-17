package gov.nysenate.ess.time.controller.api;

import com.google.common.collect.*;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.client.view.base.MapView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.core.util.ShiroUtils;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.time.client.response.InvalidTimeRecordResponse;
import gov.nysenate.ess.time.client.view.attendance.TimeRecordCreationNotPermittedData;
import gov.nysenate.ess.time.client.view.attendance.TimeRecordNotFoundData;
import gov.nysenate.ess.time.client.view.attendance.TimeRecordView;
import gov.nysenate.ess.time.dao.attendance.AttendanceDao;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;
import gov.nysenate.ess.time.model.attendance.TimeRecordScope;
import gov.nysenate.ess.time.model.attendance.TimeRecordStatus;
import gov.nysenate.ess.time.model.auth.EssTimePermission;
import gov.nysenate.ess.time.model.personnel.SupervisorException;
import gov.nysenate.ess.time.service.accrual.AccrualInfoService;
import gov.nysenate.ess.time.service.attendance.TimeRecordManager;
import gov.nysenate.ess.time.service.attendance.TimeRecordNotFoundEidBeginDateEx;
import gov.nysenate.ess.time.service.attendance.TimeRecordNotFoundException;
import gov.nysenate.ess.time.service.attendance.TimeRecordService;
import gov.nysenate.ess.time.service.attendance.validation.InvalidTimeRecordException;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordCreationNotPermittedEx;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordCreationValidator;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordValidationService;
import gov.nysenate.ess.time.service.notification.RecordReminderEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gov.nysenate.ess.time.model.auth.SimpleTimePermission.TIME_RECORD_MANAGEMENT;
import static gov.nysenate.ess.time.model.auth.TimePermissionObject.*;
import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/timerecords")
public class TimeRecordRestApiCtrl extends BaseRestApiCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(TimeRecordRestApiCtrl.class);

    @Autowired EmployeeInfoService employeeInfoService;
    @Autowired PayPeriodService periodService;
    @Autowired TimeRecordService timeRecordService;
    @Autowired AccrualInfoService accrualInfoService;
    @Autowired TimeRecordManager timeRecordManager;

    @Autowired AttendanceDao attendanceDao;

    @Autowired TimeRecordValidationService validationService;
    @Autowired TimeRecordCreationValidator creationValidator;

    @Autowired RecordReminderEmailService emailService;

    /**
     * Get Time Record API
     * -------------------
     *
     * Get time records for one or more employees:
     * (GET) /api/v1/timerecords[.json]
     *
     * Request Parameters: empId - int[] - required - Records will be retrieved for these employee ids
     *                     to - Date - default current date - Gets time records that end before or on this date
     *                     from - Date - default Jan 1 on year of 'to' Date - Gets time records that begin on or after this date
     *                     status - String[] - default all statuses - Will only get time records with one of these statuses
     */
    @RequestMapping(value = "", method = {GET, HEAD}, produces = "application/json")
    public BaseResponse getRecordsJson(@RequestParam Integer[] empId,
                                       @RequestParam(required = false) String from,
                                       @RequestParam(required = false) String to,
                                       @RequestParam(required = false) String[] status) {
        /**
         * permission check occurs in {@link TimeRecordRestApiCtrl#getRecords(Set, Range, Set)}
         */
        return getRecordResponse(
                getRecords(empId, from, to, status), false);
    }

    /**
     * Get Active Time Record API
     * --------------------------
     *
     * @param empId Integer
     * @param scope String (accepted values are 'E', 'S', 'P', for employee, supervisor, and personnel respectively.
     * @return TimeRecord ListView Response
     */
    @RequestMapping(value = "/active", method = {GET, HEAD}, produces = "application/json")
    public BaseResponse getActiveRecords(@RequestParam Integer[] empId,
                                         @RequestParam(required = false) String[] scope) {
        Arrays.stream(empId)
                .map(eId -> new EssTimePermission(eId, TIME_RECORDS, GET, LocalDate.now()))
                .forEach(this::checkPermission);

        Set<TimeRecordScope> scopes = (scope != null)
            ? Stream.of(scope).map(TimeRecordScope::getScopeFromCode).collect(Collectors.toSet())
            : Sets.newHashSet(TimeRecordScope.EMPLOYEE, TimeRecordScope.SUPERVISOR);
        ListMultimap<Integer, TimeRecord> activeRecsPerEmp = ArrayListMultimap.create();
        Set<Integer> empIdSet = new HashSet<>(Arrays.asList(empId));
        empIdSet.forEach(eid ->
            activeRecsPerEmp.putAll(eid, timeRecordService.getActiveTimeRecords(eid).stream()
                .filter(tr -> scopes.contains(tr.getRecordStatus().getScope()))
                .collect(toList())));
        return getRecordResponse(activeRecsPerEmp, false);
    }

    /**
     * Get Time Record Years API
     * -------------------------
     *
     * Returns the years during which the given employee has at least one time record during.
     *
     * Request Params: empId - employeeId
     */
    @RequestMapping(value = "activeYears")
    public BaseResponse getTimeRecordYears(@RequestParam Integer empId) {
        checkPermission(new EssTimePermission(empId, TIME_RECORD_ACTIVE_YEARS, GET, LocalDateTime.now()));
        SortedSet<Integer> timeRecordYears = new TreeSet<>();
        timeRecordYears.addAll(attendanceDao.getAttendanceYears(empId));
        timeRecordYears.addAll(timeRecordService.getTimeRecordYears(empId, SortOrder.ASC));
        return ListViewResponse.ofIntList(new ArrayList<>(timeRecordYears), "years");
    }

    /**
     * Get Active Supervisor Record Count API
     * --------------------------------------
     * Get the number of records needing action for a specific supervisor
     *
     * Usage:       (GET) /api/v1/timerecords/supervisor/count
     *
     * Request Params:
     * @param supId int - supervisor id
     * @param from String - ISO 8601 Date formatted
     * @param to String - ISO 8601 Date formatted
     * @param status String - {@link TimeRecordStatus}
     * @return ViewObjectResponse
     */
    @RequestMapping(value = "/supervisor/count", method = {GET, HEAD}, produces = "application/json")
    public BaseResponse getActiveSupervisorRecordCount(@RequestParam int supId,
                                                       @RequestParam(required = false) String from,
                                                       @RequestParam(required = false) String to,
                                                       @RequestParam(required = false) String[] status) {
        Range<LocalDate> dateRange = parseDateRange(from, to);

        checkPermission(new EssTimePermission(supId, SUPERVISOR_TIME_RECORDS, GET, dateRange));

        Set<TimeRecordStatus> statuses = parseStatuses(status, TimeRecordStatus.inProgress());
        return new ViewObjectResponse<>(new ViewObject() {
            public Integer getCount() throws SupervisorException {
                return timeRecordService.getActiveSupervisorRecords(supId, dateRange, statuses).size();
            }
            @Override
            public String getViewType() {
                return "supervisor record count";
            }
        });
    }

    /**
     * Get Active Supervisor Record
     * ----------------------------
     *
     * @param supId
     * @param from
     * @param to
     * @param status
     * @return
     * @throws SupervisorException
     */
    @RequestMapping(value = "/supervisor", method = {GET, HEAD}, produces = "application/json")
    public BaseResponse getActiveSupervisorRecords(@RequestParam int supId,
                                                   @RequestParam(required = false) String from,
                                                   @RequestParam(required = false) String to,
                                                   @RequestParam(required = false) String[] status)
            throws SupervisorException {
        Range<LocalDate> dateRange = parseDateRange(from, to);

        checkPermission(new EssTimePermission(supId, SUPERVISOR_TIME_RECORDS, GET, dateRange));

        Set<TimeRecordStatus> statuses = parseStatuses(status, TimeRecordStatus.inProgress());
        return getRecordResponse(timeRecordService.getActiveSupervisorRecords(supId, dateRange, statuses), false);
    }

    /**
     * Send Time Record Reminder
     * -------------------------
     * Send email reminders to employees requesting that they submit a time record
     *
     * Usage:       (POST) /api/3/timerecords/reminder
     *
     * Request Params:
     * <code>empId</code> and <code>beginDate</code> will be multi-mapped together using common indexes
     * e.g. <code>{empId: [11423, 11168], beginDate: ['2016-07-28', '2016-08-11', '2016-08-11']}</code>
     *  will result in <code>{11423: ['2016-07-28', '2016-08-11'], 11168: ['2016-08-11']}</code>
     * This will send an email to each employee for the records with the begin dates mapped to that employee
     * @param empId Integer[] - employee ids
     * @param beginDate String[] - ISO 8601 formatted date - time record begin dates
     * @return {@link SimpleResponse} indicating message send success
     */
    @RequestMapping(value = "/reminder", method = POST)
    public BaseResponse sendReminderEmails(@RequestParam Integer[] empId,
                                           @RequestParam String[] beginDate) {
        // Convert array parameters to usable forms
        List<Integer> empIdList = Arrays.asList(empId);
        List<LocalDate> beginDateList = Arrays.stream(beginDate)
                .map(dateString -> parseISODate(dateString, "beginDate"))
                .collect(Collectors.toList());

        // Ensure empId -> beginDate mapping is valid
        if (empIdList.size() != beginDateList.size()) {
            throw new InvalidRequestParamEx(
                    OutputUtils.toJson(ImmutableMap.of("empId", empId, "beginDate", beginDate)),
                    "empId, beginDate", "Integer, String",
                    "must pass the same number of 'empId' and 'beginDate' parameters"
            );
        }

        Multimap<Integer, LocalDate> empIdDateMap = TreeMultimap.create();

        // Simultaneously iterate through employee ids and begin dates to check permissions
        // and organize parameters into a map
        Iterator<Integer> empIdIterator = empIdList.iterator();
        Iterator<LocalDate> beginDateIterator = beginDateList.iterator();
        while (empIdIterator.hasNext() && beginDateIterator.hasNext()) {
            Integer eId = empIdIterator.next();
            LocalDate bDate = beginDateIterator.next();
            // Check for notification permissions for each time record that will be included in the notification
            checkPermission(new EssTimePermission(eId, TIME_RECORD_NOTIFICATION, POST, bDate));
            // add id and date to map
            empIdDateMap.put(eId, bDate);
        }

        emailService.sendEmailReminders(
                ShiroUtils.getAuthenticatedEmpId(), empIdDateMap);

        return new SimpleResponse(true, "Sent time record email reminders", "time-record-reminder-success");
    }

    /**
     * Create Time Record API
     * --------------------
     *
     * Create a new time record for the given pay period:
     *      (POST) /api/v1/timerecords/new
     *
     * @param empId int - employee id
     * @param date String - iso date formatted - will select the pay period containing this date
     * @throws TimeRecordCreationNotPermittedEx - if a record cannot be created for the given period
     */
    @RequestMapping(value = "/new", method = RequestMethod.POST, consumes = "application/json")
    public BaseResponse createNewRecord(@RequestParam int empId, @RequestParam String date)
            throws TimeRecordCreationNotPermittedEx {
        LocalDate parsedDate = parseISODate(date, "date");
        PayPeriod period = periodService.getPayPeriod(PayPeriodType.AF, parsedDate);

        // If the subject is not permitted to use the time record manager, require employee record post permissions
        // And run validation on request
        if (!getSubject().isPermitted(TIME_RECORD_MANAGEMENT.getPermission())) {
            checkPermission(new EssTimePermission(empId, TIME_RECORDS, POST, period.getDateRange()));
            creationValidator.validateRecordCreation(empId, period);
        }

        timeRecordManager.ensureRecords(empId, Collections.singleton(period));

        return new SimpleResponse(true, "time record created", "next-record-created");
    }

    /**
     * Save Time Record API
     * --------------------
     *
     * Save a time record:
     *      (POST) /api/v1/timerecords
     *
     * Post Data: json TimeRecordView
     */
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
    public void saveRecord(@RequestBody TimeRecordView record,
                           @RequestParam(defaultValue = "SAVE") String action) {
        TimeRecordAction timeRecordAction = getEnumParameter("action", action, TimeRecordAction.class);
        TimeRecord newRecord = record.toTimeRecord();

        checkPermission(new EssTimePermission(record.getEmployeeId(), TIME_RECORDS, POST, newRecord.getDateRange()));

        validationService.validateTimeRecord(newRecord, timeRecordAction);
        timeRecordService.saveRecord(newRecord, timeRecordAction);
    }

    /**
     * Review Time Record API
     * ----------------------
     *
     * Review a time record:
     *     (POST) /api/v1/timerecords/review
     *
     * Request Params:
     * @param timeRecordId Integer - id of the reviewed time record
     * @param remarks - String - any remarks attached to the review
     * @param action - String - {@link TimeRecordAction} - action to take on the record
     */
    @RequestMapping(value = "/review", method = POST)
    public void reviewRecord(@RequestParam BigInteger timeRecordId,
                             @RequestParam(required = false) String remarks,
                             @RequestParam String action) {
        TimeRecord record = timeRecordService.getTimeRecord(timeRecordId);

        checkPermission(new EssTimePermission(record.getEmployeeId(), TIME_RECORDS, POST, record.getDateRange()));

        TimeRecordAction timeRecordAction = getEnumParameter("action", action, TimeRecordAction.class);
        if (timeRecordAction == TimeRecordAction.SAVE) {
            throw new InvalidRequestParamEx(action,
                    "action", "String", "action != SAVE");
        }

        record.setRemarks(remarks);
        validationService.validateTimeRecord(record, timeRecordAction);
        timeRecordService.saveRecord(record, timeRecordAction);
    }

    /**
     * Handle cases where an invalid time record is posted
     * Return a response indicating time record errors
     * @param ex {@link InvalidTimeRecordException}
     * @return {@link InvalidTimeRecordResponse}
     */
    @ExceptionHandler(InvalidTimeRecordException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public BaseResponse handleInvalidTimeRecordException(InvalidTimeRecordException ex) {
        return new InvalidTimeRecordResponse(getTimeRecordView(ex.getTimeRecord()), ex.getDetectedErrors());
    }

    /**
     * Handle cases where a requested time record was not found based on empId and begin Date
     * @param ex {@link TimeRecordNotFoundEidBeginDateEx}
     * @return {@link ViewObjectErrorResponse}
     */
    @ExceptionHandler(TimeRecordNotFoundEidBeginDateEx.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public BaseResponse handleTimeRecordNotFoundEidBeginDateEx(TimeRecordNotFoundEidBeginDateEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.TIME_RECORD_NOT_FOUND, new TimeRecordNotFoundData(ex));
    }

    /**
     * Handle cases where a specifically requested time record was not found
     * @param ex {@link TimeRecordNotFoundException}
     * @return {@link ViewObjectErrorResponse}
     */
    @ExceptionHandler(TimeRecordNotFoundException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public BaseResponse handleTimeRecordNotFoundEx(TimeRecordNotFoundException ex) {
        return new ViewObjectErrorResponse(ErrorCode.TIME_RECORD_NOT_FOUND,
                Objects.toString(ex.getTimeRecordId()));
    }

    /**
     * Handle cases where a specifically requested time record was not found
     * @param ex {@link TimeRecordCreationNotPermittedEx}
     * @return {@link ViewObjectErrorResponse}
     */
    @ExceptionHandler(TimeRecordCreationNotPermittedEx.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public BaseResponse handleTimeRecordCreationNotPermittedEx(TimeRecordCreationNotPermittedEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.CANNOT_CREATE_NEW_RECORD,
                new TimeRecordCreationNotPermittedData(ex));
    }

    /** --- Internal Methods --- */

    private ListMultimap<Integer, TimeRecord> getRecords(Set<Integer> empIds, Range<LocalDate> dateRange,
                                                         Set<TimeRecordStatus> statuses) {
        empIds.forEach(empId -> checkPermission(new EssTimePermission(empId, TIME_RECORDS, GET, dateRange)));

        ListMultimap<Integer, TimeRecord> records = LinkedListMultimap.create();
        timeRecordService.getTimeRecords(empIds, dateRange, statuses)
                .forEach(record -> records.put(record.getEmployeeId(), record));
        return records;
    }

    private ListMultimap<Integer, TimeRecord> getRecords(Integer[] empId, String from, String to, String[] status) {
        return getRecords(new HashSet<>(Arrays.asList(empId)), parseDateRange(from, to), parseStatuses(status));
    }

    private Range<LocalDate> parseDateRange(String from, String to) {
        LocalDate toDate = to != null ? parseISODate(to, "to") : LocalDate.now().plusDays(1);
        LocalDate fromDate = from != null ? parseISODate(from, "from") : LocalDate.of(toDate.getYear(), 1, 1);
        return getClosedOpenRange(fromDate, toDate, "from", "to");
    }

    private Set<TimeRecordStatus> parseStatuses(String[] status, Set<TimeRecordStatus> defaultValue) {
        if (status != null && status.length > 0) {
            return Arrays.asList(status).stream()
                    .map(recordStatus -> getEnumParameter("status", recordStatus, TimeRecordStatus.class))
                    .collect(Collectors.toSet());
        }
        return defaultValue;
    }

    private Set<TimeRecordStatus> parseStatuses(String[] status) {
        return parseStatuses(status, EnumSet.allOf(TimeRecordStatus.class));
    }

    /**
     * Construct a json or xml response from a timerecord multimap.  The response consists of a map of employee ids to
     * time records
     *
     * @param records ListMultimap<Integer, TimeRecord> records
     * @param xml boolean
     * @param supervisor boolean
     * @return ViewObjectResponse
     */
    private ViewObjectResponse<?> getRecordResponse(ListMultimap<Integer, TimeRecord> records, boolean supervisor, boolean xml) {
        Set<Integer> empIdSet = new HashSet<>();
        records.values().stream().forEach(tr -> {
            empIdSet.add(tr.getEmployeeId());
            empIdSet.add(tr.getSupervisorId());
        });
        Map<Integer, Employee> empMap = employeeInfoService.getEmployees(empIdSet);
        return new ViewObjectResponse<>(MapView.of(
                records.keySet().stream()
                        .map(id -> new AbstractMap.SimpleEntry<>((xml) ? (supervisor ? "sup" : "emp") + "Id-" + id : id,
                                        ListView.of(records.get(id).stream()
                                                .sorted()
                                                .map(tr -> new TimeRecordView(tr, empMap.get(tr.getEmployeeId()), empMap.get(tr.getSupervisorId())))
                                                .collect(toList())))
                        )
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        ));
    }

    private ViewObjectResponse<?> getRecordResponse(ListMultimap<Integer, TimeRecord> records, boolean supervisor) {
        return getRecordResponse(records, supervisor, false);
    }

    private TimeRecordView getTimeRecordView(TimeRecord record) {
        return new TimeRecordView(record,
                employeeInfoService.getEmployee(record.getEmployeeId()),
                employeeInfoService.getEmployee(record.getSupervisorId()));
    }
}