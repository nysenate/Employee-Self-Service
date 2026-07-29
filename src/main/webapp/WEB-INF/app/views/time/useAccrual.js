import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

function getQueryKey(empId, beforeDate) {
  return ["accruals", empId, beforeDate];
}

/**
 * Fetches the accruals available to an employee as of the given date.
 * @param empId The employee id.
 * @param beforeDate ISO date, i.e. "2026-01-01". Accruals are computed for the pay period
 *                   containing this date, counting only usage before it.
 * @param throwOnError Whether a failure should replace the page with an error. Pass false where
 *                     the accruals are supporting detail and the page can stand without them,
 *                     such as a supervisor reading an employee they may not have access to.
 */
export function useAccruals(empId, beforeDate, throwOnError = true) {
  return useQuery({
    queryKey: getQueryKey(empId, beforeDate),
    queryFn: () => {
      return fetchApiJson(
        `/accruals?empId=${empId}&beforeDate=${beforeDate}`,
      ).then((body) => body.result);
    },
    enabled: !!empId && !!beforeDate,
    staleTime: 1000 * 60,
    retry: throwOnError,
    throwOnError,
  });
}

/**
 * Fetches the years an employee has accrual records for, most recent first.
 */
export function useAccrualActiveYears(empId) {
  return useQuery({
    queryKey: ["accruals", "activeYears", empId],
    queryFn: () => {
      return fetchApiJson(`/accruals/active-years?empId=${empId}`).then(
        (body) => [...body.years].reverse(),
      );
    },
    enabled: !!empId,
    staleTime: 1000 * 60,
    throwOnError: true,
  });
}

/**
 * Fetches an employee's accrual summaries for every pay period in the given range,
 * in chronological order.
 *
 * Periods that have not been processed yet come back as computed records, which are the
 * projections shown on the Accrual Projections page.
 *
 * Errors are not thrown here: both pages that use this show a banner in place of the table
 * rather than replacing the page, so callers read isError instead.
 *
 * @param empId The employee id.
 * @param fromDate ISO date, inclusive.
 * @param toDate ISO date, exclusive.
 */
export function useAccrualHistory(empId, fromDate, toDate) {
  return useQuery({
    queryKey: ["accruals", "history", empId, fromDate, toDate],
    queryFn: () => {
      return fetchApiJson(
        `/accruals/history?empId=${empId}&fromDate=${fromDate}&toDate=${toDate}`,
      ).then((body) => body.result);
    },
    enabled: !!empId && !!fromDate && !!toDate,
    staleTime: 1000 * 60,
    retry: false,
  });
}
