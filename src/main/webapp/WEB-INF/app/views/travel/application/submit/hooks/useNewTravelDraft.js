import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { travelQueryKeys } from "app/views/travel/shared/hooks/travelQueryKeys";

export function useNewTravelDraft() {
  return useQuery({
    queryKey: travelQueryKeys.newDraft(),
    queryFn: () =>
      fetchApiJson("/travel/drafts", { method: "PUT" }).then(
        (body) => body.result,
      ),
    retry: false,
    staleTime: Infinity,
  });
}
