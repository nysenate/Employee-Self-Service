angular.module('essTime')
    .service('AccrualUtils', accrualUtils);

function accrualUtils () {
    
    return {
        computeDeltas: computeDeltas
    };

    /**
     * Compute the hours used during the given pay periods based on the change in the YTD usage.
     * @param accruals
     */
    function computeDeltas (accruals) {
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
    }
}