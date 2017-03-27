
angular.module('essTime')
    .directive('accrualHistory', ['$timeout', 'appProps', 'modals',
                                  'AccrualHistoryApi', 'EmpInfoApi', 'ActiveYearsTimeRecordsApi',
                                  accrualHistoryDirective]);

function accrualHistoryDirective($timeout, appProps, modals, AccrualHistoryApi, EmpInfoApi, ActiveYearsTimeRecordsApi) {
    return {
        scope: {
            /**
             *  An optional employee sup info
             *  If this is present, then accruals will be displayed for the corresponding employee
             *    for the appropriate dates.
             *  Otherwise, accruals will be displayed for the logged in user
             */
            empSupInfo: '=?'
        },
        templateUrl: appProps.ctxPath + '/template/time/accrual/history-directive',

        link: function ($scope) {
            $scope.accSummaries = {};
            $scope.activeYears = [];
            $scope.timeRecords = [];
            $scope.selectedYear = null;
            $scope.empInfo = {};
            $scope.isTe = false;

            $scope.error = null;
            $scope.request = {
                empInfo: false,
                empActiveYears: false,
                accSummaries: false
            };

            $scope.floatTheadOpts = {
                scrollingTop: 47,
                useAbsolutePositioning: false
            };

            /* --- Watches --- */

            /** Watch the bound employee sup info and set empId when it changes */
            $scope.$watchCollection('empSupInfo', setEmpId);

            /** When a new empId is selected, refresh employee info, active years and clear cached accrual summaries */
            $scope.$watch('empId', getEmpInfo);
            $scope.$watch('empId', getEmpActiveYears);
            $scope.$watch('empId', clearAccSummaries);

            /** When a new year is selected, get accrual summaries for that year */
            $scope.$watch('selectedYear', getAccSummaries);

            /* --- Api Request Methods --- */

            /**
             * Retrieves employee info from the api to determine if the employee is a temporary employee
             */
            function getEmpInfo() {
                // Cancel emp info retrieval if empId is null or viewing non-user employee
                if (!($scope.empId && $scope.isUser())) {
                    return;
                }
                var params = {
                    empId: $scope.empId,
                    detail: true
                };
                console.debug('getting emp info', params);
                $scope.request.empInfo = true;
                EmpInfoApi.get(params,
                    function onSuccess(response) {
                        console.debug('got emp info');
                        var empInfo = response.employee;
                        $scope.empInfo = empInfo;
                        $scope.isTe = empInfo.payType === 'TE';
                    },
                    function onFail(errorResponse) {
                        console.error('Error retrieving emp info', errorResponse);
                        modals.open('500', errorResponse);
                    }
                ).$promise.finally(function () {
                    $scope.request.empInfo = false;
                });
            }

            /**
             * Retrieves the employee's active years
             */
            function getEmpActiveYears() {
                if (!$scope.empId) {
                    return;
                }
                $scope.selectedYear = null;
                var params = {empId: $scope.empId};
                console.debug('getting active years', params);
                $scope.request.empActiveYears = true;
                ActiveYearsTimeRecordsApi.get(params,
                    function onSuccess(resp) {
                        $scope.activeYears = resp.years.reverse();
                        // Filter active years if looking at someone else's record
                        if (!$scope.isUser()) {
                            var startDateYear = moment($scope.empSupInfo.effectiveStartDate || 0).year();
                            var endDateYear = moment($scope.empSupInfo.effectiveEndDate || undefined).year();
                            $scope.activeYears = $scope.activeYears.filter(function (year) {
                                return year >= startDateYear && year <= endDateYear;
                            });
                        }
                        $scope.selectedYear = $scope.activeYears[0];
                        console.debug('got active years', $scope.activeYears);
                    }, function onFail(resp) {
                        modals.open('500', {details: resp});
                        console.error('error loading employee active years', resp);
                    }
                ).$promise.finally(function () {
                    $scope.request.empActiveYears = false;
                });
            }

            /**
             * Retrieve the employee's accrual records
             */
            function getAccSummaries() {
                var year = $scope.selectedYear;
                if (!year || $scope.accSummaries[year]) {
                    return;
                }

                var fromDate = moment([year, 0, 1]);
                var toDate = moment([year + 1, 0, 1]);

                // Restrict by start and end dates if applicable
                if (!$scope.isUser()) {
                    var startDateMoment = moment($scope.empSupInfo.effectiveStartDate || 0);
                    var endDateMoment = moment($scope.empSupInfo.effectiveEndDate || '3000-01-01');

                    fromDate = moment.max(fromDate, startDateMoment);
                    toDate = moment.min(toDate, endDateMoment);
                }

                var params = {
                    empId: $scope.empId,
                    fromDate: fromDate.format('YYYY-MM-DD'),
                    toDate: toDate.format('YYYY-MM-DD')
                };
                $scope.request.accSummaries = true;
                AccrualHistoryApi.get(params,
                    function onSuccess (resp) {
                        $scope.error = null;
                        // Filter out projection records and order in reverse chronological
                        $scope.accSummaries[year] = resp.result
                            .filter(function(acc) {
                                return !acc.computed || acc.submitted;
                            }).reverse();
                    }, function onFail (resp) {
                        modals.open('500', {details: resp});
                        console.error('error loading accrual history', resp);
                        $scope.error = {
                            title: "Could not retrieve accrual information.",
                            message: "If you are eligible for accruals please try again later."
                        }
                    }
                ).$promise.finally(function () {
                    $scope.request.accSummaries = false;
                });
            }

            /* --- Display Methods --- */

            /**
             * @returns {boolean} true iff the user's accruals are being displayed
             */
            $scope.isUser = function () {
                return $scope.empId === appProps.user.employeeId;
            };

            /**
             * @returns {boolean} true iff any requests are currently loading
             */
            $scope.isLoading = function () {
                for (var dataType in $scope.request) {
                    if (!$scope.request.hasOwnProperty(dataType)) {
                        continue;
                    }
                    if ($scope.request[dataType]) {
                        return true;
                    }
                }
                return false;
            };

            /**
             * Open the accrual detail modal
             * @param accrualRecord
             */
            $scope.viewDetails = function (accrualRecord) {
                modals.open('accrual-details', {accruals: accrualRecord}, true);
            };

            /* --- Internal Methods --- */

            /**
             * Set the employee id from the passed in employee sup info if it exists
             * Otherwise set it to the user's empId
             */
            function setEmpId() {
                if ($scope.empSupInfo && $scope.empSupInfo.empId) {
                    $scope.empId = $scope.empSupInfo.empId;
                }
                else {
                    $scope.empId = appProps.user.employeeId;
                    console.log('No empId provided.  Using user\'s empId:', $scope.empId);
                }
            }

            /**
             * Clears any existing cached accrual summaries
             */
            function clearAccSummaries () {
                $scope.accSummaries = {};
            }

            /* --- Angular smart table hacks --- */

            /**
             * Attempt to reflow the accrual table 20 times with one attempt every 5ms
             * @param count
             */
            function reflowTable (count) {
                if (count > 20 || !$scope.accSummaries[$scope.selectedYear]) {
                    return;
                }
                count = isNaN(count) ? 0 : count;
                $(".detail-acc-history-table").floatThead('reflow');
                $timeout(function () {
                    reflowTable(count + 1)
                }, 5);
            }

            $scope.$watchCollection('accSummaries[selectedYear]', reflowTable);
        }
    }
}
