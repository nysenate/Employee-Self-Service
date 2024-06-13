import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/utils/fetchJson";


export function useEmployeeTransactions(empId) {
  return useQuery({
    queryKey: [ 'employee', 'transactions', empId ],
    queryFn: () => {
      return fetchApiJson(`/empTransactions/snapshot/current?empId=${empId}`)
        .then((body) => body.snapshot.items)
    }
  })
}
