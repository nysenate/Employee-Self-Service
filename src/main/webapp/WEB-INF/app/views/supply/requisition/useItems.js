import { fetchApiJson } from "app/api/fetchJson";
import { useQuery } from "@tanstack/react-query";
import { buildQueryString } from "app/utils/apiUtils";

function getQueryKey(locId, categories, term, sort, limit, offset) {
  return [
    "supply",
    "item",
    "list",
    locId,
    categories,
    term,
    sort,
    limit,
    offset,
  ];
}

// export function useItems({locId, categories, term, sort, limit, offset}) {
export function useItems(locId, filterState) {
  const params = {
    locId,
    ...filterState,
  };
  const queryParams = buildQueryString(params);
  return useQuery({
    queryKey: getQueryKey(
      params.locId,
      params.categories,
      params.term,
      params.sort,
      params.limit,
      params.offset,
    ),
    queryFn: () => {
      return fetchApiJson(`/supply/items?${queryParams}`);
    },
    staleTime: 60000,
    throwOnError: true,
  });
}
