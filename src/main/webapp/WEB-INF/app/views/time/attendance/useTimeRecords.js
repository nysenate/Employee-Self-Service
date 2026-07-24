import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

const ACTIVE_RECORDS_QUERY_KEY = ["timeRecords", "active"];

/**
 * Fetches the employee's active time records, i.e. those that are still open for entry or
 * review. Both employee and supervisor scoped records are returned.
 */
export function useActiveTimeRecords(empId) {
  return useQuery({
    queryKey: [...ACTIVE_RECORDS_QUERY_KEY, empId],
    queryFn: () => {
      return fetchApiJson(`/timerecords/active?empId=${empId}`).then(
        (body) => body.result.items[empId] || [],
      );
    },
    enabled: !!empId,
    staleTime: 1000 * 60,
    throwOnError: true,
  });
}

/**
 * Saves or submits a time record.
 * Call with { record, submit }, where submit sends the record on to the supervisor.
 *
 * A saved record is not reloaded here: a submitted record leaves the employee's hands, so
 * refetching would pull it out from under the confirmation the employee is still reading.
 * Call reloadTimeRecordData once they are done with it.
 */
export function useSaveTimeRecord() {
  return useMutation({
    mutationFn: ({ record, submit }) => {
      return fetchApiJson(`/timerecords?action=${submit ? "submit" : "save"}`, {
        method: "POST",
        payload: record,
      });
    },
  });
}

/**
 * Reloads everything a saved record affects: the records themselves, and the accruals,
 * allowances and grants that its hours are drawn from.
 */
export function reloadTimeRecordData(queryClient) {
  return Promise.all(
    [
      "timeRecords",
      "accruals",
      "allowances",
      "expectedHours",
      "miscLeaveGrants",
    ].map((key) => queryClient.invalidateQueries({ queryKey: [key] })),
  );
}

/**
 * Creates the employee's time record for the pay period containing the given date.
 * Call with { empId, date }.
 */
export function useCreateNextRecord() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ empId, date }) => {
      return fetchApiJson(`/timerecords/new?empId=${empId}&date=${date}`, {
        method: "POST",
        payload: {},
      });
    },
    onSuccess: () => {
      return queryClient.invalidateQueries({
        queryKey: ACTIVE_RECORDS_QUERY_KEY,
      });
    },
  });
}

/**
 * Fetches the employee's misc leave grants, each with the hours remaining on it as of the
 * given date. A grant with null hours remaining is unlimited.
 * @param empId The employee id.
 * @param endDate ISO date. Misc hours entered on or after this date are not counted as used.
 */
export function useMiscLeaveGrants(empId, endDate) {
  return useQuery({
    queryKey: ["miscLeaveGrants", empId, endDate],
    queryFn: () => {
      // A ListViewResponse serializes its list directly onto the result field.
      return fetchApiJson(
        `/miscleave/grantsWithRemainingHours?empId=${empId}&endDateStr=${endDate}`,
      ).then((body) => body.result);
    },
    enabled: !!empId && !!endDate,
    staleTime: 1000 * 60,
    throwOnError: true,
  });
}

/**
 * Fetches the years in which the employee has time records, most recent first.
 */
export function useActiveTimeRecordYears(empId) {
  return useQuery({
    queryKey: ["timeRecords", "activeYears", empId],
    queryFn: () => {
      return fetchApiJson(`/timerecords/activeYears?empId=${empId}`).then(
        (body) => [...body.years].reverse(),
      );
    },
    enabled: !!empId,
    staleTime: 1000 * 60 * 5,
    throwOnError: true,
  });
}

/**
 * Fetches the employee's electronic timesheets that overlap the given date range.
 * @param empId The employee id.
 * @param from Inclusive ISO start date, i.e. "2024-01-01".
 * @param to Exclusive ISO end date.
 */
export function useTimeRecords(empId, from, to) {
  return useQuery({
    queryKey: ["timeRecords", empId, from, to],
    queryFn: () => {
      return fetchApiJson(
        `/timerecords?empId=${empId}&from=${from}&to=${to}`,
      ).then((body) => body.result.items[empId] || []);
    },
    enabled: !!empId && !!from && !!to,
    staleTime: 1000 * 60,
    throwOnError: true,
  });
}

/**
 * Fetches the employee's attendance records (including paper timesheets) for a date range.
 */
export function useAttendanceRecords(empId, from, to) {
  return useQuery({
    queryKey: ["attendanceRecords", empId, from, to],
    queryFn: () => {
      return fetchApiJson(
        `/attendance/records?empId=${empId}&from=${from}&to=${to}`,
      ).then((body) => body.records);
    },
    enabled: !!empId && !!from && !!to,
    staleTime: 1000 * 60,
    throwOnError: true,
  });
}

function useMiscLeaveTypesQuery(select) {
  return useQuery({
    queryKey: ["miscLeaveTypes"],
    queryFn: () => {
      // A ListViewResponse serializes its list directly onto the result field.
      return fetchApiJson("/miscleave/types").then((body) => body.result);
    },
    select,
    staleTime: Infinity,
    throwOnError: true,
  });
}

/**
 * Fetches the display labels for every misc leave type, keyed by type name.
 */
export function useMiscLeaveTypes() {
  return useMiscLeaveTypesQuery(toMiscLeaveTypeMap);
}

/**
 * Fetches every misc leave type, in the order they are offered for selection.
 */
export function useMiscLeaveTypeList() {
  return useMiscLeaveTypesQuery();
}

function toMiscLeaveTypeMap(miscLeaveTypes) {
  return miscLeaveTypes.reduce((map, miscLeaveType) => {
    map[miscLeaveType.type] = miscLeaveType;
    return map;
  }, {});
}
