import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  getEmployeeAlertInfo,
  updateEmployeeAlertInfo,
} from "app/api/employeeAlertInfo";

function getQueryKey(empId) {
  return ["employee", "detail", empId, "alert-info"];
}

export function useEmployeeAlertInfo(empId) {
  return useQuery({
    queryKey: getQueryKey(empId),
    queryFn: () => {
      return getEmployeeAlertInfo(empId).then((body) => body.result);
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
      return updateEmployeeAlertInfo(data);
    },
    onSuccess: (data, { empId }) => {
      // Invalidate and refetch
      return queryClient.invalidateQueries({ queryKey: getQueryKey(empId) });
    },
  });
}
