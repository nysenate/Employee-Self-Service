var essTime = angular.module('essTime');

essTime.controller('AccrualHistoryCtrl', ['$scope', 'appProps', 'AccrualHistoryApi', 'EmpActiveYearsApi',
                                          'modals', 'AccrualUtils', accrualHistoryCtrl]);

function accrualHistoryCtrl($scope, appProps, AccrualHistoryApi, EmpActiveYearsApi, modals, accrualUtils) {

    $scope.state = {
        empId: appProps.user.employeeId,
        today: moment(),
        accSummaries: {},
        activeYears: [],
        selectedYear: null,

        // Page state
        searching: false,
        error: null
    };

    $scope.floatTheadOpts = {
        scrollingTop: 47,
        useAbsolutePositioning: false
    };

    $scope.getAccSummaries = function(year) {
        if ($scope.state.accSummaries[year]) {
            return $scope.state.accSummaries[year];
        }
        else {
            $scope.state.searching = true;
            var fromDate = moment([year, 0, 1]);
            var toDate = moment([year + 1, 0, 1]).subtract(1, 'days');
            AccrualHistoryApi.get({
                empId: $scope.state.empId,
                fromDate: fromDate.format('YYYY-MM-DD'),
                toDate: toDate.format('YYYY-MM-DD')
            }, function(resp) {
                if (resp.success) {
                    $scope.state.error = null;
                    // Compute deltas
                    accrualUtils.computeDeltas(resp.result);
                    // Gather historical acc summaries
                    $scope.state.accSummaries[year] = resp.result.filter(function(acc) {
                        return !acc.computed;
                    }).reverse();
                }
                $scope.state.searching = false;
            }, function(resp) {
                modals.open('500', {details: resp});
                console.log(resp);
                $scope.state.error = {
                    title: "Could not retrieve accrual information.",
                    message: "If you are eligible for accruals please try again later."
                }
            });
        }
    };

    /**
     * Retrieves the years that an employee has been employed during.
     * @param callBack
     */
    $scope.getEmpActiveYears = function(callBack) {
        EmpActiveYearsApi.get({empId: $scope.state.empId}, function(resp) {
            $scope.state.activeYears = resp.activeYears.reverse();
            $scope.state.selectedYear = resp.activeYears[0];
            if (callBack) callBack();
        }, function(resp) {
            modals.open('500', {details: resp});
            console.log(resp);
        });
    };

    /**
     * Initialize
     */
    $scope.init = function() {
        $scope.getEmpActiveYears(function() {
            $scope.getAccSummaries($scope.state.selectedYear);
        });
        // make sure that the table head is in position
        $timeout(function () {
            jQuery('.detail-acc-history-table').floatThead('reflow');
        });
        // make extra sure that the table head is in position
        $timeout(function () {
            jQuery('.detail-acc-history-table').floatThead('reflow');
        }, 50);
    }();
}