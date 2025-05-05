import { fetchApiJson } from "app/api/fetchJson";
import { useQuery } from "@tanstack/react-query";

function getQueryKeys(itemId) {
  return ["supply", "item", "detail", itemId];
}

export function useItem(itemId) {
  const queryParams = getQueryKeys(itemId);
  return useQuery({
    queryKey: getQueryKeys(itemId),
    queryFn: () =>
      fetchApiJson(`/supply/items/${itemId}`).then((body) => body.result),
    staleTime: 60000,
    throwOnError: true,
  });
}
