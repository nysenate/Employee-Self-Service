import { fetchApiJson } from "app/utils/fetchJson";
import { useQuery } from "@tanstack/react-query";

export function useTaskAssignments(empId) {
  return useQuery({
    queryKey: [ 'taskAssignments', empId ],
    queryFn: () => {
      return fetchApiJson(`/personnel/task/assignment/${empId}?detail=true&activeOnly=true`)
        .then((body) => body.assignments)
    }
  })
}