import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

export function useResponsibilityCenterHeadSearch(term) {
  return useQuery({
    queryKey: ["respctr", "head", "search", term],
    queryFn: () => {
      return fetchApiJson(
        `/respctr/head/search?limit=ALL&offset=1&term=${term}`,
      ).then((body) => body.result);
    },
    staleTime: 1000 * 60 * 1,
    throwOnError: true,
  });
}
