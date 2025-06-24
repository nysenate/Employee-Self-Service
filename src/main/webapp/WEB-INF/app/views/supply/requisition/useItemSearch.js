import { fetchApiJson } from "app/api/fetchJson";
import { useQuery } from "@tanstack/react-query";
import { buildQueryString } from "app/utils/apiUtils";
import { itemKeys } from "app/views/supply/item.queryKeys";

export function useItemSearch(locId, filterState) {
  const params = {
    locId,
    ...filterState,
  };
  const queryParams = buildQueryString(params);
  return useQuery({
    queryKey: itemKeys.search(params),
    queryFn: () => {
      return fetchApiJson(`/supply/items?${queryParams}`);
    },
    staleTime: 1000 * 60 * 5,
    throwOnError: true,
  });
}
