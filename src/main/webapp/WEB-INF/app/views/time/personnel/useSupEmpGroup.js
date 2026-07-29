import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { possessive } from "app/utils/possessive";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";

/**
 * The user's extended supervisor employee group: every employee they may review, both the ones
 * reporting to them directly and the ones reporting to the supervisors beneath them.
 *
 * Ported from the legacy supEmpGroupService
 * (assets/js/src/time/personnel/sup-emp-group-service.js).
 */

/** The legacy service asked for the last two years of supervisor history. */
const HISTORY_YEARS = 2;

function fromDate() {
  const date = new Date();
  date.setFullYear(date.getFullYear() - HISTORY_YEARS);
  return date.toISOString().slice(0, 10);
}

/**
 * Fetches the user's extended supervisor employee group along with the supervisors who have
 * granted them override access, and derives the lookups the employee select needs.
 *
 * @param supId The supervisor's employee id.
 * @returns The query, extended with:
 *          supEmpGroups - the user's own group followed by every group beneath it,
 *          getName(empId) - the name of anyone in the extended group,
 *          getEmpInfos(index, omitSenators) - the employees of the group at that index.
 */
export function useSupEmpGroup(supId) {
  const query = useQuery({
    queryKey: ["supEmpGroup", supId],
    queryFn: async () => {
      const [group, overrides] = await Promise.all([
        fetchApiJson(
          `/supervisor/employees?supId=${supId}&fromDate=${fromDate()}&extended=true`,
        ).then((body) => body.result),
        fetchApiJson(`/supervisor/overrides?supId=${supId}`).then(
          (body) => body.overrides,
        ),
      ]);
      return { group, overrides };
    },
    enabled: !!supId,
    staleTime: 1000 * 60 * 5,
    throwOnError: true,
  });

  const { data: user } = useRequireAuthedUser();

  const derived = useMemo(
    () => buildSupEmpGroups(query.data, user),
    [query.data, user],
  );

  return { ...query, ...derived };
}

function buildSupEmpGroups(data, user) {
  const empty = {
    supEmpGroups: [],
    getName: () => undefined,
    getEmpInfos: () => [],
  };

  if (!data || !user) {
    return empty;
  }

  const { group, overrides } = data;
  const { nameMap, supIdMap } = buildEmpMaps(group, overrides, user);
  const getName = (empId) => nameMap[empId];

  /*
   * An employee's tier is how many supervisors sit between them and the top of the group. It
   * orders the group dropdown so that a supervisor always appears after the supervisor they
   * report to. Cycles are possible in the override data, so visited ids stop the walk.
   */
  const getTier = (empId) => {
    const visited = new Set([group.supId]);
    let current = empId;
    let tier = 0;
    while (current && !visited.has(current)) {
      tier++;
      visited.add(current);
      current = supIdMap[current];
    }
    return tier;
  };

  const secondaryGroups = Object.values(group.employeeSupEmpGroups || {})
    .flat()
    .map((empGroup) => ({
      ...empGroup,
      supStartDate: empGroup.effectiveFrom,
      supEndDate: empGroup.effectiveTo,
      empFirstName: getName(empGroup.supId)?.firstName,
      empLastName: getName(empGroup.supId)?.lastName,
    }))
    .sort(
      (a, b) =>
        getTier(a.supId) - getTier(b.supId) ||
        compare(a.empLastName, b.empLastName) ||
        compare(a.empFirstName, b.empFirstName) ||
        a.supId - b.supId,
    );

  const supEmpGroups = [group, ...secondaryGroups].map((empGroup) =>
    labelSupEmpGroup(empGroup, group.supId, getName),
  );

  /**
   * The employees of the group at the given index: the supervisor's own employees first, then,
   * for the user's own group only, the employees they hold an override on.
   */
  const getEmpInfos = (index, omitSenators) => {
    const empGroup = supEmpGroups[index];
    if (!empGroup) {
      return [];
    }

    const primary = [...(empGroup.primaryEmployees || [])].sort(
      (a, b) =>
        compare(a.empLastName, b.empLastName) ||
        compare(a.empFirstName, b.empFirstName) ||
        a.empId - b.empId,
    );

    const emps = [...primary];

    // Overrides only ever apply to the user's own group, never to a group beneath it.
    if (empGroup.supId === group.supId) {
      (empGroup.empOverrideEmployees || []).forEach((emp) =>
        emps.push({ ...emp, empOverride: true }),
      );
      Object.values(empGroup.supOverrideEmployees?.items || {})
        .flat()
        .forEach((emp) => emps.push({ ...emp, supOverride: true }));
    }

    return emps
      .filter((emp) => !(omitSenators && emp.senator))
      .map((emp) => labelEmployee(emp, getName));
  };

  return { supEmpGroups, getName, getEmpInfos };
}

