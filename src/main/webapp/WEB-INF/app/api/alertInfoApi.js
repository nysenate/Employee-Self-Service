import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/utils/fetchJson";
import useAuth from "app/contexts/Auth/useAuth";

export function useAlertInfo(empId) {
  return useQuery({
    queryKey: [ 'alert-info', empId ],
    queryFn: () => {
      return fetchApiJson(`/alert-info?empId=${empId}`)
        .then((body) => body.result)
    },
    throwOnError: true,
  })
}

export function useMutateAlertInfo() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (data) => {
      return fetchApiJson(`/alert-info`, { method: "POST", payload: data })
    },
    onSuccess: (data, { empId }) => {
      // Invalidate and refetch
      return queryClient.invalidateQueries({ queryKey: [ 'alert-info', empId ] })
    },
  })
}
