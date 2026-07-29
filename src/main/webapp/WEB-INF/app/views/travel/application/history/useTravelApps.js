import { buildQueryString } from "app/utils/apiUtils";
import { fetchApiJson } from "app/api/fetchJson";
import { useQuery } from "@tanstack/react-query";

function getQueryKey(queryString) {
  return ["travel", "applications", queryString];
}

export function useTravelApps(params) {
  const queryString = buildQueryString(params);
  return useQuery({
    queryKey: getQueryKey(queryString),
    queryFn: () => fetchApiJson(`/travel/applications?${queryString}`),
    staleTime: 0,
    throwOnError: true,
  });
}
