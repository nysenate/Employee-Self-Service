import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

export function useTrainings(activeOnly) {
  return useQuery({
    queryKey: ["trainings", activeOnly],
    queryFn: () => {
      return fetchApiJson(`/personnel/task?activeOnly=${activeOnly}`).then(
        (body) => body.tasks,
      );
    },
    staleTime: 1000 * 60 * 1,
    throwOnError: true,
  });
}
