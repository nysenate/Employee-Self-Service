import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { buildQueryString } from "app/utils/apiUtils";

function getQueryKey(queryString) {
  return ["supply", "reports", "item-summary", queryString];
}

export function useItemSummary(params) {
  const queryString = buildQueryString(params);
  return useQuery({
    queryKey: getQueryKey(queryString),
    queryFn: ({ signal }) =>
      fetchApiJson(`/supply/reports/item-summary?${queryString}`, {
        signal,
      }).then((body) => body.result),
  });
}
