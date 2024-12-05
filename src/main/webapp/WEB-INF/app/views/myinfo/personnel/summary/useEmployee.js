import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { getEmployee } from "app/api/employee";


function getQueryKey(empId, detail) {
  return ['employee', 'detail', empId, 'full', detail]
}

export function useEmployee(empId, detail = true) {
  return useQuery({
    queryKey: getQueryKey(empId, detail),
    queryFn: () => {
      return getEmployee(empId, detail)
        .then((body) => body.employee)
    },
    staleTime: 60000,
    throwOnError: true,
  })
}
