import { useMutation, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { travelQueryKeys } from "app/views/travel/shared/hooks/travelQueryKeys";
import { useNotifySuccess } from "app/components/NotificationProvider";

export function useCancelTravelApplication(appId) {
  const notifySuccess = useNotifySuccess();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      const response = await fetchApiJson(
        `/travel/application/edit/${appId}/cancel`,
        { method: "POST" },
      );
      if (response?.success !== true) {
        throw new Error("Cancellation was not confirmed.");
      }
      return response;
    },
    retry: false,
    onSuccess: async () => {
      // The legacy endpoint returns the application as loaded before cancellation.
      // Fetch fresh data instead of writing that stale response into the cache.
      await Promise.allSettled([
        queryClient.invalidateQueries({
          queryKey: travelQueryKeys.applications(),
        }),
        queryClient.invalidateQueries({ queryKey: travelQueryKeys.reviews() }),
      ]);
      notifySuccess(`Application #${appId} canceled`);
    },
  });
}
