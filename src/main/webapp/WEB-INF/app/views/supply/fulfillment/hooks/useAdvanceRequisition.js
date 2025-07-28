import { useMutation, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { requisitionKeys } from "app/views/supply/shared/hooks/requisition.queryKeys";

/**
 * Advances/Processes a requisition to it's next status.
 */
export function useAdvanceRequisition() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (requisition) => {
      return fetchApiJson(
        `/supply/requisitions/${requisition.requisitionId}/process`,
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
