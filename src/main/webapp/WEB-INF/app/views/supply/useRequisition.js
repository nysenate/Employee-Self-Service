import { fetchApiJson } from "app/api/fetchJson";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

function getQueryKey(id) {
  return ["supply", "requisition", "detail", id, "history"];
}

export function useRequisitionHistory(id) {
  return useQuery({
    queryKey: getQueryKey(id),
    queryFn: () => {
      return fetchApiJson(`/supply/requisitions/history/${id}`);
    },
    throwOnError: true,
  });
}

export function useMutateRequisition() {
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
        queryKey: ["supply", "requisition"],
      });
    },
    throwOnError: true,
  });
}
