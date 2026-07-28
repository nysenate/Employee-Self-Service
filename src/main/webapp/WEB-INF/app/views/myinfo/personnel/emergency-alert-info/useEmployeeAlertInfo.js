import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

function getQueryKey(empId) {
  return ["employee", "detail", empId, "alert-info"];
}

export function useEmployeeAlertInfo(empId) {
  return useQuery({
    queryKey: getQueryKey(empId),
    queryFn: () => {
      return fetchApiJson(`/alert-info?empId=${empId}`).then(
        (body) => body.result,
      );
    },
    enabled: !!empId,
    staleTime: 1000 * 60 * 1,
    throwOnError: true,
  });
}

export function useMutateEmployeeAlertInfo() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data) => {
      return fetchApiJson(`/alert-info`, { method: "POST", payload: data });
    },
    onSuccess: (data, { empId }) => {
      // Invalidate and refetch
      return queryClient.invalidateQueries({ queryKey: getQueryKey(empId) });
    },
  });
}
