import { useQuery } from "@tanstack/react-query";
import { getTrainings } from "app/api/trainings";

export function useTrainings(activeOnly) {
  return useQuery({
    queryKey: ['trainings', activeOnly],
    queryFn: () => {
      return getTrainings(activeOnly)
        .then(body => body.tasks)
    },
    staleTime: 60000,
    throwOnError: true,
  })
}