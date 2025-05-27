import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

export function useItems() {
  return useQuery({
    queryKey: ["supply", "items", "list"],
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
    queryKey: ["supply", "items", "map"],
    queryFn: () => {
      return fetchApiJson(`/supply/items`).then((body) => body.result);
    },
    select: (data) => {
      const map = new Map();
      data.forEach((item) => map.set(item.id, item));
      return map;
    },
    staleTime: 1000 * 60 * 5,
    throwOnError: true,
  });
}
