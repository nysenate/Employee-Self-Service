import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

function getQueryKey() {
  return ["supply", "items", "list"];
}

export function useItems() {
  return useQuery({
    queryKey: getQueryKey(),
    queryFn: () => {
      return fetchApiJson(`/supply/items`).then((body) => body.result);
    },
    staleTime: 1000 * 60 * 5,
    throwOnError: true,
  });
}

// Returns a Map of itemId to item for all items
export function useItemsMap() {
  return useQuery({
    queryKey: getQueryKey(),
    queryFn: () => {
      return fetchApiJson(`/supply/items`).then((body) => body.result);
    },
    select: (data) =>
      data.reduce((acc, item) => {
        acc[item.id] = item;
        return acc;
      }, {}),
    staleTime: 1000 * 60 * 5,
    throwOnError: true,
  });
}
