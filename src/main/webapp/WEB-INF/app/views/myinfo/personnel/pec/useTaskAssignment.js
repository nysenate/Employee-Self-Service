import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  acknowledgeDocument,
  getTaskAssignment,
  getTaskAssignments, manuallyDeactivateTaskAssignment, manuallyOverrideCompletionStatus, searchTaskAssignments,
  submitEthicsLiveForm,
  submitVideoCodes
} from "app/api/taskAssignment";
import { fetchApiJson } from "app/api/fetchJson";
import { manuallyAssignTask, searchPotentialTaskAssignments } from "../../../../api/taskAssignment";


const taskAssignmentKeys = {
  all: ['tasks', 'assignments'],
  list: (empId) => [...taskAssignmentKeys.all, empId, 'list'],
  search: (params) => [...taskAssignmentKeys.all, 'search', params],
  detail: (empId, taskId) => [...taskAssignmentKeys.all, empId, taskId],
  potential: (params) => [...taskAssignmentKeys.all, 'potential', params],
}

export function useTaskAssignments(empId) {
  return useQuery({
    queryKey: taskAssignmentKeys.list(empId),
    queryFn: () => {
      return getTaskAssignments(empId)
        .then((body) => body.assignments)
    },
    throwOnError: true,
  })
}

export function useTaskAssignment(empId, taskId) {
  return useQuery({
    queryKey: taskAssignmentKeys.detail(empId, taskId),
    queryFn: () => {
      return getTaskAssignment(empId, taskId)
        .then((body) => body.task)
    },
    throwOnError: true,
  })
}

export function useAcknowledgeDocument() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: acknowledgeDocument,
    onSuccess: (data, { empId, taskId }) => {
      return queryClient.invalidateQueries({ queryKey: taskAssignmentKeys.all })
    },
    throwOnError: true,
  })
}

export function useSubmitVideoCodes() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: submitVideoCodes,
    onSuccess: (data, { empId, taskId }) => {
      return queryClient.invalidateQueries({ queryKey: taskAssignmentKeys.all })
    },
  })
}

export function useSubmitEthicsLiveForm() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: submitEthicsLiveForm,
    onSuccess: (data, { empId, taskId }) => {
      return queryClient.invalidateQueries({ queryKey: taskAssignmentKeys.all })
    },
  })
}

export function useManuallyOverrideCompletionStatus() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: manuallyOverrideCompletionStatus,
    onSuccess: (data) => {
      return queryClient.invalidateQueries({ queryKey: taskAssignmentKeys.all })
    }
  })
}

export function useManuallyDeactivateTaskAssignment() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: manuallyDeactivateTaskAssignment,
    onSuccess: (data) => {
      return queryClient.invalidateQueries({ queryKey: taskAssignmentKeys.all })
    }
  })
}

export function useManuallyAssignTask() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: manuallyAssignTask,
    onSuccess: (data) => {
      return queryClient.invalidateQueries({ queryKey: taskAssignmentKeys.all })
    }
  })
}

export function useSearchTaskAssignments(state) {
  const queryParams = searchTaskAssignmentsQueryParams(state)
  return useQuery({
    queryKey: taskAssignmentKeys.search(queryParams),
    queryFn: () => {
      return searchTaskAssignments(queryParams)
    },
    cacheTime: 0 // Disable caching for this query.
  })
}

export function useSearchPotentialAssignments(state) {
  const queryParams = searchTaskAssignmentsQueryParams(state)
  return useQuery({
    queryKey: taskAssignmentKeys.potential(queryParams),
    queryFn: () => {
      return searchPotentialTaskAssignments(queryParams)
    },
    cacheTime: 0 // Disable caching for this query.
  })
}

// Converts the state object into string of query parameters for the search task assignments endpoints.
export function searchTaskAssignmentsQueryParams(state) {
  const searchParams = {
    ...state,
    respCtrHead: state.respCtrHead.map(r => r.code)
  }
  return buildQueryString(searchParams)
}

// Serializes params into a string of query parameters.
// Params with empty values are not included.
function buildQueryString(params) {
  const keyValuePairs = [];
  for (const key in params) {
    if (params[key] !== '' && params[key] != null) {
      if (Array.isArray(params[key]) && params[key].length > 0) {
        params[key].forEach(value => {
          keyValuePairs.push(key + '=' + value);
        });
      } else if (!Array.isArray(params[key])) {
        keyValuePairs.push(key + '=' + params[key]);
      }
    }
  }
  let queryString = keyValuePairs.join('&');
  return queryString;
}