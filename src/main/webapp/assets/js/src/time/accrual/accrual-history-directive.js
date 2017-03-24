
angular.module('essTime')
    .directive('accrualHistory', ['$timeout', 'appProps', 'modals',
                                  'AccrualHistoryApi', 'EmpInfoApi', 'EmpActiveYearsApi',
                                  accrualHistoryDirective]);

function accrualHistoryDirective($timeout, appProps, modals, AccrualHistoryApi, EmpInfoApi, EmpActiveYearsApi) {
    return {
        scope: {
            empId: '=?'
        },
        templateUrl: appProps.ctxPath + '/template/time/accrual/history-directive',

        link: function ($scope) {
            if (!$scope.empId) {
                $scope.empId = appProps.user.employeeId;
                console.log('No empId provided.  Using user\'s empId:', $scope.empId);
            }
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

            $scope.$watch('empId', getEmpInfo);
            $scope.$watch('empId', getEmpActiveYears);
            $scope.$watch('selectedYear', getAccSummaries);

            /* --- Api Request Methods --- */

            /**
             * Retrieves employee info from the api to determine if the employee is a temporary employee
             */
            function getEmpInfo() {
                if (!$scope.empId) {
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
                EmpActiveYearsApi.get(params,
                    function onSuccess(resp) {
                        $scope.activeYears = resp.activeYears.reverse();
                        $scope.selectedYear = resp.activeYears[0];
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
                var toDate = moment([year + 1, 0, 1]).subtract(1, 'days');
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
