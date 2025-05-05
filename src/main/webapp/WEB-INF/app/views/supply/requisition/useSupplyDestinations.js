import { getSupplyDestinations } from "app/api/supplyDestinations";
import { useQuery } from "@tanstack/react-query";

function getQueryKey(empId) {
  return ["supply", "destination", "list", empId];
}

export function useSupplyDestinations(empId) {
  return useQuery({
    queryKey: getQueryKey(empId),
    queryFn: () => {
      return getSupplyDestinations(empId).then((body) => body.result);
    },
    staleTime: 60000,
    throwOnError: true,
  });
}
