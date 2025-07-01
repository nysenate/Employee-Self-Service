import { useMutation, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { requisitionKeys } from "app/views/supply/requisition.queryKeys";

/**
 * Removes the last change to this requisition
 */
export function useUndoRequisition() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (requisition) => {
      return fetchApiJson(
        `/supply/requisitions/${requisition.requisitionId}/undo`,
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
