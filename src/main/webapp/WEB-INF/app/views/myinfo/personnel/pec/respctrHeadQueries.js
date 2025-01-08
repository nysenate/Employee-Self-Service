import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { searchResponsibilityCenterHeads } from "app/api/responsibilityCenterHead";

export function useResponsibilityCenterHeadSearch(term) {
  return useQuery({
    queryKey: ['respctr', 'head', 'search', term],
    queryFn: () => {
      return searchResponsibilityCenterHeads(term)
        .then(body => body.result)
    },
    staleTime: 60000,
    throwOnError: true,
  })
}