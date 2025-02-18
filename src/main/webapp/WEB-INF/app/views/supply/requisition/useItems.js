import { fetchApiJson } from "app/api/fetchJson";
import { useQuery } from "@tanstack/react-query";

function getQueryKey(locId) {
  return ["supply", "item", "list", locId];
}

export function useItems(locId) {
  return useQuery({
    queryKey: getQueryKey(locId),
    queryFn: () => {
      return fetchApiJson(`/supply/items/orderable/${locId}`).then(
        (body) => body.result,
      );
    },
    staleTime: 60000,
    throwOnError: true,
  });
}
