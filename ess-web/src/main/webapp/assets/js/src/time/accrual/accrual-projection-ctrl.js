var essTime = angular.module('essTime');

essTime.controller('AccrualProjectionCtrl', ['$scope', 'appProps', 'AccrualHistoryApi', 'modals', 'AccrualUtils', 
                                           accrualProjectionCtrl]);

function accrualProjectionCtrl($scope, appProps, AccrualHistoryApi, modals, accrualUtils) {

    $scope.state = {
        empId: appProps.user.employeeId,
        today: moment(),
        projections: {},
        accSummaries: {},
        selectedYear: moment().year(),

        // Page state
        searching: false,
        error: null
    };

    $scope.floatTheadOpts = {
        scrollingTop: 47,
        useAbsolutePositioning: false
    };
    
    $scope.init = function () {
        $scope.getAccSummaries($scope.state.selectedYear);
    };

    $scope.getAccSummaries = function(year) {
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
                // Gather projected acc records if year is 1 yr ago, current, or future.
                if (year >= $scope.state.today.year() - 1) {
                    $scope.state.projections[year] = resp.result.filter(function(acc) {
                        return acc.computed && acc.empState.payType !== 'TE' && acc.empState.employeeActive;
                    });
                }
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
    };

    /**
     * When a user enters in hours in the projections table, the totals need to be re-computed for
     * the projected accrual records.
     * @param year - the selected year
     */
    $scope.recalculateProjectionTotals = function(year) {
        var projLen = $scope.state.projections[year].length;
        var summLen = $scope.state.accSummaries[year].length;
        var baseRec = null;
        if (summLen > 0) {
            baseRec = $scope.state.accSummaries[year][0];
        }
        else {
            baseRec = $scope.state.projections[year][projLen - 1];
        }
        var per = baseRec.personalUsed, 
            vac = baseRec.vacationUsed, 
            sick = baseRec.empSickUsed + baseRec.famSickUsed;
        // Acc projections are stored in reverse chrono order
        for (var i = 0; i < $scope.state.projections[year].length; i++) {
            var rec = $scope.state.projections[year][i];
            per += rec.personalUsedDelta || 0;
            vac += rec.vacationUsedDelta || 0;
            sick += rec.sickUsedDelta || 0;
            rec.personalAvailable = rec.personalAccruedYtd - per;
            rec.vacationAvailable = rec.vacationAccruedYtd + rec.vacationBanked - vac;
            rec.sickAvailable = rec.sickAccruedYtd + rec.sickBanked - sick;
        }
    };
    
    $scope.init();
}
