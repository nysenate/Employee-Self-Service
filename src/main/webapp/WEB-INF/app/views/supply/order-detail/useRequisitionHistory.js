import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { requisitionKeys } from "app/views/supply/requisition.queryKeys";

// Returns the full history of a requisitions which includes all previous versions.
export function useRequisitionHistory(id) {
  return useQuery({
    queryKey: requisitionKeys.history(id),
    queryFn: () => {
      return fetchApiJson(`/supply/requisitions/history/${id}`);
    },
    staleTime: 0,
    throwOnError: true,
  });
}
