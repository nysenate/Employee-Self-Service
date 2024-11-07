import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/utils/fetchJson";

export function useAssignments(activeOnly) {
  return useQuery({
    queryKey: [ 'assignments', activeOnly ],
    queryFn: () => {
      return fetchApiJson(`/personnel/task`)
        .then(body => body.tasks)
    },
  })
}