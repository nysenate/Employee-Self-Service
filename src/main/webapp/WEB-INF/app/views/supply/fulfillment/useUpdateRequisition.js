import { useMutation, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { requisitionKeys } from "app/views/supply/requisition.queryKeys";

export function useUpdateRequisition() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (requisition) => {
      return fetchApiJson(`/supply/requisitions/${requisition.requisitionId}`, {
        method: "POST",
        payload: requisition,
      });
    },
    onSuccess: (res) => {
      return queryClient.invalidateQueries({
        queryKey: requisitionKeys.all(),
      });
    },
    throwOnError: true,
  });
}
