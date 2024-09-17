import { fetchApiJson } from "app/utils/fetchJson";
import { useQuery } from "@tanstack/react-query";


export function useEmployeeActiveYears(empId, useFiscalYears) {
  return useQuery({
    queryKey: [ 'employee', 'active-years', empId, useFiscalYears ],
    queryFn: () => {
      return fetchApiJson(`/employees/activeYears?empId=${empId}&fiscalYear=${useFiscalYears}`)
        .then((body) => body.activeYears)
    },
    throwOnError: true,
  })
}