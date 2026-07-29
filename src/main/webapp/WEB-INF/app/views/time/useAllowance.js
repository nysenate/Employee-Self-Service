import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

function getQueryKey(empId, year) {
  return ["allowances", empId, year];
}

/**
 * Fetches a temporary employee's allowance usage for the given year.
 * @param empId The employee id.
 * @param year The four digit year.
 */
export function useAllowance(empId, year) {
  return useQuery({
    queryKey: getQueryKey(empId, year),
    queryFn: () => {
      // A ListViewResponse serializes its list directly onto the result field.
      return fetchApiJson(`/allowances?empId=${empId}&year=${year}`).then(
        (body) => body.result[0] || null,
      );
    },
    enabled: !!empId && !!year,
    staleTime: 1000 * 60,
    throwOnError: true,
  });
}

/**
 * Fetches the years an employee has an allowance for, most recent first.
 */
export function useAllowanceActiveYears(empId) {
  return useQuery({
    queryKey: ["allowances", "activeYears", empId],
    queryFn: () => {
      return fetchApiJson(`/allowances/active-years?empId=${empId}`).then(
        (body) => [...body.years].reverse(),
      );
    },
    enabled: !!empId,
    staleTime: 1000 * 60,
    throwOnError: true,
  });
}

/**
 * Fetches a temporary employee's allowance usage broken down by pay period for the
 * given year, in reverse chronological order.
 * @param empId The employee id.
 * @param year The four digit year.
 */
export function usePeriodAllowanceUsage(empId, year) {
  return useQuery({
    queryKey: ["allowances", "period", empId, year],
    queryFn: () => {
      // A ListViewResponse serializes its list directly onto the result field.
      return fetchApiJson(
        `/allowances/period?empId=${empId}&year=${year}`,
      ).then((body) => [...body.result].reverse());
    },
    enabled: !!empId && !!year,
    staleTime: 1000 * 60,
    throwOnError: true,
  });
}
