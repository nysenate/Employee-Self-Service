import { useQuery } from "@tanstack/react-query";
import { getEmployeeActiveYears } from "app/api/employeeActiveYears";


function getQueryKey(empId, useFiscalYears) {
  return ['employee', 'detail', empId, 'active-years', useFiscalYears]
}

export function useEmployeeActiveYears(empId, useFiscalYears) {
  return useQuery({
    queryKey: getQueryKey(empId, useFiscalYears),
    queryFn: () => {
      return getEmployeeActiveYears(empId, useFiscalYears)
        .then((body) => body.activeYears)
    },
    staleTime: 60000,
    throwOnError: true,
  })
}