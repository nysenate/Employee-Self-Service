import { buildQueryString } from "app/utils/apiUtils";
import { fetchApiJson } from "app/api/fetchJson";
import { useQuery } from "@tanstack/react-query";

function getQueryKey(queryString) {
  return [("supply", "order-history", queryString)];
}

export function useOrderHisotry(queryParams) {
  const queryString = buildQueryString(queryParams);
  return useQuery({
    queryKey: getQueryKey(queryString),
    queryFn: ({ signal }) => {
      return fetchApiJson(`/supply/requisitions/orderHistory?${queryString}`, {
        signal,
      });
    },
    enabled: !!queryParams.location,
    staleTime: 1000 * 60,
    throwOnError: true,
  });
}
