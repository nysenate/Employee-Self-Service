import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

function getQueryKey() {
  return ["supply", "item", "list"];
}

// Returns a Map of itemId to item for all items
export function useItems() {
  return useQuery({
    queryKey: getQueryKey(),
    queryFn: () => {
      return fetchApiJson(`/supply/items`)
        .then((body) => body.result)
        .then((items) => new Map(items.map((i) => [i.id, i])));
    },
    staleTime: 60000,
    throwOnError: true,
  });
}
