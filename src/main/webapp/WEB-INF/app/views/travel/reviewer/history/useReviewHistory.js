import { buildQueryString } from "app/utils/apiUtils";
import { fetchApiJson } from "app/api/fetchJson";
import { useQuery } from "@tanstack/react-query";

function getQueryKey(params) {
  return ["travel", "review", "history", params];
}

export function useReviewHistory(params) {
  const queryString = buildQueryString(params);
  return useQuery({
    queryKey: getQueryKey(params),
    queryFn: () => fetchApiJson(`/travel/review/history?${queryString}`),
    staleTime: 0,
    throwOnError: true,
  });
}
