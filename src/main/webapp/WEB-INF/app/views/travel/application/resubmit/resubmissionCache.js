import { travelQueryKeys } from "app/views/travel/shared/hooks/travelQueryKeys";

export async function refreshTravelAfterResubmission(queryClient) {
  await Promise.allSettled([
    queryClient.invalidateQueries({
      queryKey: travelQueryKeys.applications(),
      refetchType: "active",
    }),
    queryClient.invalidateQueries({
      queryKey: travelQueryKeys.reviews(),
      refetchType: "active",
    }),
  ]);
}
