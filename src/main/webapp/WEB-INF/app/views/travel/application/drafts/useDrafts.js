import { fetchApiJson } from "app/api/fetchJson";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { travelQueryKeys } from "app/views/travel/shared/hooks/travelQueryKeys";

export function useDrafts() {
  return useQuery({
    queryKey: travelQueryKeys.drafts(),
    queryFn: () => fetchApiJson(`/travel/drafts`),
    staleTime: 0,
    throwOnError: true,
  });
}

export function useMutateDraft() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (draftId) =>
      fetchApiJson(`/travel/drafts/${draftId}`, { method: "DELETE" }),
    onSuccess: (_response, draftId) => {
      queryClient.removeQueries({
        queryKey: travelQueryKeys.draft(draftId),
        exact: true,
      });
      queryClient.invalidateQueries({
        queryKey: travelQueryKeys.drafts(),
        exact: true,
      });
    },
  });
}
