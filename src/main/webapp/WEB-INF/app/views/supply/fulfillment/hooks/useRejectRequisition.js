import { useMutation, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { requisitionKeys } from "app/views/supply/shared/hooks/requisition.queryKeys";

/**
 * Rejects a requisition.
 */
export function useRejectRequisition() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (requisition) => {
      return fetchApiJson(
        `/supply/requisitions/${requisition.requisitionId}/reject`,
        {
          method: "POST",
          payload: requisition,
        },
      );
    },
    onSuccess: (res) => {
      return queryClient.invalidateQueries({
        queryKey: requisitionKeys.all(),
      });
    },
    throwOnError: true,
  });
}
