import { fetchApiJson } from "app/utils/fetchJson";
import { useQuery } from "@tanstack/react-query";


export function useEmployeePaychecks(empId, year, useFiscalYear) {
  return useQuery({
    queryKey: [ 'employee', 'paychecks', empId, year, useFiscalYear ],
    queryFn: () => {
      return fetchApiJson(`/paychecks?empId=${empId}&year=${year}&fiscalYear=${useFiscalYear}`)
        .then((body) => body.result)
    },
    throwOnError: true,
  })
}