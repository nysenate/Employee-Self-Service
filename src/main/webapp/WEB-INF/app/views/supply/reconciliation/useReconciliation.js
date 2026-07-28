import { useMutation, useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

function getQueryKey() {
  return ["supply", "reconciliation"];
}

export function useReconciliation() {
  return useQuery({
    queryKey: getQueryKey(),
    queryFn: () => {
      return fetchApiJson(`/supply/reconciliation`).then((body) => body.result);
    },
    throwOnError: true,
    staleTime: 0,
    refetchOnMount: true,
    // Prevent refetches while users are entering data.
    refetchOnWindowFocus: false,
    refetchOnReconnect: false,
  });
}

export function useSubmitReconciliation() {
  return useMutation({
    mutationFn: (data) => {
      return fetchApiJson(`/supply/reconciliation`, {
        method: "POST",
        payload: data,
      });
    },
    throwOnError: true,
  });
}
