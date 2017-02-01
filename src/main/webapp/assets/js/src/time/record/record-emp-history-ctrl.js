var essTime = angular.module('essTime');

essTime.controller('EmpRecordHistoryCtrl', ['$scope', '$q', 'appProps',  'ActiveYearsTimeRecordsApi', 'TimeRecordApi',
                                            'AttendanceRecordApi', 'SupervisorEmployeesApi', 'modals', 'RecordUtils',
    empRecordHistoryCtrl]);

function empRecordHistoryCtrl($scope, $q, appProps, ActiveYearsTimeRecordsApi, TimeRecordsApi,
          AttendanceRecordApi, SupervisorEmployeesApi, modals, recordUtils) {

    $scope.state = {
        supId: appProps.user.employeeId,
        searching: false,
        todayMoment: moment(),

        selectedEmp: null,
        recordYears: [],
        selectedRecYear: null,
        // The start and end dates for the period where the employee was under the viewing supervisor
        //  within the selected year!
        supStartDate: null,
        supEndDate: null,
        records: [],
        timesheetRecords: [],
        attendRecords: [],

        allEmps: [],
        primaryEmps: []
    };

    $scope.getEmployeeGroups = function(supId, fromDate, toDate) {
        var fromDateMoment = (fromDate) ? moment(fromDate) : moment().subtract(2, 'years');
        var toDateMoment = (toDate) ? moment(toDate) : moment();
        $scope.state.searching = true;
        SupervisorEmployeesApi.get({
            supId: supId,
            fromDate: fromDateMoment.format('YYYY-MM-DD'),
            toDate: toDateMoment.format('YYYY-MM-DD')
        }, function(resp) {
            if (resp.success == true) {
                $scope.state.primaryEmps = resp.result.primaryEmployees.sort(function(a,b) {
                    return a.empLastName.localeCompare(b.empLastName)});
                // This lookup table maps empId -> last name in case it's needed for the supervisor overrides.
                var primaryEmpLookup = {};
                // Add all the employees into a single collection to populate the drop down.
                angular.forEach($scope.state.primaryEmps, function(emp) {
                    emp.group = 'Direct employees';
                    primaryEmpLookup[emp.empId] = emp.empLastName;
                    setAdditionalEmpData(emp);
                    $scope.state.allEmps.push(emp);
                });
                angular.forEach(resp.result.empOverrideEmployees, function(emp) {
                    emp.group = 'Additional Employees';
                    setAdditionalEmpData(emp);
                    $scope.state.allEmps.push(emp);
                });
                angular.forEach(resp.result.supOverrideEmployees.items, function(supGroup, supId) {
                    angular.forEach(supGroup, function(emp) {
                        emp.group = ((primaryEmpLookup[supId]) ? primaryEmpLookup[supId] + '\'s Employees'
                                                               : 'Sup Override Employees');
                        setAdditionalEmpData(emp);
                        $scope.state.allEmps.push(emp);
                    });
                });
                $scope.state.selectedEmp = $scope.state.allEmps[0];
                $scope.getTimeRecordsForEmp($scope.state.selectedEmp);
            }
            $scope.state.searching = false;
        }, function(resp) {
            $scope.state.searching = false;
            modals.open('500', {details: resp});
            console.log(resp);
        });
    };

    function setAdditionalEmpData(emp) {
        emp.supStartMoment = moment(emp.supStartDate || '1970-01-01');
        emp.supEndMoment = moment(emp.supEndDate || undefined);
        emp.dropDownLabel = emp.empLastName + ' ' + emp.empFirstName[0] + '.' +
            ' (' + emp.supStartMoment.format('MMM YYYY') + ' - ' +
            emp.supEndMoment.format('MMM YYYY') + ')';
    }

    $scope.getTimeRecordsForEmp = function(emp) {
        ActiveYearsTimeRecordsApi.get({empId: emp.empId}, function(resp) {
            if (resp.success) {
                var supStartYear = emp.supStartMoment.year();
                var supEndYear = emp.supEndMoment.year();
                $scope.state.recordYears = resp.years
                    // Only use years that overlap with supervisor dates
                    .filter(function(year) { return year >= supStartYear && year <= supEndYear; })
                    .reverse();
                $scope.state.selectedRecYear = $scope.state.recordYears[0];
                if (resp.years.length > 0) {
                    $scope.getRecordsForYear(emp, $scope.state.selectedRecYear);
                }
            }
        }, function(resp) {
            modals.open('500', {details: resp});
            console.log(resp);
        });
    };

    $scope.getRecordsForYear = function (emp, year) {
        
        $scope.state.records = [];
        $scope.state.attendRecords = [];
        $scope.state.timesheetRecords = [];
        
        // Initialize effective supervisor dates
        var startMoment = moment([year, 0, 1]);
        var endMoment = ($scope.state.todayMoment.year() == year) ? moment() : moment([year, 11, 31]);
        // Do not fetch records if this year does not overlap with supervisor dates
        if (startMoment.isAfter(emp.supEndMoment) || endMoment.isBefore(emp.supStartMoment)) {
            $scope.state.records = [];
            return;
        }
        // Restrict retrieval range based on effective supervisor dates
        $scope.state.supStartDate = moment.max(startMoment, emp.supStartMoment);
        $scope.state.supEndDate = moment.min(endMoment, emp.supEndMoment);

        $scope.state.searching = true;
        $q.all([
            $scope.getTimesheetRecords(),
            $scope.getAttendRecords()
        ]).then(function () {
            initTimesheetRecords();
            initAttendRecords();

            // sort records in reverse chronological order
            $scope.state.records
                .sort(recordUtils.compareRecords)
                .reverse();
        }).finally(function () {
            $scope.state.searching = false;
        });
    };

    $scope.getTimesheetRecords = function() {
        var emp = $scope.state.selectedEmp;
        return TimeRecordsApi.get({empId: emp.empId,
                            from: $scope.state.supStartDate.format('YYYY-MM-DD'),
                            to: $scope.state.supEndDate.format('YYYY-MM-DD')},
            function(resp) {
                $scope.state.timesheetRecords = (resp.result.items[emp.empId] || []).reverse();
                console.log('got timesheet records', $scope.state.timesheetRecords);
            }, function(resp) {
                modals.open('500', {details: resp});
                console.log(resp);
        }).$promise;
    };

    /**
     * Initialize timesheet records by calculating totals
     * Add timesheet records to displayed record list
     */
    function initTimesheetRecords () {
        angular.forEach($scope.state.timesheetRecords, function (record) {
            recordUtils.calculateDailyTotals(record);
            record.totals = recordUtils.getRecordTotals(record);
            $scope.state.records.push(record);
        });
    }

    /**
     * Initialize attendance records:
     *  filter out records that are covered by electronic timesheets
     *  format records to make them compatible with electronic timesheet totals
     *  add records to displayed records list
     */
    function initAttendRecords () {
        $scope.state.paperTimesheetsDisplayed = false;
        $scope.state.attendRecords
            .filter(useAttendRecord)
            .map(recordUtils.formatAttendRecord)
            .forEach(function (record) {
                $scope.state.paperTimesheetsDisplayed = true;
                $scope.state.records.push(record);
            });
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

    $scope.getAttendRecords = function (emp, year) {
        var params = {
            empId: $scope.state.selectedEmp.empId,
            from: $scope.state.supStartDate.format('YYYY-MM-DD'),
            to: $scope.state.supEndDate.format('YYYY-MM-DD')
        };
        return AttendanceRecordApi.get(params, function (response) {
            console.log('got attendance records', response.records);
            $scope.state.attendRecords = response.records;
        }, function (errorResponse) {
            modals.open('500', {details: errorResponse});
            console.error(errorResponse);
        }).$promise;
    };

    // Open a new modal window showing a detailed view of the given record
    $scope.showDetails = function(record) {
        // Do not display details for paper timesheet record
        if (record.paperTimesheet) {
            return;
        }
        var params = { record: record };
        modals.open('record-details', params, true );
    };

    $scope.init = function() {
        $scope.getEmployeeGroups($scope.state.supId);
    }();
    
    
}
