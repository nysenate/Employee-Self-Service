import { useMutation, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { travelQueryKeys } from "app/views/travel/shared/hooks/travelQueryKeys";

export function useSubmitTravelApplication() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (draft) =>
      fetchApiJson("/travel/drafts/submit", {
        method: "POST",
        payload: draft,
      }).then((body) => body.result),
    onSuccess: () =>
      queryClient.invalidateQueries({
        queryKey: travelQueryKeys.all,
        refetchType: "none",
      }),
  });
}
