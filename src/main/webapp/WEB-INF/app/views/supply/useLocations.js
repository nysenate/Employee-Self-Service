import { fetchApiJson } from "app/api/fetchJson";
import { useQuery } from "@tanstack/react-query";

function getQueryKey() {
  return ["supply", "locations", "list"];
}

export function useLocations() {
  return useQuery({
    queryKey: getQueryKey(),
    queryFn: () => {
      return fetchApiJson(`/locations`).then((body) => body.result);
    },
    staleTime: 1000 * 60 * 5,
    throwOnError: true,
  });
}
