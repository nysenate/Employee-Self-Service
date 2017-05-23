angular.module('essTime')
    .service('AllowanceUtils', ['$filter', allowanceUtils]);

function allowanceUtils($filter) {

    return {
        computeRemaining: computeRemaining,
        getAvailableHours: getAvailableHours
    };

    /**
     * Compute remaining allowance, hours, and total hours
     * based on the highest salary present during the given date range
     *
     * @param allowance
     * @param {Object} dateRange - object with params 'beginDate' and 'endDate'
     */
    function computeRemaining (allowance, dateRange) {
        var highestRate = 0;
        angular.forEach(allowance.salaryRecs, function (salaryRec) {
            // Select only temporary salaries that are effective during the record date range
            if (salaryRec.payType === 'TE' &&
                !moment(salaryRec.effectDate).isAfter(dateRange.endDate) &&
                !moment(dateRange.beginDate).isAfter(salaryRec.endDate)) {
                if (salaryRec.salaryRate > highestRate) {
                    highestRate = salaryRec.salaryRate;
                }
            }
        });

        allowance.remainingAllowance = allowance.yearlyAllowance - allowance.moneyUsed;
        allowance.remainingHours = allowance.remainingAllowance / highestRate;
        allowance.remainingHours = $filter('round')(allowance.remainingHours, 0.25, -1);
        allowance.totalHours = allowance.hoursUsed + allowance.remainingHours;
    }

    /**
     * Get the number of available work hours at the selected salary rate
     *  such that the record cost does not exceed the employee's annual allowance
     * @returns {number}
     */
    function getAvailableHours (allowance, tempWorkHours) {
        var remainingHours = (allowance || {}).remainingHours;

        return remainingHours - tempWorkHours;
    }
}