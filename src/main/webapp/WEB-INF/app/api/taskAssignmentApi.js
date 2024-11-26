import { fetchApiJson } from "app/utils/fetchJson";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

export function useTaskAssignments(empId) {
  return useQuery({
    queryKey: ['tasks', 'assignments', 'list', empId],
    queryFn: () => {
      return fetchApiJson(`/personnel/task/assignment/${empId}?detail=true&activeOnly=true`)
        .then((body) => body.assignments)
    },
    throwOnError: true,
  })
}

export function useTaskAssignment(empId, taskId) {
  return useQuery({
    queryKey: ['tasks', 'assignments', 'detail', empId, taskId],
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
      return queryClient.invalidateQueries({ queryKey: ['tasks', 'assignments', 'detail', empId, taskId] })
    },
    throwOnError: true,
  })
}

export function useSubmitVideoCodes() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (data) => {
      return fetchApiJson(`/personnel/task/video/code`, { method: "POST", payload: data })
    },
    onSuccess: (data, { empId, taskId }) => {
      return queryClient.invalidateQueries({ queryKey: ['tasks', 'assignments', 'detail', empId, taskId] })
    },
  })
}

export function useSubmitEthicsLiveForm() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (data) => {
      return fetchApiJson(`/personnel/task/ethics/live/code`, { method: "POST", payload: data })
    },
    onSuccess: (data, { empId, taskId }) => {
      return queryClient.invalidateQueries({ queryKey: ['tasks', 'assignments', 'detail', empId, taskId] })
    },
  })
}

export function useManuallyOverrideCompletionStatus() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (data) => {
      return fetchApiJson(`/admin/personnel/task/overrride/${data.updatedByEmpId}/${data.taskId}/${data.isCompleted}/${data.assignedEmpId}`)
    },
    onSuccess: (data) => {
      return queryClient.invalidateQueries({ queryKey: ['tasks', 'assignments', 'search'] })
    }
  })
}

export function useManuallyDeactivateTaskAssignment() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (data) => {
      return fetchApiJson(`/admin/personnel/task/overrride/activation/${data.updatedByEmpId}/${data.taskId}/${data.isActive}/${data.assignedEmpId}`)
    },
    onSuccess: (data) => {
      return queryClient.invalidateQueries({ queryKey: ['tasks', 'assignments', 'search'] })
    }
  })
}
