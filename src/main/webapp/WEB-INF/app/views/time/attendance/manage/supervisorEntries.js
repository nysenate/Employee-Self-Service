import { possessive } from "app/utils/possessive";
import {
  getRecordTotals,
  TIME_ENTRY_FIELDS,
  withDailyTotals,
} from "app/views/time/attendance/recordUtils";

/**
 * Builds the entries of the "View Employees Under" dropdown on the Review Time Records page,
 * and sorts the records fetched for an entry into the sections the page shows.
 *
 * Ported from the internal methods of the legacy RecordManageCtrl
 * (assets/js/src/time/record/record-manage-ctrl.js).
 */

/**
 * Every supervisor whose records the user may review: the user themselves, each override they
 * hold, and each of their employees who is a supervisor in their own right.
 *
 * @param supEmpGroups The user's own group followed by the groups beneath it.
 * @param getName Looks up a name by employee id.
 * @param userEmpId The logged in user's employee id.
 */
export function buildSupervisorEntries(supEmpGroups, getName, userEmpId) {
  const extSupEmpGroup = supEmpGroups[0];
  if (!extSupEmpGroup) {
    return [];
  }

  return [
    ...primaryEntries(extSupEmpGroup, getName, userEmpId),
    ...supOverrideEntries(extSupEmpGroup, getName, userEmpId),
    ...empOverrideEntries(extSupEmpGroup, getName, userEmpId),
    ...extendedEntries(supEmpGroups, getName),
  ];
}

/**
 * The user's own entries: one covering everything they are responsible for, and, when they hold
 * overrides, a second covering only the employees who report to them directly.
 */
function primaryEntries(extSupEmpGroup, getName, userEmpId) {
  const name = getName(userEmpId);
  const hasOverrides =
    (extSupEmpGroup.supOverrideEmployees?.size ?? 0) > 0 ||
    (extSupEmpGroup.empOverrideEmployees?.length ?? 0) > 0;

  const baseLabel = hasOverrides
    ? `${name?.fullName} + Overrides`
    : name?.fullName;

  const entries = [
    {
      querySupId: userEmpId,
      supId: userEmpId,
      name,
      baseLabel,
      dropDownLabel: baseLabel,
      fullEmpGroup: true,
    },
  ];

  if (hasOverrides) {
    entries.push({
      querySupId: userEmpId,
      supId: userEmpId,
      name,
      baseLabel: name?.fullName,
      dropDownLabel: name?.fullName,
      fullEmpGroup: false,
    });
  }

  return entries;
}

/** One entry per supervisor who has handed the user their whole employee group. */
function supOverrideEntries(extSupEmpGroup, getName, userEmpId) {
  return Object.keys(extSupEmpGroup.supOverrideEmployees?.items || {})
    .map((supId) => entryForOverride(parseInt(supId), getName, userEmpId))
    .map((entry) => ({ ...entry, group: "Supervisor Overrides" }))
    .sort(byDropDownLabel);
}

/** One entry per single employee the user has been given responsibility for. */
function empOverrideEntries(extSupEmpGroup, getName, userEmpId) {
  return (extSupEmpGroup.empOverrideEmployees || [])
    .map((empInfo) => ({
      ...entryForOverride(empInfo.empId, getName, userEmpId),
      supId: empInfo.supId,
      ovrEmpId: empInfo.empId,
      group: "Employee Overrides",
      empOverride: true,
      supOverride: false,
    }))
    .sort(byDropDownLabel);
}

function entryForOverride(supId, getName, userEmpId) {
  const name = getName(supId);
  const baseLabel = supNameLabel(name);
  return {
    // Override records still come back under the user's own supervisor id.
    querySupId: userEmpId,
    supId,
    name,
    baseLabel,
    dropDownLabel: baseLabel,
    supOverride: true,
  };
}

/**
 * One entry per supervisor beneath the user, so they can look further down the chain. Groups
 * that ended more than a year ago are dropped: their records are long since processed.
 */
function extendedEntries(supEmpGroups, getName) {
  const aYearAgo = new Date();
  aYearAgo.setFullYear(aYearAgo.getFullYear() - 1);
  const cutoff = aYearAgo.toISOString().slice(0, 10);

  return supEmpGroups
    .slice(1)
    .filter(
      (empGroup) => !empGroup.effectiveTo || cutoff < empGroup.effectiveTo,
    )
    .map((empGroup) => {
      const name = getName(empGroup.supId);
      const baseLabel = supNameLabel(name);
      return {
        querySupId: empGroup.supId,
        supId: empGroup.supId,
        name,
        group: `Supervisors Under ${getName(empGroup.supSupId)?.fullName}`,
        baseLabel,
        dropDownLabel: baseLabel,
        extendedSup: true,
        effectiveFrom: empGroup.effectiveFrom,
        effectiveTo: empGroup.effectiveTo,
      };
    });
}

