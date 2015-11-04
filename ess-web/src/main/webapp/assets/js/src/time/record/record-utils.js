var essTime = angular.module('essTime');

/**
 * Contains common time record functions that are used across several different controllers
 */
essTime.service('RecordUtils', [function () {

    // Contains the field names for each hour field in a time entry
    var timeEntryFields = [
        'workHours',
        'travelHours',
        'holidayHours',
        'vacationHours',
        'personalHours',
        'sickEmpHours',
        'sickFamHours',
        'miscHours'
    ];

    return {
        getDailyTotal: getDailyTotal,
        calculateDailyTotals: calculateDailyTotals,
        getTotal: getTotal,
        getRecordTotals: getRecordTotals,
        getTimeEntryFields: getTimeEntryFields
    };

    // Return a copy of timeEntryFields array
    function getTimeEntryFields() {
        return timeEntryFields.slice();
    }

    // Get the total used hours for a single time entry
    function getDailyTotal(entry) {
        return timeEntryFields
            .map(function (timeField) {
                return +entry[timeField];
            }).reduce(function (a, b) {
                return a + b;
            });
    }

    // Calculate and add the daily total as a field in each time entry within a record
    function calculateDailyTotals (record) {
        for (var i = 0, entries = record.timeEntries; i < entries.length; i++) {
            entries[i].total = getDailyTotal(entries[i]);
        }
    }

    // Gets the total number of hours used for a specific time usage type over an entire time record
    // If payTypes is passed in, only entries with a pay type that exists in payTypes will be counted
    function getTotal(record, type, payTypes) {
        var total = 0;
        var entries = record.timeEntries;
        if (entries) {
            for (var i = 0; i < entries.length; i++) {
                if (!payTypes || payTypes.indexOf(entries[i].payType) >= 0) {
                    total += +(entries[i][type] || 0);
                }
            }
        }
        return total;
    }

    // Returns an object containing the total number of hours for each time usage type over an entire time recodr
    function getRecordTotals(record) {
        var totals = {};

        for (var iField in timeEntryFields) {
            var field = timeEntryFields[iField];
            totals[field] = getTotal(record, field);
        }
        totals.raSaWorkHours = getTotal(record, 'workHours', ['RA', 'SA']);
        totals.tempWorkHours = getTotal(record, 'workHours', ['TE']);
        totals.raSaTotal = getTotal(record, 'total', ['RA', 'SA']);
        totals.total = getTotal(record, 'total');
        return totals;
    }
}]);

