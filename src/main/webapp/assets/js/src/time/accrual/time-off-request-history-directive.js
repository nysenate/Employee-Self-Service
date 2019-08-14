(function () {

    angular.module('essTime').directive('timeOffRequestHistory',
                                        ['appProps', 'ActiveYearsTimeRecordsApi', 'SupervisorTimeOffRequestApi',
                                         'TimeOffRequestListService', timeOffRequestHistoryDirective]);

    function timeOffRequestHistoryDirective(appProps, ActiveYearsTimeRecordsApi, SupervisorTimeOffRequestApi,
                                            TimeOffRequestListService) {

        return {
            scope: {
                /**
                 *  An optional employee sup info
                 *  If this is present, then records will be displayed for the corresponding employee
                 *    for the appropriate dates.
                 *  Otherwise, records will be displayed for the logged in user
                 */
                empSupInfo: '=?',
                /** If set, employee scope records will function as links to the time entry page for the record. */
                linkToEntryPage: '@?'
            },
            templateUrl: appProps.ctxPath + 'template/time/accrual/time-off-request-history-directive',
            link: function ($scope) {
                $scope.state = {
                    supId: appProps.user.employeeId,
                    searching: false,
                    request: {
                        tRecYears: false,
                        records: false
                    },
                    todayMoment: moment(),

                    selectedEmp: {},
                    recordYears: [],
                    selectedRecYear: -1
                };

                //getting the active years for time off requests
                /* NOTE: The active years for time off requests are the same
                * as those for attendance records */
                $scope.$watchCollection('empSupInfo', setEmpId);
                $scope.$watchCollection('empSupInfo', getTimeRecordYears);
                $scope.$watch('state.selectedRecYear', getRequests);

                function getTimeRecordYears() {

                    if (!$scope.state.selectedEmp.empId) {
                        return;
                    }

                    $scope.state.selectedRecYear = -1;
                    $scope.state.request.tRecYears = true;
                    return ActiveYearsTimeRecordsApi.get({empId: $scope.state.selectedEmp.empId},
                                                         handleActiveYearsResponse,
                                                         $scope.handleErrorResponse)
                        .$promise.finally(function () {
                            $scope.state.request.tRecYears = false;
                        });
                }

                function handleActiveYearsResponse(resp) {
                    var emp = $scope.state.selectedEmp;
                    var isUserSup = emp && emp.supId === $scope.state.supId;
                    var startDate = isUserSup ? emp.supStartDate : emp.effectiveStartDate;
                    var endDate = isUserSup ? emp.supEndDate : emp.effectiveEndDate;
                    var supStartYear = moment(startDate || 0).year();
                    var supEndYear = moment(endDate || undefined).year();
                    $scope.state.recordYears = resp.years
                    // Only use years that overlap with supervisor dates
                        .filter(function (year) {
                            return year >= supStartYear && year <= supEndYear;
                        })
                        .reverse();
                    $scope.state.selectedRecYear = $scope.state.recordYears.length > 0
                                                   ? $scope.state.recordYears[0] : false;
                }

                function setEmpId() {
                    if ($scope.empSupInfo && $scope.empSupInfo.empId) {
                        $scope.state.selectedEmp = $scope.empSupInfo;
                    } else {
                        $scope.state.selectedEmp = {
                            empId: appProps.user.employeeId
                        };
                        console.log('No empId provided.  Using user\'s empId:', $scope.state.selectedEmp.empId);
                    }
                }

                /*  END OF DUPLICATE CODE   */

                $scope.requests = null;

                /**
                 * Function that gets the requests for the selected employee and year
                 */
                function getRequests() {
                    if($scope.state.selectedRecYear > 0) {
                        SupervisorTimeOffRequestApi.query({
                                                            supId: $scope.state.supId,
                                                            empId: $scope.state.selectedEmp.empId,
                                                            year: $scope.state.selectedRecYear
                                                        }).$promise.then(
                            //successful query
                            function (data) {
                                $scope.requests = TimeOffRequestListService.formatData(data);
                            },
                            //failed query
                            function () {
                                $scope.errmsg = "Invalid query."
                            }
                        );
                    }
                }

            }
        }
    }

})();