function supNameLabel(name) {
  return `${name?.lastName}, ${name?.firstName}`;
}

function byDropDownLabel(a, b) {
  return a.dropDownLabel.localeCompare(b.dropDownLabel);
}

/**
 * The date range to request an entry's records over: the last year through next month, clipped
 * to the dates the user's view of an indirect supervisor's group is effective for.
 */
export function recordDateRange(entry) {
  const from = new Date();
  from.setFullYear(from.getFullYear() - 1);
  const to = new Date();
  to.setMonth(to.getMonth() + 1);

  let fromDate = toIsoDate(from);
  let toDate = toIsoDate(to);

  if (entry.extendedSup) {
    fromDate = maxDate(fromDate, entry.effectiveFrom);
    toDate = minDate(toDate, entry.effectiveTo);
  }

  return { from: fromDate, to: toDate };
}

/**
 * Sorts the records fetched for an entry into lists by status, dropping the ones the entry is
 * not responsible for.
 *
 * @param entry The selected supervisor entry.
 * @param recordMap Records keyed by employee id, as returned by the supervisor records endpoint.
 * @param userEmpId The logged in user's employee id.
 * @returns Records by status, i.e. { SUBMITTED: [...], NOT_SUBMITTED: [...] }.
 */
export function groupRecordsByStatus(entry, recordMap, userEmpId) {
  const isUser = entry.querySupId === userEmpId;
  const byStatus = {};

  Object.values(recordMap || {})
    .flat()
    .filter((record) => {
      // An override on one employee does not extend to the rest of that supervisor's group.
      if (!isUser && record.supervisorId !== entry.querySupId) {
        return false;
      }
      // A temporary employee's untouched record is nothing for a supervisor to act on.
      if (
        record.scope === "E" &&
        isFullTempRecord(record) &&
        !recordHasEnteredTime(record)
      ) {
        return false;
      }
      // Outside the full group view, only the records this entry actually covers.
      return (
        entry.fullEmpGroup ||
        (entry.supId === record.supervisorId &&
          (!entry.ovrEmpId || entry.ovrEmpId === record.employeeId))
      );
    })
    .map(withTotals)
    .forEach((record) => {
      byStatus[record.recordStatus] = byStatus[record.recordStatus] || [];
      byStatus[record.recordStatus].push(record);
    });

  Object.values(byStatus).forEach((records) =>
    records.sort(byEmployeeThenDate),
  );

  return byStatus;
}

function withTotals(record) {
  const withDaily = withDailyTotals(record);
  return { ...withDaily, totals: getRecordTotals(withDaily) };
}

function byEmployeeThenDate(a, b) {
  return (
    (a.employee?.lastName || "").localeCompare(b.employee?.lastName || "") ||
    (a.employee?.firstName || "").localeCompare(b.employee?.firstName || "") ||
    a.beginDate.localeCompare(b.beginDate)
  );
}

/** True if every entry on the record was made while the employee was temporary. */
function isFullTempRecord(record) {
  const entries = record.timeEntries || [];
  return entries.length > 0 && entries.every((entry) => entry.payType === "TE");
}

/** True if any hour field anywhere on the record has been filled in. */
function recordHasEnteredTime(record) {
  return (record.timeEntries || []).some((entry) =>
    TIME_ENTRY_FIELDS.some((field) => !isNaN(parseInt(entry[field]))),
  );
}

function toIsoDate(date) {
  return [
    date.getFullYear(),
    String(date.getMonth() + 1).padStart(2, "0"),
    String(date.getDate()).padStart(2, "0"),
  ].join("-");
}

function maxDate(a, b) {
  return !b || a > b ? a : b;
}

function minDate(a, b) {
  return !b || a < b ? a : b;
}

/**
 * The pending record counts shown in the dropdown labels. Only the user's own entries carry
 * them, since an indirect supervisor's records are not the user's to act on.
 */
export function withPendingCounts(entries, recordsByEntry) {
  return entries.map((entry, index) => {
    if (entry.extendedSup) {
      return entry;
    }
    const pending = (recordsByEntry[index]?.SUBMITTED || []).length;
    return {
      ...entry,
      dropDownLabel: `${entry.baseLabel} - (${pending} Pending Records)`,
    };
  });
}

/** Describes an entry in the "no records need action" notice. */
export function noRecordsNotice(entry) {
  if (!entry || entry.fullEmpGroup) {
    return {
      title: "No time records need action.",
      message: "There are currently no records that require approval.",
    };
  }
  const fullName = entry.name?.fullName;
  return {
    title: `None of ${possessive(fullName)} time records need action.`,
    message: `There are currently no records under ${fullName} that require approval.`,
  };
}
