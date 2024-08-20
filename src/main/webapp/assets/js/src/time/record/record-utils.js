var essTime = angular.module('essTime');

/**
 * Contains common time record functions that are used across several different controllers
 */
essTime.service('RecordUtils', recordUtils);
function recordUtils() {

    // Contains the field names for each hour field in a time entry
    var timeEntryFields = [
        'workHours',
        'travelHours',
        'holidayHours',
        'vacationHours',
        'personalHours',
        'sickEmpHours',
        'sickFamHours',
        'miscHours',
        'misc2Hours'
    ];

    return {
        getDailyTotal: getDailyTotal,
        calculateDailyTotals: calculateDailyTotals,
        getTotal: getTotal,
        wasMiscEntered: wasMiscEntered,
        getRecordTotals: getRecordTotals,
        getTimeEntryFields: getTimeEntryFields,
        formatAttendRecord: formatAttendRecord,
        compareRecords: compareRecords,
        entryHasEnteredTime: entryHasEnteredtime,
        recordHasEnteredTime: recordHasEnteredTime,
        isFullTempRecord: isFullTempRecord
    };

    // Return a copy of timeEntryFields array
    function getTimeEntryFields() {
        return timeEntryFields.slice();
    }

    // Get the total used hours for a single time entry
    function getDailyTotal(entry) {
        return timeEntryFields
            .map(function (timeField) {
                var fieldValue = entry[timeField];
                return isNaN(fieldValue) ? 0 : +fieldValue;
            }).reduce(function (a, b) {
                return a + b;
            });
    }

    // Calculate and add the daily total as a field in each time entry within a record
    function calculateDailyTotals (record) {
        var entries = record.timeEntries
        for (var i = 0; i < entries.length; i++) {
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

    function wasMiscEntered(record) {
        var total = getTotal(record, 'miscHours');
        if (total > 0) {
            return true;
        }
        return false;
    }

    // Returns an object containing the total number of hours for each time usage type over an entire time record
    function getRecordTotals(record) {
        var totals = {};

        for (var iField in timeEntryFields) {
            if (!timeEntryFields.hasOwnProperty(iField)) {
                continue;
            }
            var field = timeEntryFields[iField];
            totals[field] = getTotal(record, field);
        }
        totals.raSaWorkHours = getTotal(record, 'workHours', ['RA', 'SA']);
        totals.tempWorkHours = getTotal(record, 'workHours', ['TE']);
        totals.raSaTotal = getTotal(record, 'total', ['RA', 'SA']);
        totals.total = getTotal(record, 'total');
        return totals;
    }

    /**
     * Modify the given attend record so that it will be compatible with timesheets for a summary view
     * @param attendRecord
     * @returns {*}
     */
    function formatAttendRecord(attendRecord) {
        attendRecord.totals = {
            workHours: attendRecord.workHours,
            holidayHours: attendRecord.holidayHours,
            vacationHours: attendRecord.vacationHours,
            personalHours: attendRecord.personalHours,
            sickEmpHours: attendRecord.sickEmpHours,
            sickFamHours: attendRecord.sickFamHours,
            miscHours: attendRecord.miscHours,
            misc2Hours: attendRecord.misc2Hours,
            total: attendRecord.totalHours
        };
        attendRecord.recordStatus = 'APPROVED_PERSONNEL';
        attendRecord.payPeriod = {payPeriodNum: attendRecord.payPeriodNum};
        return attendRecord;
    }

    /**
     * Compare two attendance/timesheet records based on begin and end dates
     * @param lhs
     * @param rhs
     * @returns {number}
     */
    function compareRecords(lhs, rhs) {
        var lhsBegin = moment(lhs.beginDate),
            rhsBegin = moment(rhs.beginDate);

        if (lhsBegin.isBefore(rhsBegin)) return -1;
        if (lhsBegin.isAfter(rhsBegin)) return 1;

        var lhsEnd = moment(lhs.endDate),
            rhsEnd = moment(rhs.endDate);

        if (lhsEnd.isBefore(rhsEnd)) return -1;
        if (lhsEnd.isAfter(rhsEnd)) return 1;

        return 0;
    }

    /**
     * Checks if the given time entry has any entered (non-null integer) values
     * @param timeEntry
     * @returns {boolean} true if values have been entered
     */
    function entryHasEnteredtime(timeEntry) {
        for (var iField in timeEntryFields) {
            if (!timeEntryFields.hasOwnProperty(iField)) {
                continue;
            }
            var field = timeEntryFields[iField];
            if (!timeEntry.hasOwnProperty(field)) {
                console.error("Field not found on time entry: ", field, timeEntry);
            }
            if (!isNaN(parseInt(timeEntry[field]))) {
                return true;
            }
        }
        return false;
    }


    /**
     * Checks if the given time record has any time entries with any entered (non-null integer) values
     * @param timeRecord
     * @returns {boolean} true if values have been entered
     */
    function recordHasEnteredTime(timeRecord) {
        var entries = timeRecord.timeEntries;
        for (var iEntry in entries) {
            if (!entries.hasOwnProperty(iEntry)) {
                continue;
            }
            if (entryHasEnteredtime(entries[iEntry])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the record contains only entries where the employee was a temporary employee
     * @param timeRecord
     * @returns {boolean}
     */
    function isFullTempRecord(timeRecord) {
        var entries = timeRecord.timeEntries;
        if (entries.length === 0) {
            return false;
        }
        for (var iEntry in entries) {
            if (!entries.hasOwnProperty(iEntry)) {
                continue;
            }
            var entry = entries[iEntry];
            if (entry.payType !== 'TE') {
                return false;
            }
        }
        return true;
    }
}

