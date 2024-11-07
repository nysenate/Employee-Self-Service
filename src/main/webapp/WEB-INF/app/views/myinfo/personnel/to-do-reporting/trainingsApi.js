import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/utils/fetchJson";

export function useTrainings(activeOnly) {
  return useQuery({
    queryKey: [ 'trainings', activeOnly ],
    queryFn: () => {
      return fetchApiJson(`/personnel/task`)
        .then(body => body.tasks)
    },
  })
}