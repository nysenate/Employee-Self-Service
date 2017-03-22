var essTime = angular.module('essTime');

essTime.controller('EmpRecordHistoryCtrl', ['$scope', '$q', '$timeout',
                                            'appProps', 'modals', 'RecordUtils', 'supEmpGroupService',
                                            'ActiveYearsTimeRecordsApi', 'TimeRecordApi',
                                            'AttendanceRecordApi', 'SupervisorEmployeesApi',
    empRecordHistoryCtrl]);

function empRecordHistoryCtrl($scope, $q, $timeout, appProps, modals, recordUtils, supEmpGroupService,
                              ActiveYearsTimeRecordsApi, TimeRecordsApi,
                              AttendanceRecordApi, SupervisorEmployeesApi) {

    $scope.state = {
        supId: appProps.user.employeeId,
        searching: false,
        request: {
            empGroups: false,
            tRecYears: false,
            records: false
        },
        todayMoment: moment(),

        selectedEmp: {},
        recordYears: [],
        selectedRecYear: -1,
        // The start and end dates for the period where the employee was under the viewing supervisor
        //  within the selected year!
        supStartDate: null,
        supEndDate: null,
        records: [],
        timesheetMap: {},
        timesheetRecords: [],
        attendRecords: []
    };

    /* --- Watches --- */

    $scope.$watchCollection('state.selectedEmp', getTimeRecordYears);

    $scope.$watch('state.selectedRecYear', getRecords);

    /* --- Init --- */

    function init() {
        $scope.state.request.empGroups = true;
        supEmpGroupService.init()
            .finally(function () {
                $scope.state.request.empGroups = false;
            });
    }
    init();

    /* --- API request methods --- */

    function getTimeRecordYears () {
        var emp = $scope.state.selectedEmp;
        if (!emp.empId) {
            return;
        }

        $scope.state.selectedRecYear = -1;
        $scope.state.request.tRecYears = true;
        return ActiveYearsTimeRecordsApi.get({empId: emp.empId}, function(resp) {
            if (resp.success) {
                var isUserSup = emp.supId === $scope.state.supId;
                var startDate = isUserSup ? emp.supStartDate : emp.effectiveStartDate;
                var endDate = isUserSup ? emp.supEndDate : emp.effectiveEndDate;
                var supStartYear = moment(startDate || 0).year();
                var supEndYear = moment(endDate || undefined).year();
                $scope.state.recordYears = resp.years
                // Only use years that overlap with supervisor dates
                    .filter(function(year) { return year >= supStartYear && year <= supEndYear; })
                    .reverse();
                $scope.state.selectedRecYear = $scope.state.recordYears[0];
            }
        }, function(resp) {
            modals.open('500', {details: resp});
            console.log(resp);
        }).$promise.finally(function () {
            $scope.state.request.tRecYears = false;
        });
    }

    function getRecords () {
        var emp = $scope.state.selectedEmp;
        if (!emp.empId) {
            return;
        }

        var year = $scope.state.selectedRecYear;
        if (year < 0) {
            return;
        }

        $scope.state.records = [];
        $scope.state.attendRecords = [];
        $scope.state.timesheetRecords = [];

        // Initialize effective supervisor dates
        var isUserSup = emp.supId === $scope.state.supId;
        var supStartDate = isUserSup ? emp.supStartDate : emp.effectiveStartDate
        var supEndDate = isUserSup ? emp.supEndDate : emp.effectiveEndDate;

        var startMoment = moment([year, 0, 1]);
        var endMoment = ($scope.state.todayMoment.year() == year) ? moment() : moment([year, 11, 31]);

        // Do not fetch records if this year does not overlap with supervisor dates
        if (startMoment.isAfter(supEndDate || undefined) ||
            endMoment.isBefore(supStartDate || 0))
        {
            $scope.state.records = [];
            return;
        }
        // Restrict retrieval range based on effective supervisor dates
        $scope.state.supStartDate = moment.max(startMoment, moment(supStartDate || 0));
        $scope.state.supEndDate = moment.min(endMoment, moment(supEndDate || undefined));

        $scope.state.request.records = true;
        $q.all([
            getTimesheetRecords(),
            getAttendRecords()
        ]).then(function () {
            initTimesheetRecords();
            initAttendRecords();
            combineRecords();
        }).finally(function () {
            $scope.state.request.records = false;
        });
    }

    function getTimesheetRecords () {
        var emp = $scope.state.selectedEmp;
        var params = {
            empId: emp.empId,
            from: $scope.state.supStartDate.format('YYYY-MM-DD'),
            to: $scope.state.supEndDate.format('YYYY-MM-DD')
        };
        return TimeRecordsApi.get(params,
            function(resp) {
                $scope.state.timesheetRecords = (resp.result.items[emp.empId] || []).reverse();
                console.debug('got timesheet records', $scope.state.timesheetRecords);
            }, $scope.handleErrorResponse
            ).$promise;
    }

    function getAttendRecords () {
        var emp = $scope.state.selectedEmp;
        var params = {
            empId: emp.empId,
            from: $scope.state.supStartDate.format('YYYY-MM-DD'),
            to: $scope.state.supEndDate.format('YYYY-MM-DD')
        };
        return AttendanceRecordApi.get(params, function (response) {
            console.debug('got attendance records', response.records);
            $scope.state.attendRecords = response.records;
        }, $scope.handleErrorResponse
        ).$promise;
    }

    /* --- Display Methods --- */

    // Open a new modal window showing a detailed view of the given record
    $scope.showDetails = function(record) {
        // Do not display details for paper timesheet record
        if (record.paperTimesheet) {
            return;
        }
        var params = { record: record };
        modals.open('record-details', params, true );
    };

    $scope.isLoading = function () {
        for (var request in $scope.state.request) {
            if ($scope.state.request.hasOwnProperty(request) &&
                    $scope.state.request[request] === true) {
                return true;
            }
        }
        return false;
    };

    /* --- Internal Methods --- */

    /**
     * Initialize timesheet records by calculating totals
     * Add timesheets to timesheet id -> sheet map
     */
    function initTimesheetRecords () {
        $scope.state.timesheetMap = {};
        angular.forEach($scope.state.timesheetRecords, function (record) {
            recordUtils.calculateDailyTotals(record);
            record.totals = recordUtils.getRecordTotals(record);
            $scope.state.timesheetMap[record.timeRecordId] = record;
        });
    }

    /**
     * Initialize attendance records:
     *  filter out records that are covered by electronic timesheets
     *  format records to make them compatible with electronic timesheet totals
     *  add records to displayed records list
     */
    function initAttendRecords () {
        $scope.state.attendRecords.forEach(recordUtils.formatAttendRecord);
    }

    /**
     * Combine queried timesheets and attend records
     */
    function combineRecords() {
        $scope.state.records = [];
        $scope.state.paperTimesheetsDisplayed = false;
        // Stores the latest end date for all queried attend records
        var attendEnd = moment('1970-01-01T00:00:00');

        // Go through all attend records and add any associated timesheets
        // Add the attend record itself if has no timesheet references
        angular.forEach($scope.state.attendRecords, function (attendRecord) {
            if (moment(attendRecord.endDate).isAfter(attendEnd)) {
                attendEnd = attendRecord.endDate;
            }
            if (attendRecord.timesheetIds.length === 0) {
                $scope.state.paperTimesheetsDisplayed = true;
                $scope.state.records.push(attendRecord);
                return;
            }
            angular.forEach(attendRecord.timesheetIds, function (tsId) {
                var record = $scope.state.timesheetMap[tsId];
                if (!record) {
                    console.error('Could not find timesheet with id:', tsId);
                    return;
                }
                $scope.state.records.push(record);
            })
        });

        // Add any timesheets with end dates > the last attend record end date
        angular.forEach($scope.state.timesheetRecords, function (timesheet) {
            if (moment(timesheet.endDate).isAfter(attendEnd)) {
                $scope.state.records.push(timesheet)
            }
        });

        // sort records in reverse chronological order
        $scope.state.records
            .sort(recordUtils.compareRecords)
            .reverse();
    }
}
