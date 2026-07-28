import { fetchApiJson } from "app/api/fetchJson";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

function getQueryKey() {
  return ["travel", "drafts"];
}

export function useDrafts() {
  return useQuery({
    queryKey: getQueryKey(),
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
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: getQueryKey() });
    },
  });
}
