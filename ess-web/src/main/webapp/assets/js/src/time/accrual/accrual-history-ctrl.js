var essTime = angular.module('essTime');

essTime.controller('AccrualHistoryCtrl', ['$scope', '$http', '$location', '$anchorScroll', '$timeout',
                                          'appProps', 'AccrualHistoryApi', 'EmpActiveYearsApi', 'modals',
                                          accrualHistoryCtrl]);

function accrualHistoryCtrl($scope, $http, $location, $anchorScroll, $timeout,
                            appProps, AccrualHistoryApi, EmpActiveYearsApi, modals) {

    $scope.state = {
        empId: appProps.user.employeeId,
        today: moment(),
        projections: {},
        accSummaries: {},
        activeYears: [],
        selectedYear: null,

        // Page state
        searching: false,
        error: null
    };

    $scope.getAccSummaries = function(year) {
        if ($scope.state.accSummaries[year]) {
            return $scope.state.accSummaries[year];
        }
        else {
            $scope.state.searching = true;
            var fromDate = moment([year, 0, 1]);
            var toDate = moment([year + 1, 0, 1]).subtract(1, 'days');
            var accSummariesResp = AccrualHistoryApi.get({
                empId: $scope.state.empId,
                fromDate: fromDate.format('YYYY-MM-DD'),
                toDate: toDate.format('YYYY-MM-DD')
            }, function(resp) {
                if (resp.success) {
                    $scope.state.error = null;
                    // Compute deltas
                    computeDeltas(resp.result);
                    // Gather historical acc summaries
                    $scope.state.accSummaries[year] = resp.result.filter(function(acc) {
                        return !acc.computed;
                    }).reverse();
                    // Gather projected acc records if year is 1 yr ago, current, or future.
                    if (year >= $scope.state.today.year() - 1) {
                        $scope.state.projections[year] = resp.result.filter(function(acc) {
                            return acc.computed && acc.empState.payType !== 'TE' && acc.empState.employeeActive;
                        }).reverse();
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
        }
    };

    /**
     * Compute the hours used during the given pay periods based on the change in the YTD usage.
     * @param accruals
    */
    var computeDeltas = function(accruals) {
        for (var i = 0; i < accruals.length; i++) {
            var currSummary = accruals[i];
            if (i == 0) {
                currSummary.vacationUsedDelta = currSummary.vacationUsed;
                currSummary.personalUsedDelta = currSummary.personalUsed;
                currSummary.sickUsedDelta = currSummary.empSickUsed + currSummary.famSickUsed;
            }
            else {
                var prevSummary = accruals[i - 1];
                currSummary.vacationUsedDelta = currSummary.vacationUsed - prevSummary.vacationUsed;
                currSummary.personalUsedDelta = currSummary.personalUsed - prevSummary.personalUsed;
                currSummary.sickUsedDelta = (currSummary.empSickUsed + currSummary.famSickUsed) -
                    (prevSummary.empSickUsed + prevSummary.famSickUsed);
            }
        }
    };

    /**
     * When a user enters in hours in the projections table, the totals need to be re-computed for
     * the projected accrual records.
     * @param year - the selected year
     */
    $scope.recalculateProjectionTotals = function(year) {
        var projLen = $scope.state.projections[year].length;
        var summLen = $scope.state.accSummaries[year].length;
        var baseRec = null, startIndex = 0;
        if (summLen > 0) {
            baseRec = $scope.state.accSummaries[year][0];
            startIndex = projLen - 1;
        }
        else {
            baseRec = $scope.state.projections[year][projLen - 1];
            startIndex = projLen - 2;
        }
        var per = baseRec.personalUsed, vac = baseRec.vacationUsed, sick = baseRec.empSickUsed + baseRec.famSickUsed;
        // Acc projections are stored in reverse chrono order
        for (var i = startIndex; i >= 0; i--) {
            var rec = $scope.state.projections[year][i];
            per += rec.personalUsedDelta || 0;
            vac += rec.vacationUsedDelta || 0;
            sick += rec.sickUsedDelta || 0;
            rec.personalAvailable = rec.personalAccruedYtd - per;
            rec.vacationAvailable = rec.vacationAccruedYtd + rec.vacationBanked - vac;
            rec.sickAvailable = rec.sickAccruedYtd + rec.sickBanked - sick;
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

    $scope.floatTheadOpts = {
        scrollingTop: 47,
        useAbsolutePositioning: false
    };

    /**
     * Perform an immediate and delayed float-thead reflow
     * Preferably immediate to reduce flicker but sometimes it is too early
     */
    $scope.$watch('activeAccrualTab', function () {
        jQuery('.detail-acc-history-table').floatThead('reflow');
        $timeout(function () {
            jQuery('.detail-acc-history-table').floatThead('reflow');
        }, 100); 
    });

    /**
     * Initialize
     */
    $scope.init = function() {
        $scope.getEmpActiveYears(function() {
            $scope.getAccSummaries($scope.state.selectedYear);
        });
    }();
};