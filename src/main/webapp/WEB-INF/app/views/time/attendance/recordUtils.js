/**
 * Common time record helpers, ported from the legacy AngularJS RecordUtils service
 * (assets/js/src/time/record/record-utils.js).
 *
 * These are pure functions: rather than annotating records in place the way the Angular
 * service did, they return new objects so they can be used from React render paths.
 */

/** The hour fields present on every time entry. */
export const TIME_ENTRY_FIELDS = [
  "workHours",
  "travelHours",
  "holidayHours",
  "vacationHours",
  "personalHours",
  "sickEmpHours",
  "sickFamHours",
  "miscHours",
  "misc2Hours",
];

const ANNUAL_PAY_TYPES = ["RA", "SA"];
const TEMP_PAY_TYPES = ["TE"];

/** Sums every hour field of a single time entry, treating unentered values as zero. */
export function getDailyTotal(entry) {
  return TIME_ENTRY_FIELDS.reduce((total, field) => {
    const value = entry[field];
    return total + (isNaN(value) || value === null ? 0 : +value);
  }, 0);
}

/** Returns a copy of the record with a "total" added to each of its time entries. */
export function withDailyTotals(record) {
  return {
    ...record,
    timeEntries: (record.timeEntries || []).map((entry) => ({
      ...entry,
      total: getDailyTotal(entry),
    })),
  };
}

/**
 * Sums a single hour field across a whole record.
 * When payTypes is given, only entries with a matching pay type are counted.
 */
function getTotal(record, field, payTypes) {
  return (record.timeEntries || []).reduce((total, entry) => {
    if (payTypes && !payTypes.includes(entry.payType)) {
      return total;
    }
    return total + +(entry[field] || 0);
  }, 0);
}

/**
 * Returns the totals for each hour type across a whole record.
 * Expects a record that has already been passed through withDailyTotals.
 */
export function getRecordTotals(record) {
  const totals = {};
  TIME_ENTRY_FIELDS.forEach((field) => {
    totals[field] = getTotal(record, field);
  });
  totals.raSaWorkHours = getTotal(record, "workHours", ANNUAL_PAY_TYPES);
  totals.tempWorkHours = getTotal(record, "workHours", TEMP_PAY_TYPES);
  totals.raSaTotal = getTotal(record, "total", ANNUAL_PAY_TYPES);
  totals.total = getTotal(record, "total");
  return totals;
}

/** Adds daily totals and record totals to a timesheet record. */
export function initTimesheetRecord(record) {
  const withTotals = withDailyTotals(record);
  return { ...withTotals, totals: getRecordTotals(withTotals) };
}

/**
 * Reshapes a paper attendance record so it can be displayed alongside electronic timesheets.
 * Paper records are always already approved by personnel.
 */
export function formatAttendRecord(attendRecord) {
  return {
    ...attendRecord,
    totals: {
      workHours: attendRecord.workHours,
      holidayHours: attendRecord.holidayHours,
      vacationHours: attendRecord.vacationHours,
      personalHours: attendRecord.personalHours,
      sickEmpHours: attendRecord.sickEmpHours,
      sickFamHours: attendRecord.sickFamHours,
      miscHours: attendRecord.miscHours,
      misc2Hours: attendRecord.misc2Hours,
      total: attendRecord.totalHours,
    },
    recordStatus: "APPROVED_PERSONNEL",
    payPeriod: { payPeriodNum: attendRecord.payPeriodNum },
  };
}

/** Orders records chronologically by begin date, then end date. */
export function compareRecords(lhs, rhs) {
  if (lhs.beginDate !== rhs.beginDate) {
    return lhs.beginDate < rhs.beginDate ? -1 : 1;
  }
  if (lhs.endDate !== rhs.endDate) {
    return lhs.endDate < rhs.endDate ? -1 : 1;
  }
  return 0;
}

/** True if the record contains any entry with a Regular/Special Annual pay type. */
export function hasAnnualEntries(record) {
  return (record.timeEntries || []).some((entry) =>
    ANNUAL_PAY_TYPES.includes(entry.payType),
  );
}

/** True if the record contains any entry with a Temporary pay type. */
export function hasTempEntries(record) {
  return (record.timeEntries || []).some((entry) =>
    TEMP_PAY_TYPES.includes(entry.payType),
  );
}

/** Formats an hour value for display, showing "--" when nothing was entered. */
export function entryHours(value) {
  return isNaN(parseInt(value)) ? "--" : value;
}
