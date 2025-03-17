import { useMutation, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

export function useCheckout() {
  return useMutation({
    mutationFn: (data) =>
      fetchApiJson(`/supply/requisitions`, {
        method: "POST",
        payload: data,
      }),
    throwOnError: true,
  });
}
