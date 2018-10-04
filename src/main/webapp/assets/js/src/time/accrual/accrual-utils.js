angular.module('essTime')
    .service('AccrualUtils', ['$httpParamSerializer', 'appProps',
    function accrualUtils() {

        return {
            isFirstRecordOfYear: isFirstRecordOfYear
        };

        /**
         * Returns true iff the given record is the first record of its year
         * @param record
         * @returns {boolean}
         */
        function isFirstRecordOfYear(record) {
            var beginDate = moment(record.payPeriod.startDate);
            return beginDate.month() === 0 && beginDate.date() === 1;
        }

    }]);
