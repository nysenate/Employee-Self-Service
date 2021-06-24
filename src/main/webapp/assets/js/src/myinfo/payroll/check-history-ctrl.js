var essMyInfo = angular.module('essMyInfo');

essMyInfo.controller('EmpCheckHistoryCtrl',
    ['$scope', '$filter', 'appProps', 'EmpCheckHistoryApi', 'EmpActiveYearsApi',
        function($scope, $filter, appProps, EmpCheckHistoryApi, EmpActiveYearsApi) {

            $scope.summary = {};
            $scope.checkHistory = {
                searching: false,
                useFiscalYears: false,
                recordYears: null,
                recordFiscalYears: null,
                year: null
            };

            $scope.dirDepositPresent = false;
            $scope.checkPresent = false;

            $scope.init = function() {
                $scope.checkHistory.recordYears = appProps.empActiveYears;
                $scope.checkHistory.year = $scope.checkHistory.recordYears[$scope.checkHistory.recordYears.length - 1];
                $scope.getRecords();
                $scope.getActiveDates();
            };

            $scope.getRecords = function() {
                $scope.summary = {};
                $scope.checkHistory.searching = true;
                var empId = appProps.user.employeeId;
                var params = {
                    empId: empId,
                    year: $scope.checkHistory.year,
                    fiscalYear: $scope.checkHistory.useFiscalYears
                };
                EmpCheckHistoryApi.get(params, function(response) {
                    $scope.summary = response.result;
                    $scope.checkHistory.searching = false;
                }, function(response) {
                    $scope.checkHistory.searching = false;
                    $scope.handleErrorResponse(response)
                })
            };

            /**
             * Retrieve employee active dates and use them to initialize the fiscal year list
             */
            $scope.getActiveDates = function () {
                EmpActiveYearsApi.get({empId: appProps.user.employeeId, fiscalYear: true},
                    function onSuccess(response) {
                        $scope.checkHistory.recordFiscalYears = response.activeYears;
                    },
                    $scope.handleErrorResponse
                )
            };

            /**
             * Function that is run when the user switches from standard to fiscal year view
             * Sets the year as the first year in the switched-to year set
             */
            $scope.onFiscalYearSwitch = function () {
                var yearArray = $scope.checkHistory.useFiscalYears
                    ? $scope.checkHistory.recordFiscalYears
                    : $scope.checkHistory.recordYears;
                $scope.checkHistory.year = yearArray[yearArray.length - 1];
                $scope.getRecords();
            };


            /** Compares two currency values, returning true if they differ by more than 3 cents. */
            $scope.isSignificantChange = function(curr, previous) {
                if (typeof previous !== 'undefined') {
                    if (Math.abs(curr - previous) > 0.03) {
                        return true;
                    }
                }
                return false;
            };

            /**
             * Only display the direct deposit column if at least one check has a value for it.
             */
            $scope.displayDirectDepositColumn = function () {
                return $scope.summary.directDepositTotal > 0;
            }

            /**
             * Only display the check amount column if at least one check has a value for it.
             */
            $scope.displayCheckColumn = function () {
                return $scope.summary.checkAmountTotal > 0;
            }

            $scope.init();
        }
    ]
);
