var essTime = angular.module('essTime');

essTime.controller('AccrualProjectionCtrl', ['$scope', '$timeout', 'appProps', 
                                             'AccrualHistoryApi', 'EmpInfoApi', 
                                             'modals', 'AccrualUtils', 
                                             accrualProjectionCtrl]);

function accrualProjectionCtrl($scope, $timeout, appProps, AccrualHistoryApi, EmpInfoApi, modals, accrualUtils) {

    var maxVacationBanked = 210,
        maxSickBanked = 1400;

    $scope.state = {
        empId: appProps.user.employeeId,
        today: moment(),
        projections: [],
        accSummaries: [],
        selectedYear: moment().year(),
        empInfo: {},
        isTe: false,

        // Page state
        searching: false,
        error: null
    };

    $scope.floatTheadOpts = {
        scrollingTop: 47,
        useAbsolutePositioning: false
    };

    /** Get emp info for the selected employee id */
    $scope.$watch('state.empId', getEmpInfo);
    
    $scope.init = function () {
        $scope.getAccSummaries($scope.state.selectedYear);
    };

    $scope.getAccSummaries = function(year) {
        $scope.state.searching = true;
        var fromDate = moment([year, 0, 1]).subtract(6, 'months');
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
                $scope.state.accSummaries = resp.result.filter(function(acc) {
                    return !acc.computed || acc.submitted;
                }).reverse();
                // Gather projected acc records if year is 1 yr ago, current, or future.
                if (year >= $scope.state.today.year() - 1) {
                    $scope.state.projections = resp.result
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
     */
    $scope.recalculateProjectionTotals = function() {
        var accSummaries = $scope.state.accSummaries;
        var projections = $scope.state.projections;
        var baseRec = accSummaries.length > 0 ? accSummaries[0] : null;
        var multiYear = false;

        var per = 0, vac = 0, sick = 0;
        if (baseRec) {
            per = baseRec.personalUsed;
            vac = baseRec.vacationUsed;
            sick = baseRec.empSickUsed + baseRec.famSickUsed;
        }
        // Acc projections are stored in reverse chrono order
        for (var i = 0; i < projections.length; i++) {
            var rec = projections[i];

            var lastRec = i === 0 ? baseRec : projections[i - 1];

            // If multiple years are present, banked hours will be dynamic and need to be reset
            if (multiYear) {
                rec.vacationBanked = lastRec.vacationBanked;
                rec.sickBanked = lastRec.sickBanked;
            }

            // Apply rollover if record is the first of the year and a preceding record is available
            if (lastRec && accrualUtils.isFirstRecordOfYear(rec)) {
                multiYear = true;

                rec.vacationBanked = Math.min(lastRec.vacationAvailable, maxVacationBanked);
                rec.sickBanked = Math.min(lastRec.sickAvailable, maxSickBanked);

                per = vac = sick = 0;
            }

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
        return acc.computed && !acc.submitted && acc.empState.payType !== 'TE' && acc.empState.employeeActive;
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

    /**
     * Retrieves employee info from the api to determine if the employee is a temporary employee
     */
    function getEmpInfo() {
        if (!$scope.state.empId) {
            return;
        }
        EmpInfoApi.get({empId: $scope.state.empId, detail: true},
            function onSuccess(response) {
                var empInfo = response.employee;
                $scope.state.empInfo = empInfo;
                $scope.state.isTe = empInfo.payType === 'TE';
            },
            function onFail(errorResponse) {
                modals.open('500', errorResponse);
            }
        );
    }

    function reflowTable () {
        $timeout(function () {
            $(".detail-acc-history-table").floatThead('reflow');
        }, 0);
    }

    $scope.$watchCollection('state.projections', reflowTable);
    
    $scope.init();
}
