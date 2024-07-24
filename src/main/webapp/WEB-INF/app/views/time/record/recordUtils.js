const timeEntryFields = [
  'workHours',
  'travelHours',
  'holidayHours',
  'vacationHours',
  'personalHours',
  'sickEmpHours',
  'sickFamHours',
  'miscHours'
];

export function getDailyTotal(entry) {
  return timeEntryFields
    .map(timeField => {
      const fieldValue = entry[timeField];
      return isNaN(fieldValue) ? 0 : +fieldValue;
    })
    .reduce((a, b) => a + b, 0);
}

export function calculateDailyTotals(record) {
  const entries = record.timeEntries;
  for (let i = 0; i < entries.length; i++) {
    entries[i].total = getDailyTotal(entries[i]);
  }
}

export function getTotal(record, type, payTypes) {
  let total = 0;
  const entries = record.timeEntries;
  if (entries) {
    for (let i = 0; i < entries.length; i++) {
      if (!payTypes || payTypes.includes(entries[i].payType)) {
        total += +(entries[i][type] || 0);
      }
    }
  }
  return total;
}

export function getRecordTotals(record) {
  const totals = {};

  for (const field of timeEntryFields) {
    totals[field] = getTotal(record, field);
  }
  totals.raSaWorkHours = getTotal(record, 'workHours', ['RA', 'SA']);
  totals.tempWorkHours = getTotal(record, 'workHours', ['TE']);
  totals.raSaTotal = getTotal(record, 'total', ['RA', 'SA']);
  totals.total = getTotal(record, 'total');
  return totals;
}

export function formatAttendRecord(attendRecord) {
  attendRecord.totals = {
    workHours: attendRecord.workHours,
    holidayHours: attendRecord.holidayHours,
    vacationHours: attendRecord.vacationHours,
    personalHours: attendRecord.personalHours,
    sickEmpHours: attendRecord.sickEmpHours,
    sickFamHours: attendRecord.sickFamHours,
    miscHours: attendRecord.miscHours,
    total: attendRecord.totalHours
  };
  attendRecord.recordStatus = 'APPROVED_PERSONNEL';
  attendRecord.payPeriod = { payPeriodNum: attendRecord.payPeriodNum };
  return attendRecord;
}

export function compareRecords(lhs, rhs) {
  const lhsBegin = new Date(lhs.beginDate);
  const rhsBegin = new Date(rhs.beginDate);

  if (lhsBegin < rhsBegin) return -1;
  if (lhsBegin > rhsBegin) return 1;

  const lhsEnd = new Date(lhs.endDate);
  const rhsEnd = new Date(rhs.endDate);

  if (lhsEnd < rhsEnd) return -1;
  if (lhsEnd > rhsEnd) return 1;

  return 0;
}

export function entryHasEnteredTime(timeEntry) {
  for (const field of timeEntryFields) {
    if (field in timeEntry && !isNaN(parseInt(timeEntry[field]))) {
      return true;
    }
  }
  return false;
}

export function recordHasEnteredTime(timeRecord) {
  const entries = timeRecord.timeEntries;
  for (const entry of entries) {
    if (entryHasEnteredTime(entry)) {
      return true;
    }
  }
  return false;
}

export function isFullTempRecord(timeRecord) {
  const entries = timeRecord.timeEntries;
  if (entries.length === 0) {
    return false;
  }
  for (const entry of entries) {
    if (entry.payType !== 'TE') {
      return false;
    }
  }
  return true;
}

export function getTimeEntryFields() {
  return [...timeEntryFields];
}
