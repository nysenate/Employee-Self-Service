import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/utils/fetchJson";

export function useTasks(activeOnly = false) {
  return useQuery({
    queryKey: ['tasks', activeOnly],
    queryFn: () => {
      return fetchApiJson(`/personnel/task?activeOnly=${activeOnly}`)
        .then(body => body.tasks)
    },
    staleTime: 60000,
  })
}