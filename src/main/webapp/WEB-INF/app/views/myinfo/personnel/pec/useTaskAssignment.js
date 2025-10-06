import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { buildQueryString } from "app/utils/apiUtils";
import { fetchApiJson } from "app/api/fetchJson";

const taskAssignmentKeys = {
  all: ["tasks", "assignments"],
  list: (empId) => [...taskAssignmentKeys.all, empId, "list"],
  search: (params) => [...taskAssignmentKeys.all, "search", params],
  detail: (empId, taskId) => [...taskAssignmentKeys.all, empId, taskId],
  potential: (params) => [...taskAssignmentKeys.all, "potential", params],
};

export function useTaskAssignments(empId, detail = true, activeOnly = true) {
  return useQuery({
    queryKey: taskAssignmentKeys.list(empId),
    queryFn: () => {
      return fetchApiJson(
        `/personnel/task/assignment/${empId}?detail=${detail}&activeOnly=${activeOnly}`,
      ).then((body) => body.assignments);
    },
    enabled: !!empId,
    throwOnError: true,
  });
}

export function useTaskAssignment(empId, taskId) {
  return useQuery({
    queryKey: taskAssignmentKeys.detail(empId, taskId),
    queryFn: () => {
      return fetchApiJson(`/personnel/task/assignment/${empId}/${taskId}`).then(
        (body) => body.task,
      );
    },
    enabled: !!empId,
    throwOnError: true,
  });
}

export function useAcknowledgeDocument() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ empId, taskId }) =>
      fetchApiJson(
        `/personnel/task/acknowledgment?empId=${empId}&taskId=${taskId}`,
        { method: "POST" },
      ),
    onSuccess: (data, { empId, taskId }) => {
      return queryClient.invalidateQueries({
        queryKey: taskAssignmentKeys.all,
      });
    },
    throwOnError: true,
  });
}

export function useSubmitVideoCodes() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data) =>
      fetchApiJson(`/personnel/task/video/code`, {
        method: "POST",
        payload: data,
      }),
    onSuccess: (data, { empId, taskId }) => {
      return queryClient.invalidateQueries({
        queryKey: taskAssignmentKeys.all,
      });
    },
  });
}

export function useSubmitEthicsLiveForm() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data) =>
      fetchApiJson(`/personnel/task/ethics/live/code`, {
        method: "POST",
        payload: data,
      }),
    onSuccess: (data, { empId, taskId }) => {
      return queryClient.invalidateQueries({
        queryKey: taskAssignmentKeys.all,
      });
    },
  });
}

export function useManuallyOverrideCompletionStatus() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data) =>
      fetchApiJson(
        `/admin/personnel/task/overrride/${data.updatedByEmpId}/${data.taskId}/${data.isCompleted}/${data.assignedEmpId}`,
      ),
    onSuccess: (data) => {
      return queryClient.invalidateQueries({
        queryKey: taskAssignmentKeys.all,
      });
    },
  });
}

export function useManuallyDeactivateTaskAssignment() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data) =>
      fetchApiJson(
        `/admin/personnel/task/overrride/activation/${data.updatedByEmpId}/${data.taskId}/${data.isActive}/${data.assignedEmpId}`,
      ),
    onSuccess: (data) => {
      return queryClient.invalidateQueries({
        queryKey: taskAssignmentKeys.all,
      });
    },
  });
}

export function useManuallyAssignTask() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data) =>
      fetchApiJson(
        `/admin/personnel/task/overrride/${data.updatedByEmpId}/${data.taskId}/${data.assignedEmpId}`,
      ),
    onSuccess: (data) => {
      return queryClient.invalidateQueries({
        queryKey: taskAssignmentKeys.all,
      });
    },
  });
}

export function useSearchTaskAssignments(state) {
  const queryParams = searchTaskAssignmentsQueryParams(state);
  return useQuery({
    queryKey: taskAssignmentKeys.search(queryParams),
    queryFn: () => {
      return fetchApiJson(`/personnel/task/emp/search?${queryParams}`);
    },
    cacheTime: 0, // Disable caching for this query.
  });
}

export function useSearchPotentialAssignments(state) {
  const queryParams = searchTaskAssignmentsQueryParams(state);
  return useQuery({
    queryKey: taskAssignmentKeys.potential(queryParams),
    queryFn: () => {
      return fetchApiJson(`/personnel/task/emp/assignSearch?${queryParams}`);
    },
    cacheTime: 0, // Disable caching for this query.
  });
}

// Converts the state object into string of query parameters for the search task assignments endpoints.
export function searchTaskAssignmentsQueryParams(state) {
  const searchParams = {
    ...state,
    respCtrHead: state.respCtrHead.map((r) => r.code),
  };
  return buildQueryString(searchParams);
}
