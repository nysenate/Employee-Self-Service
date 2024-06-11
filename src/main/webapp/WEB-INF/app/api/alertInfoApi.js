import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/utils/fetchJson";
import useAuth from "app/contexts/Auth/useAuth";

export function useAlertInfo() {
  const auth = useAuth()
  return useQuery({
    queryKey: [ 'alert-info', auth.empId() ],
    queryFn: () => {
      return fetchApiJson(`/alert-info?empId=${auth.empId()}`)
        .then((body) => body.result)
    }
  })
}

export function useMutateAlertInfo() {
  const auth = useAuth()
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (data) => {
      return fetchApiJson(`/alert-info`, { method: "POST", payload: data })
    },
    onSuccess: () => {
      // Invalidate and refetch
      return queryClient.invalidateQueries({ queryKey: [ 'alert-info', auth.empId() ] })
    }
  })
}
