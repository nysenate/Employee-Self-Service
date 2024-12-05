import { useQuery } from "@tanstack/react-query";
import { getEmployeePaychecks } from "app/api/employeePaychecks";


function getQueryKey(empId, year, useFiscalYear) {
  return ['employee', 'detail', empId, 'paychecks', year, useFiscalYear]
}

export function useEmployeePaychecks(empId, year, useFiscalYear) {
  return useQuery({
    queryKey: getQueryKey(empId, year, useFiscalYear),
    queryFn: () => {
      return getEmployeePaychecks(empId, year, useFiscalYear)
        .then((body) => body.result)
    },
    staleTime: 60000,
    throwOnError: true,
  })
}