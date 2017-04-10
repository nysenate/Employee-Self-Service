var essTime = angular.module('essTime');

essTime.controller('RecordHistoryCtrl', ['$scope', '$q', 'appProps',
                                         'ActiveYearsTimeRecordsApi', 'TimeRecordApi', 'AttendanceRecordApi',
                                         'modals', 'RecordUtils', recordHistoryCtrl]);

function recordHistoryCtrl($scope, $q, appProps, ActiveYearsTimeRecordsApi, timeRecordsApi, attendanceRecordApi, 
                           modals, recordUtils) {

    $scope.state = {
        searching: false,
        recordYears: null,
        year: null,
        timeRecords: [],
        attendRecords: []
    };

    $scope.init = function() {
        var empId = appProps.user.employeeId;
        ActiveYearsTimeRecordsApi.get({empId: empId}, function(resp) {
            $scope.state.recordYears = resp.years.reverse();
            $scope.state.year = $scope.state.recordYears[0];
            $scope.getRecords();
        }, function (resp) {
            modals.open('500', {action: 'Get time record history', details: resp});
            console.error(resp);
        });
    };

    // Settings for floating the time entry table heading
    $scope.floatTheadOpts = {
        scrollingTop: 47
    };

    /** --- API Methods --- */

    /**
     * Gets timesheets and attendance records, and initializes them
     */
    $scope.getRecords = function () {
        $scope.state.searching = true;
        $scope.records = {employee: [], other: []};
        $scope.annualTotals = {};
        $q.all([
            $scope.getTimeRecords(),
            $scope.getAttendRecords()
        ]).then(function () {
            console.log('records retrieved for', $scope.state.year);
        }).catch(function (reason) {
            console.error('error loading time/attendance records', reason)
        }).finally(function () {
            $scope.state.searching = false;
            initializeTimeRecords();
            initializeAttendRecords();
            // Sort records into reverse chronological order
            $scope.records.other
                .sort(recordUtils.compareRecords)
                .reverse();
            console.log($scope.records);
        });
    };

    /**
     * Get all time records for the selected year
     * return a promise that resolves when the records are retrieved
     */
    $scope.getTimeRecords = function() {
        var empId = appProps.user.employeeId;
        var now = moment();
        var fromMoment = moment([$scope.state.year]);
        var toMoment = moment([$scope.state.year + 1]);
        var params = {
            empId: empId,
            from: fromMoment.format('YYYY-MM-DD'),
            to: toMoment.format('YYYY-MM-DD')
        };
        return timeRecordsApi.get(params, function(response) {
            console.log('got time records');
            $scope.state.timesheetRecords = response.result.items[empId];
        }, function(response) {
            modals.open('500', {details: response});
            console.error(response);
        }).$promise;
    };

    /**
     * Get all attendance records for the selected year
     * return a promise that resolves when the records are retrieved
     */
    $scope.getAttendRecords = function() {
        var params = {
            empId: appProps.user.employeeId,
            year: $scope.state.year
        };

        return attendanceRecordApi.get(params, function (response) {
            console.log('got attendance records', response.records);
            $scope.state.attendRecords = response.records;
        }, function (errorResponse) {
            modals.open('500', {details: errorResponse});
            console.error(errorResponse);
        }).$promise;
    };

    /** --- Display Methods --- */

    // Open a new modal window showing a detailed view of the given record
    $scope.showDetails = function(record) {
        // Dont show details for paper timesheet records 
        if (record.paperTimesheet) {
            return;
        }
        var params = {record: record};
        modals.open('record-details', params, true);
    };

    /** --- Internal Methods --- */

    // Calculate totals for each record from a record response and categorize them by scope
    function initializeTimeRecords() {
        var responseRecords = $scope.state.timesheetRecords;
        for(var i in responseRecords) {
            var record = responseRecords[i];

            recordUtils.calculateDailyTotals(record);
            record.totals = recordUtils.getRecordTotals(record);

            if (record.scope === "E") {
                $scope.records.employee.push(record);
            } else {
                addToAnnualTotals(record);
                // Store non employee scope records in reverse chronological order
                $scope.records.other.unshift(record);
            }
        }
    }

    /**
     * Initialize attendance records and add them to the record list
     */
    function initializeAttendRecords() {
        var attendRecords = $scope.state.attendRecords;
        var recordArray = $scope.records.other;
        $scope.state.paperTimesheetsDisplayed = false;
        attendRecords.map(recordUtils.formatAttendRecord)
            .filter(useAttendRecord)
            .forEach(function (attendRec) {
                addToAnnualTotals(attendRec);
                recordArray.unshift(attendRec);
                $scope.state.paperTimesheetsDisplayed = true;
            });
        console.log($scope.records.other);
    }

    /**
     * Determine if an attendance record should be used
     * Ensures there are no associated electronic time records
     * @param attendRec
     * @returns {boolean}
     */
    function useAttendRecord(attendRec) {
        if (!attendRec.paperTimesheet) {
            return false;
        }
        var timeRecords = $scope.state.timesheetRecords;
        for (var iTrec in timeRecords) {
            if (!timeRecords.hasOwnProperty(iTrec)) continue;
            var timeRec = timeRecords[iTrec];
            if (attendRec.beginDate === timeRec.beginDate &&
                attendRec.endDate === timeRec.endDate) {
                return false;
            }
        }
        return true;
    }

    // Add the totals of the given record to the running annual totals
    function addToAnnualTotals(record) {
        for(var field in record.totals) {
            if (record.totals.hasOwnProperty(field)) {
                if (!$scope.annualTotals.hasOwnProperty(field)) {
                    $scope.annualTotals[field] = 0;
                }
                $scope.annualTotals[field] += record.totals[field];
            }
        }
    }

    $scope.init();
}
