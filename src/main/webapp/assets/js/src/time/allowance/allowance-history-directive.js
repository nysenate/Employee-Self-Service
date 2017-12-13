angular.module('essTime')
    .directive('allowanceHistory', ['$rootScope', '$timeout', 'appProps', 'AllowanceUtils', 'EmpInfoApi',
                                    'AllowanceActiveYearsApi', 'PeriodAllowanceUsageApi',
                                    allowanceHistoryDirective]);

function allowanceHistoryDirective($rootScope, $timeout, appProps, allowanceUtils, empInfoApi,
                                   allowanceActiveYearsApi, periodAllowanceUsageApi) {
    return {
        scope: {
            /**
             *  An optional employee sup info
             *  If this is present, then allowance history will be displayed for the corresponding employee
             *    for the appropriate dates.
             *  Otherwise, allowance history will be displayed for the logged in user
             */
            empSupInfo: '=?'
        },
        templateUrl: appProps.ctxPath + '/template/time/allowance/history-directive',

        link: function ($scope, $elem, $attrs) {

            $scope.periodAllowanceUsages = {};
            $scope.activeYears = [];
            $scope.selectedYear = null;
            $scope.empInfo = {};

            $scope.hideTitle = $attrs.hideTitle === 'true';

            $scope.floatTheadOpts = {
                scrollingTop: 47,
                useAbsolutePositioning: false
            };

            $scope.request = {
                empInfo: false,
                empActiveYears: false,
                periodAllowanceUsage: false
            };

            /* --- Watches --- */

            $scope.$watch('empSupInfo', onSupEmpInfoChange);

            $scope.$watch('selectedYear', getPeriodAllowanceUsages);

            /* --- Display Methods --- */

            /**
             * @returns {boolean} true iff the user's allowances are being displayed
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
             * @returns {boolean} true iff employee data is loading
             */
            $scope.isEmpLoading = function () {
                return $scope.request.empInfo || $scope.request.empActiveYears;
            };

            /**
             * Get the expected available hours for the given period allowance usage
             * @param periodUsage
             * @return {number|*}
             */
            $scope.getExpectedHours = function (periodUsage) {
                return allowanceUtils.getAvailableHours(periodUsage, periodUsage.periodHoursUsed);
            };

            /**
             * Opens a printable report for the currently selected period accrual usage
             */
            $scope.openReport = function () {
                alert('sheeit ' + $scope.selectedPeriodUsage.payPeriod.payPeriodNum);
            };

            /**
             * Selects a period accrual usage for printing.
             * @param periodUsage
             */
            $scope.selectPeriodUsage = function (periodUsage) {
                $scope.selectedPeriodUsage = periodUsage;
            };

            /**
             * Get the url for the selected period accrual usage
             */
            $scope.printSelectedPerUsage = function () {
                var periodUsage = $scope.selectedPeriodUsage;
                if (!periodUsage) {
                    return;
                }
                var url = 'http://nysasprd.senate.state.ny.us:7778/reports/rwservlet?' +
                    'cmdkey=tsuser&report=PRBHRS23&destype=CACHE&desformat=PDF&blankpages=no&' +
                    'P_DTEND=' + moment(periodUsage.payPeriod.endDate).format('DD-MMM-YY') + '&' +
                    'P_NUXREFEM=' + periodUsage.empId
                ;
                window.open(url, '_blank');
            };

            /* --- Internal Methods --- */

            /**
             * Update data to represent a newly selected employee
             */
            function onSupEmpInfoChange() {
                setEmpId();
                getEmpInfo();
                getEmpActiveYears();
                clearPeriodAllowanceUsages();
            }

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
                empInfoApi.get(params, onSuccess, $scope.handleErrorResponse)
                    .$promise.finally(function () {
                        $scope.request.empInfo = false;
                    });

                function onSuccess(response) {
                    console.debug('got emp info');
                    var empInfo = response.employee;
                    $scope.empInfo = empInfo;
                    $scope.isTe = empInfo.payType === 'TE';
                }
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

                allowanceActiveYearsApi.get(params, onSuccess, $scope.handleErrorResponse)
                    .$promise.finally(function () {
                        $scope.request.empActiveYears = false;
                    });

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
                    $scope.selectedYear = $scope.activeYears.length > 0 ? $scope.activeYears[0] : false;
                    console.debug('got active years', $scope.activeYears);
                }
            }

            /**
             * Clears any existing cached period allowance usages
             */
            function clearPeriodAllowanceUsages () {
                $scope.periodAllowanceUsages = {};
            }


            /**
             * Retrieve the employee's period allowance usage
             */
            function getPeriodAllowanceUsages() {
                var year = $scope.selectedYear;
                if (!year || $scope.periodAllowanceUsages[year]) {
                    return;
                }
                // todo implement date ranges when this page becomes available for supervisors
                // var fromDate = moment([year, 0, 1]);
                // var toDate = moment([year + 1, 0, 1]);
                //
                // // Restrict by start and end dates if applicable
                // if (!$scope.isUser()) {
                //     var startDateMoment = moment($scope.empSupInfo.effectiveStartDate || 0);
                //     var endDateMoment = moment($scope.empSupInfo.effectiveEndDate || '3000-01-01');
                //
                //     fromDate = moment.max(fromDate, startDateMoment);
                //     toDate = moment.min(toDate, endDateMoment);
                // }

                var params = {
                    empId: $scope.empId,
                    year: year
                };
                $scope.request.periodAllowanceUsage = true;
                periodAllowanceUsageApi.get(params, onSuccess, $scope.handleErrorResponse)
                    .$promise.finally(function () {
                        $scope.request.periodAllowanceUsage = false;
                    });

                function onSuccess (resp) {
                    // Order in reverse chronological
                    var allowanceUsages = resp.result.reverse();

                    // Compute remaining allowance for each period usage
                    allowanceUsages.forEach(function (periodUsage) {
                        var dateRange = {
                            beginDate: periodUsage.payPeriod.startDate,
                            endDate: periodUsage.payPeriod.endDate
                        };
                        allowanceUtils.computeRemaining(periodUsage, dateRange);
                    });

                    $scope.periodAllowanceUsages[year] = allowanceUsages;
                }
            }

            /* --- Angular smart table hacks --- */

            /**
             * Attempt to reflow the accrual table 20 times with one attempt every 5ms
             * @param count
             */
            function reflowTable (count) {
                if (count > 20 || !$scope.periodAllowanceUsages[$scope.selectedYear]) {
                    return;
                }
                count = isNaN(count) ? 0 : count;
                $(".allowance-table").floatThead('reflow');
                $timeout(function () {
                    reflowTable(count + 1)
                }, 5);
            }

            $scope.$watchCollection('periodAllowanceUsages[selectedYear]', reflowTable);

            $rootScope.$on('reflowEvent', reflowTable);
        }
    }
}
