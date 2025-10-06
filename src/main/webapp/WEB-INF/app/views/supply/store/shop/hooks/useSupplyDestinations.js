import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

function getQueryKey(empId) {
  return ["supply", "destination", "list", empId];
}

export function useSupplyDestinations(empId) {
  return useQuery({
    queryKey: getQueryKey(empId),
    queryFn: () => {
      return fetchApiJson(`/supply/destinations/${empId}`).then(
        (body) => body.result,
      );
    },
    enable: !!empId,
    staleTime: 1000 * 60 * 1,
    throwOnError: true,
  });
}
