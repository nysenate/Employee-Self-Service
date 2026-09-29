import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

function getQueryKey(empId, year, useFiscalYear) {
  return ["employee", "detail", empId, "paychecks", year, useFiscalYear];
}

export function useEmployeePaychecks(empId, year, useFiscalYear) {
  return useQuery({
    queryKey: getQueryKey(empId, year, useFiscalYear),
    queryFn: () => {
      return fetchApiJson(
        `/paychecks?empId=${empId}&year=${year}&fiscalYear=${useFiscalYear}`,
      ).then((body) => ({ ...body.result, year, useFiscalYear }));
    },
    enabled: !!empId && Number.isFinite(year),
    staleTime: 1000 * 60 * 1,
    placeholderData: (previousData, previousQuery) =>
      previousQuery?.queryKey[2] === empId ? previousData : undefined,
    throwOnError: false,
  });
}
