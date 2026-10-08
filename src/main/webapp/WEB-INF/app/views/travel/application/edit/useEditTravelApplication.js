import { useMutation, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { travelQueryKeys } from "app/views/travel/shared/hooks/travelQueryKeys";

export function useEditTravelApplication(appId) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (draft) => {
      const acknowledgement = await fetchApiJson(
        `/travel/application/edit/${appId}`,
        {
          method: "POST",
          payload: draft,
        },
      );
      if (acknowledgement?.success !== true) {
        throw new Error("The server did not acknowledge the save.");
      }
      return acknowledgement;
    },
    retry: false,
    onSuccess: async () => {
      // A successful save stays successful even if a background refresh fails.
      await Promise.allSettled([
        queryClient.invalidateQueries({
          queryKey: travelQueryKeys.applications(),
        }),
        queryClient.invalidateQueries({ queryKey: travelQueryKeys.reviews() }),
      ]);
    },
  });
}
