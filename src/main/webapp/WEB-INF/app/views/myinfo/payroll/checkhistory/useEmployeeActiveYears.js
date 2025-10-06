import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

function getQueryKey(empId, useFiscalYears) {
  return ["employee", "detail", empId, "active-years", useFiscalYears];
}

export function useEmployeeActiveYears(empId, useFiscalYears) {
  return useQuery({
    queryKey: getQueryKey(empId, useFiscalYears),
    queryFn: () => {
      return fetchApiJson(
        `/employees/activeYears?empId=${empId}&fiscalYear=${useFiscalYears}`,
      ).then((body) => body.activeYears);
    },
    enabled: !!empId,
    staleTime: 1000 * 60 * 1,
    throwOnError: true,
  });
}
