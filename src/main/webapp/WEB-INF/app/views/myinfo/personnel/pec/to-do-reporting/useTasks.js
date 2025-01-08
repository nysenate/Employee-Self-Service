import { useQuery } from "@tanstack/react-query";
import { getTasks } from "app/api/tasks";

export function useTasks(activeOnly = false) {
  return useQuery({
    queryKey: ['tasks', activeOnly],
    queryFn: () => {
      return getTasks(activeOnly)
        .then(body => body.tasks)
    },
    staleTime: 60000,
    throwOnError: true,
  })
}