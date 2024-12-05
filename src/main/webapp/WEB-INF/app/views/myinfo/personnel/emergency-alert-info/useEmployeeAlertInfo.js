import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import useAuth from "app/contexts/Auth/useAuth";
import { getEmployeeAlertInfo, updateEmployeeAlertInfo } from "app/api/employeeAlertInfo";


function getQueryKey(empId) {
  return ['employee', 'detail', empId, 'alert-info']
}

export function useEmployeeAlertInfo(empId) {
  return useQuery({
    queryKey: getQueryKey(empId),
    queryFn: () => {
      return getEmployeeAlertInfo(empId)
        .then((body) => body.result)
    },
    staleTime: 60000,
    throwOnError: true,
  })
}

export function useMutateEmployeeAlertInfo() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (data) => {
      return updateEmployeeAlertInfo(data)
    },
    onSuccess: (data, { empId }) => {
      // Invalidate and refetch
      return queryClient.invalidateQueries({ queryKey: getQueryKey(empId) })
    },
  })
}