/**
 * Maps every employee id in the extended group to their name, and every employee to their
 * supervisor. Override supervisors are included, since they can head a group of their own.
 */
function buildEmpMaps(group, overrides, user) {
  const nameMap = {};
  const supIdMap = {};

  const allEmpInfos = [
    ...(group.primaryEmployees || []),
    ...(group.empOverrideEmployees || []),
    ...Object.values(group.supOverrideEmployees?.items || {}).flat(),
    ...Object.values(group.employeeSupEmpGroups || {})
      .flat()
      .flatMap((empGroup) => empGroup.primaryEmployees || []),
  ];

  allEmpInfos.forEach((empInfo) => {
    nameMap[empInfo.empId] = {
      firstName: empInfo.empFirstName,
      lastName: empInfo.empLastName,
      fullName: `${empInfo.empFirstName} ${empInfo.empLastName}`,
    };
    // An employee override is granted directly, so it says nothing about who supervises them.
    if (!empInfo.empOverride) {
      supIdMap[empInfo.empId] = empInfo.supId;
    }
  });

  (overrides || []).forEach((override) => {
    const granter = override.overrideSupervisor;
    nameMap[granter.employeeId] = {
      firstName: granter.firstName,
      lastName: granter.lastName,
      fullName: granter.fullName,
    };
    supIdMap[override.overrideSupervisorId] = override.supervisorId;
  });

  nameMap[user.employeeId] = {
    firstName: user.firstName,
    lastName: user.lastName,
    fullName: `${user.firstName} ${user.lastName}`,
  };

  return { nameMap, supIdMap };
}

/**
 * Labels a supervisor group for the group dropdown. The user's own group is listed under their
 * full name; every other group is grouped under the supervisor it reports to.
 */
function labelSupEmpGroup(empGroup, userSupId, getName) {
  if (empGroup.supId === userSupId) {
    return { ...empGroup, dropDownLabel: getName(empGroup.supId)?.fullName };
  }
  return {
    ...empGroup,
    group: `Supervisors Under ${getName(empGroup.supSupId)?.fullName}`,
    dropDownLabel: dropDownLabel(empGroup, false),
  };
}

/** Labels an employee for the employee dropdown, grouped by how the user came to supervise them. */
function labelEmployee(emp, getName) {
  let group;
  if (emp.empOverride) {
    group = "Employee Overrides";
  } else if (emp.supOverride) {
    const supName = getName(emp.supId);
    group = supName?.lastName
      ? `${possessive(supName.lastName)} Employees`
      : "Sup Override Employees";
  } else {
    group = "Direct Employees";
  }
  return {
    ...emp,
    group,
    dropDownLabel: dropDownLabel(emp, emp.empOverride || emp.supOverride),
  };
}

/**
 * "Smith J. (Jan 2024 - Present)": the name, then the months the user's authority over them
 * covers. An override runs for its own effective dates rather than the supervisor's.
 */
function dropDownLabel(emp, override) {
  const start = parseDate(
    (override ? emp.effectiveStartDate : emp.supStartDate) || "1970-01-01",
  );
  const end = parseDate(
    (override ? emp.effectiveEndDate : emp.supEndDate) || "2999-12-31",
  );

  const name = `${emp.empLastName} ${emp.empFirstName?.charAt(0)}.`;

  let dates = formatMonth(start);
  if (!isBeforeToday(end)) {
    dates += " - Present";
  } else if (monthIndex(start) < monthIndex(end)) {
    dates += ` - ${formatMonth(end)}`;
  }

  return `${name} (${dates})`;
}

/* --- Date helpers, kept local so the labels do not depend on a date library --- */

function parseDate(isoDate) {
  const [year, month, day] = isoDate.split("-").map(Number);
  return new Date(year, month - 1, day);
}

function formatMonth(date) {
  return date.toLocaleDateString("en-US", { month: "short", year: "numeric" });
}

function monthIndex(date) {
  return date.getFullYear() * 12 + date.getMonth();
}

function isBeforeToday(date) {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return date < today;
}

function compare(a, b) {
  return (a || "").toUpperCase().localeCompare((b || "").toUpperCase());
}

/**
 * True when the user still supervises this employee today. The legacy filter compared against
 * whichever end date the record carries.
 */
export function isCurrentlySupervised(empInfo) {
  const endDate = empInfo.effectiveEndDate || empInfo.supEndDate;
  return !endDate || !isBeforeToday(parseDate(endDate));
}
