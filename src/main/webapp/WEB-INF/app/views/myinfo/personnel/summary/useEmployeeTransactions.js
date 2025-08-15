import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { getEmployeeTransactions } from "app/api/employeeTransactions";

function getQueryKey(empId) {
  return ["employee", "detail", empId, "transactions"];
}

export function useEmployeeTransactions(empId) {
  return useQuery({
    queryKey: getQueryKey(empId),
    queryFn: () => {
      return getEmployeeTransactions(empId).then((body) => body.snapshot.items);
    },
    enabled: !!empId,
    staleTime: 60000,
    throwOnError: true,
  });
}
