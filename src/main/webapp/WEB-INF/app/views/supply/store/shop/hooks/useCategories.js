import { fetchApiJson } from "app/api/fetchJson";
import { buildQueryString } from "app/utils/apiUtils";
import { useQuery } from "@tanstack/react-query";

function getQueryKey(locId, term) {
  return ["supply", "item", "category", "list", locId, term];
}

export function useCategories(locId, term) {
  const queryParams = buildQueryString({ locId, term });
  return useQuery({
    queryKey: getQueryKey(locId, term),
    queryFn: () => {
      return fetchApiJson(`/supply/items/categories?${queryParams}`).then(
        (body) => body.categories,
      );
    },
    staleTime: 60000,
    throwOnError: true,
  });
}
