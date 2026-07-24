import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

function getQueryKey(empId, beginDate, endDate) {
  return ["expectedHours", empId, beginDate, endDate];
}

/**
 * Fetches the hours an employee is expected to work over the given date range.
 * @param empId The employee id.
 * @param beginDate Inclusive ISO start date.
 * @param endDate Inclusive ISO end date.
 */
export function useExpectedHours(empId, beginDate, endDate) {
  return useQuery({
    queryKey: getQueryKey(empId, beginDate, endDate),
    queryFn: () => {
      return fetchApiJson(
        `/expectedhrs?empId=${empId}&beginDate=${beginDate}&endDate=${endDate}`,
      ).then((body) => body.result);
    },
    enabled: !!empId && !!beginDate && !!endDate,
    staleTime: 1000 * 60,
    throwOnError: true,
  });
}
