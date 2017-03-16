var essTime = angular.module('essTime');

essTime.controller('EmpRecordHistoryCtrl', ['$scope', '$q', '$timeout', 'appProps',  'ActiveYearsTimeRecordsApi', 'TimeRecordApi',
                                            'AttendanceRecordApi', 'SupervisorEmployeesApi', 'modals', 'RecordUtils',
    empRecordHistoryCtrl]);

function empRecordHistoryCtrl($scope, $q, $timeout, appProps, ActiveYearsTimeRecordsApi, TimeRecordsApi,
          AttendanceRecordApi, SupervisorEmployeesApi, modals, recordUtils) {

    $scope.state = {
        supId: appProps.user.employeeId,
        searching: false,
        request: {
            empGroups: false,
            tRecYears: false,
            records: false
        },
        todayMoment: moment(),

        iSelEmpGroup: -1,
        iSelEmp: -1,
        recordYears: [],
        selectedRecYear: -1,
        // The start and end dates for the period where the employee was under the viewing supervisor
        //  within the selected year!
        supStartDate: null,
        supEndDate: null,
        records: [],
        timesheetMap: {},
        timesheetRecords: [],
        attendRecords: [],

        nameMap: {},

        extSupEmpGroup: null,
        supEmpGroups: [],

        allEmps: [],
        primaryEmps: [],
        primarySups: []
    };

    /* --- Watches --- */

    $scope.$watch('state.iSelEmpGroup', setActiveSupEmpGroup);

    $scope.$watch('state.iSelEmp', getTimeRecordYears);

    $scope.$watch('state.selectedRecYear', getRecords);

    /* --- API request methods --- */

    $scope.getEmployeeGroups = function(supId, fromDate, toDate) {
        var fromDateMoment = (fromDate) ? moment(fromDate) : moment().subtract(2, 'years');
        var toDateMoment = (toDate) ? moment(toDate) : moment();
        var params = {
            supId: supId,
            fromDate: fromDateMoment.format('YYYY-MM-DD'),
            toDate: toDateMoment.format('YYYY-MM-DD'),
            extended: true
        };
        $scope.state.request.empGroups = true;
        SupervisorEmployeesApi.get(params, handleEmpGroupResponse, $scope.handleErrorResponse)
            .$promise.finally(function () {
                $scope.state.request.empGroups = false;
            });
    };

    function handleEmpGroupResponse (resp) {
        $scope.state.extSupEmpGroup = resp.result;
        setNameMap();
        setSupEmpGroups();
        $scope.state.iSelEmpGroup = 0;
    }

    function getTimeRecordYears () {
        var iSelEmp = $scope.state.iSelEmp;
        if (iSelEmp < 0) {
            return;
        }

        var emp = $scope.state.allEmps[iSelEmp];

        $scope.state.selectedRecYear = -1;
        $scope.state.request.tRecYears = true;
        return ActiveYearsTimeRecordsApi.get({empId: emp.empId}, function(resp) {
            if (resp.success) {
                var supStartYear = emp.supStartMoment.year();
                var supEndYear = emp.supEndMoment.year();
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
        var iSelEmp = $scope.state.iSelEmp;
        if (iSelEmp < 0) {
            return;
        }
        var emp = $scope.state.allEmps[iSelEmp];

        var year = $scope.state.selectedRecYear;
        if (year < 0) {
            return;
        }

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

        $scope.state.request.records = true;
        $q.all([
            $scope.getTimesheetRecords(),
            $scope.getAttendRecords()
        ]).then(function () {
            initTimesheetRecords();
            initAttendRecords();
            combineRecords();
        }).finally(function () {
            $scope.state.request.records = false;
        });
    }

    $scope.getTimesheetRecords = function() {
        var emp = $scope.state.allEmps[$scope.state.iSelEmp];
        var params = {
            empId: emp.empId,
            from: $scope.state.supStartDate.format('YYYY-MM-DD'),
            to: $scope.state.supEndDate.format('YYYY-MM-DD')
        };
        return TimeRecordsApi.get(params,
            function(resp) {
                $scope.state.timesheetRecords = (resp.result.items[emp.empId] || []).reverse();
                console.log('got timesheet records', $scope.state.timesheetRecords);
            }, $scope.handleErrorResponse
            ).$promise;
    };

    $scope.getAttendRecords = function () {
        var emp = $scope.state.allEmps[$scope.state.iSelEmp];
        var params = {
            empId: emp.empId,
            from: $scope.state.supStartDate.format('YYYY-MM-DD'),
            to: $scope.state.supEndDate.format('YYYY-MM-DD')
        };
        return AttendanceRecordApi.get(params, function (response) {
            console.log('got attendance records', response.records);
            $scope.state.attendRecords = response.records;
        }, $scope.handleErrorResponse
        ).$promise;
    };

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

    function setActiveSupEmpGroup() {

        if ($scope.state.iSelEmpGroup < 0) {
            return;
        }

        var selEmpGroup = $scope.state.supEmpGroups[$scope.state.iSelEmpGroup];

        var isUser = selEmpGroup.supId === $scope.state.supId;

        $scope.state.primaryEmps = selEmpGroup.primaryEmployees.sort(function(a,b) {
            return a.empLastName.localeCompare(b.empLastName)});
        // This lookup table maps empId -> last name in case it's needed for the supervisor overrides.
        var empList = [];
        // Add all the employees into a single collection to populate the drop down.
        angular.forEach($scope.state.primaryEmps, function(emp) {
            emp.group = 'Direct employees';
            setAdditionalEmpData(emp);
            empList.push(emp);
        });
        if (isUser) {
            angular.forEach(selEmpGroup.empOverrideEmployees, function (emp) {
                emp.group = 'Additional Employees';
                setAdditionalEmpData(emp);
                empList.push(emp);
            });
            angular.forEach(selEmpGroup.supOverrideEmployees.items, function (supGroup, supId) {
                angular.forEach(supGroup, function (emp) {
                    emp.group = $scope.state.nameMap[supId]
                                ? $scope.state.nameMap[supId].lastName + '\'s Employees'
                                : 'Sup Override Employees';
                    setAdditionalEmpData(emp);
                    empList.push(emp);
                });
            });
        }
        $scope.state.iSelEmp = -1;
        $timeout(function () { // Todo find a better way of triggering record reset
            $scope.state.iSelEmp = 0;
        });
        $scope.state.allEmps = empList;
    }

    function setAdditionalEmpData(emp) {
        emp.supStartMoment = moment(emp.supStartDate || '1970-01-01');
        emp.supEndMoment = moment(emp.supEndDate || '2999-12-31');
        emp.dropDownLabel = emp.empLastName + ' ' + emp.empFirstName[0] + '.' +
            ' (' + emp.supStartMoment.format('MMM YYYY') + ' - ' +
            (emp.supEndMoment.isBefore(moment(), 'day')
                ? emp.supEndMoment.format('MMM YYYY')
                : 'Present')
            + ')';
    }

    function setNameMap() {
        var extSupEmpGroup = $scope.state.extSupEmpGroup;

        var primaryEmpInfos = extSupEmpGroup.primaryEmployees;
        var empOverrideInfos = extSupEmpGroup.empOverrideEmployees;
        var supOverrideInfos = Object.keys(extSupEmpGroup.supOverrideEmployees)
            .map(function (k) { return extSupEmpGroup.supOverrideEmployees[k] });

        var allEmpInfos = primaryEmpInfos.concat(empOverrideInfos).concat(supOverrideInfos);

        var empSupEmpGroupMap = extSupEmpGroup.employeeSupEmpGroups;
        angular.forEach(empSupEmpGroupMap, function (supEmpGroups) {
            angular.forEach(supEmpGroups, function (supEmpGroup) {
                allEmpInfos = allEmpInfos.concat(supEmpGroup.primaryEmployees);
            })
        });

        angular.forEach(allEmpInfos, function (empInfo) {
            $scope.state.nameMap[empInfo.empId] = {
                firstName: empInfo.empFirstName,
                lastName: empInfo.empLastName
            };
        });

        $scope.state.nameMap[$scope.state.supId] = {
            firstName: appProps.user.firstName,
            lastName: appProps.user.lastName
        }
    }

    function setSupEmpGroups() {
        var extSupEmpGroup = $scope.state.extSupEmpGroup;
        var empSupEmpGroups = [];

        var supName = appProps.user.firstName + ' ' + appProps.user.lastName;

        angular.forEach(extSupEmpGroup.employeeSupEmpGroups, function (supEmpGroups) {
            angular.forEach(supEmpGroups, function (empGroup) {
                empGroup.supStartDate = empGroup.effectiveFrom;
                empGroup.supEndDate = empGroup.effectiveTo;
                empGroup.empFirstName = $scope.state.nameMap[empGroup.supId].firstName;
                empGroup.empLastName = $scope.state.nameMap[empGroup.supId].lastName;
                setAdditionalEmpData(empGroup);
                empGroup.group = 'Supervisors Under ' + supName;
                empSupEmpGroups.push(empGroup);
            });
        });

        empSupEmpGroups.sort(function (a, b) {
            var aLabel = a.dropDownLabel;
            var bLabel = b.dropDownLabel;
            if (aLabel < bLabel) {
                return -1;
            }
            if (aLabel > bLabel) {
                return 1;
            }
            return 0;
        });

        extSupEmpGroup.dropDownLabel = supName;

        $scope.state.supEmpGroups = [extSupEmpGroup].concat(empSupEmpGroups);
    }

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

    $scope.init = function() {
        $scope.getEmployeeGroups($scope.state.supId);
    }();
}
