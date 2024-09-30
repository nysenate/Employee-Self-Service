import { fetchApiJson } from "app/utils/fetchJson";
import { useQuery } from "@tanstack/react-query";

export function useTaskAssignments(empId) {
  return useQuery({
    queryKey: [ 'task', 'assignments', 'list', empId ],
    queryFn: () => {
      return fetchApiJson(`/personnel/task/assignment/${empId}?detail=true&activeOnly=true`)
        .then((body) => body.assignments)
    },
    throwOnError: true,
  })
}

export function useTaskAssignment(empId, taskId) {
  return useQuery({
    queryKey: [ 'task', 'assignments', 'detail', empId, taskId ],
    queryFn: () => {
      return fetchApiJson(`/personnel/task/assignment/${empId}/${taskId}`)
        .then((body) => body.task)
    },
    throwOnError: true,
  })
}