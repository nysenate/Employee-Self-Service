var essTime = angular.module('essTime');

essTime.controller('AccrualProjectionCtrl', ['$scope', '$timeout', 'appProps', 'AccrualHistoryApi', 'modals', 'AccrualUtils', 
                                           accrualProjectionCtrl]);

function accrualProjectionCtrl($scope, $timeout, appProps, AccrualHistoryApi, modals, accrualUtils) {

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
        // make sure that the table head is in position
        $timeout(function () {
            jQuery('.detail-acc-history-table').floatThead('reflow');
        });
        // make extra sure that the table head is in position
        $timeout(function () {
            jQuery('.detail-acc-history-table').floatThead('reflow');
        }, 50);
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
                    $scope.state.projections[year] = resp.result
                        .filter(isValidProjection)
                        .map(initializeProjection);
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
            baseRec = $scope.state.projections[year][0];
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

    /** --- Internal Methods --- */

    /**
     * @param acc Accrual record
     * @returns {*|boolean} - True iff the record is a computed projection
     *                          and the employee is able to accrue/use accruals
     */
    function isValidProjection(acc) {
        return acc.computed && acc.empState.payType !== 'TE' && acc.empState.employeeActive;
    }

    /** Indicates delta fields that are used for input, used to init projection */
    var deltaFields = ['personalUsedDelta', 'vacationUsedDelta', 'sickUsedDelta'];

    /**
     * Initialize the given projection for display
     * @param projection - Accrual projection record
     */
    function initializeProjection(projection) {
        // Set all 0 fields as null to facilitate initial entry
        deltaFields.forEach(function (fieldName) {
            if (projection[fieldName] === 0) {
                projection[fieldName] = null;
            }
        });
        return projection;
    }
    
    $scope.init();
}
