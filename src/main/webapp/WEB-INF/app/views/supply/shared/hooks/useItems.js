import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { itemKeys } from "app/views/supply/shared/hooks/item.queryKeys";

// Returns a Map of itemId to item for all supply items.
export function useItemsMap() {
  return useQuery({
    queryKey: itemKeys.list(),
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
