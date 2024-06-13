import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/utils/fetchJson";


export function useEmployeeDetails(empId) {
  return useQuery({
    queryKey: [ 'employee', empId ],
    queryFn: () => {
      return fetchApiJson(`/employees?detail=true&empId=${empId}`)
        .then((body) => body.employee)
    }
  })
}