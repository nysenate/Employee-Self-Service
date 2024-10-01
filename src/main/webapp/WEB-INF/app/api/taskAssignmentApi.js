import { fetchApiJson } from "app/utils/fetchJson";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

export function useTaskAssignments(empId) {
  return useQuery({
    queryKey: [ 'tasks', 'assignments', 'list', empId ],
    queryFn: () => {
      return fetchApiJson(`/personnel/task/assignment/${empId}?detail=true&activeOnly=true`)
        .then((body) => body.assignments)
    },
    throwOnError: true,
  })
}

export function useTaskAssignment(empId, taskId) {
  return useQuery({
    queryKey: [ 'tasks', 'assignments', 'detail', empId, taskId ],
    queryFn: () => {
      return fetchApiJson(`/personnel/task/assignment/${empId}/${taskId}`)
        .then((body) => body.task)
    },
    throwOnError: true,
  })
}

export function useAcknowledgeDocument() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (data) => {
      return fetchApiJson(`/personnel/task/acknowledgment?empId=${data.empId}&taskId=${data.taskId}`, { method: "POST" })
    },
    onSuccess: (data, { empId, taskId }) => {
      return queryClient.invalidateQueries({ queryKey: [ 'tasks', 'assignments', 'detail', empId, taskId ] })
    },
    throwOnError: true,
  })
}