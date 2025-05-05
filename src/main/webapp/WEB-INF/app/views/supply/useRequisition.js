import { fetchApiJson } from "app/api/fetchJson";
import { useQuery } from "@tanstack/react-query";

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
