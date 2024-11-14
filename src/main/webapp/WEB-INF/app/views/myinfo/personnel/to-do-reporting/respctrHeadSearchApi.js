import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/utils/fetchJson";

// /api/v1/respctr/head/search?limit=20&offset=1&term=tedi
export function useResponsibilityCenterHeadSearch(term) {
  return useQuery({
    queryKey: ['respctr', 'head', 'search', term],
    queryFn: () => {
      return fetchApiJson(`/respctr/head/search?limit=ALL&offset=1&term=${term}`)
        .then(body => body.result)
    },
  })
}