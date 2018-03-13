
angular.module('essTime')
    .directive('recordHistory', ['$q',
                               'appProps', 'modals', 'RecordUtils',
                               'ActiveYearsTimeRecordsApi', 'TimeRecordApi',
                               'AttendanceRecordApi', recordHistoryDirective]);

function recordHistoryDirective($q, appProps, modals, recordUtils,
                              ActiveYearsTimeRecordsApi, TimeRecordsApi,
                              AttendanceRecordApi) {
    return {
        scope: {
            /**
             *  An optional employee sup info
             *  If this is present, then records will be displayed for the corresponding employee
             *    for the appropriate dates.
             *  Otherwise, records will be displayed for the logged in user
             */
            empSupInfo: '=?'
        },
        templateUrl: appProps.ctxPath + '/template/time/record/history-directive',
        link: function ($scope, $elem, $attrs) {
            $scope.state = {
                supId: appProps.user.employeeId,
                searching: false,
                request: {
                    tRecYears: false,
                    records: false
                },
                todayMoment: moment(),

                selectedEmp: {},
                empId: null,
                recordYears: [],
                selectedRecYear: -1,
                records: [],
                timesheetMap: {},
                timesheetRecords: [],
                attendRecords: []
            };

            $scope.hideTitle = $attrs.hideTitle === 'true';

            /* --- Watches --- */

            $scope.$watchCollection('empSupInfo', setEmpId);
            $scope.$watchCollection('empSupInfo', getTimeRecordYears);

            $scope.$watch('state.selectedRecYear', getRecords);

            /* --- API request methods --- */

            function getTimeRecordYears () {
                var emp = $scope.state.selectedEmp;

                if (!emp.empId) {
                    return;
                }

                $scope.state.selectedRecYear = -1;
                $scope.state.request.tRecYears = true;
                return ActiveYearsTimeRecordsApi.get({empId: emp.empId}, function(resp) {
                    var isUserSup = emp.supId === $scope.state.supId;
                    var startDate = isUserSup ? emp.supStartDate : emp.effectiveStartDate;
                    var endDate = isUserSup ? emp.supEndDate : emp.effectiveEndDate;
                    var supStartYear = moment(startDate || 0).year();
                    var supEndYear = moment(endDate || undefined).year();
                    $scope.state.recordYears = resp.years
                    // Only use years that overlap with supervisor dates
                        .filter(function(year) { return year >= supStartYear && year <= supEndYear; })
                        .reverse();
                    $scope.state.selectedRecYear = $scope.state.recordYears.length > 0
                        ? $scope.state.recordYears[0] : false;
                }, $scope.handleErrorResponse
                ).$promise.finally(function () {
                    $scope.state.request.tRecYears = false;
                });
            }

            function getRecords () {
                var emp = $scope.state.selectedEmp;
                if (!emp.empId) {
                    return;
                }

                var year = $scope.state.selectedRecYear;
                if (!year || year < 0) {
                    return;
                }

                $scope.state.records = [];
                $scope.state.attendRecords = [];
                $scope.state.timesheetRecords = [];

                // Initialize effective supervisor dates
                var isUserSup = emp.supId === $scope.state.supId;
                var supStartDate = isUserSup ? emp.supStartDate : emp.effectiveStartDate;
                var supEndDate = isUserSup ? emp.supEndDate : emp.effectiveEndDate;

                var yearStart = moment([year]);
                var nextYearStart = moment([year + 1]);

                var supStartMoment = moment(supStartDate || 0);
                var supEndMoment = moment(supEndDate || [3000]);

                // Restrict retrieval range based on effective supervisor dates
                var fromMoment = moment.max(yearStart, supStartMoment);
                var toMoment = moment.min(nextYearStart, supEndMoment);

                // Do not fetch records if this year does not overlap with supervisor dates
                if (fromMoment.isAfter(toMoment)) {
                    $scope.state.records = [];
                    return;
                }

                $scope.state.request.records = true;
                $q.all([
                           getTimesheetRecords(emp.empId, fromMoment, toMoment),
                           getAttendRecords(emp.empId, fromMoment, toMoment)
                       ]).then(function () {
                    initTimesheetRecords();
                    initAttendRecords();
                    combineRecords();
                }).finally(function () {
                    $scope.state.request.records = false;
                });
            }

            function getTimesheetRecords (empId, from, to) {
                var params = {
                    empId: empId,
                    from: moment(from).format('YYYY-MM-DD'),
                    to: moment(to).format('YYYY-MM-DD')
                };
                return TimeRecordsApi.get(params,
                    function(resp) {
                        $scope.state.timesheetRecords = (resp.result.items[empId] || []).reverse();
                        console.debug('got timesheet records', $scope.state.timesheetRecords);
                    }, $scope.handleErrorResponse
                ).$promise;
            }

            function getAttendRecords (empId, from, to) {
                var params = {
                    empId: empId,
                    from: moment(from).format('YYYY-MM-DD'),
                    to: moment(to).format('YYYY-MM-DD')
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

            $scope.isUser = function () {
                return $scope.state.selectedEmp.empId === appProps.user.employeeId;
            };

            /* --- Internal Methods --- */

            function setEmpId() {
                if ($scope.empSupInfo && $scope.empSupInfo.empId) {
                    $scope.state.selectedEmp = $scope.empSupInfo;
                }
                else {
                    $scope.state.selectedEmp = {
                        empId: appProps.user.employeeId
                    };
                    console.log('No empId provided.  Using user\'s empId:', $scope.state.selectedEmp.empId);
                }
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
        }
    };
}