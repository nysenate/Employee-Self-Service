import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

function getQueryKey(empId) {
  return ["employee", "detail", empId, "transactions"];
}

export function useEmployeeTransactions(empId) {
  return useQuery({
    queryKey: getQueryKey(empId),
    queryFn: () => {
      return fetchApiJson(
        `/empTransactions/snapshot/current?empId=${empId}`,
      ).then((body) => body.snapshot.items);
    },
    enabled: !!empId,
    staleTime: 1000 * 60 * 1,
    throwOnError: true,
  });
}
