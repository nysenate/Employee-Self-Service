import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

export function useTravelReview(id) {
  return useQuery({
    queryKey: ["travel", "review", id],
    queryFn: () => fetchApiJson(`/travel/review/${id}`),
    staleTime: 0,
    throwOnError: true,
    enabled: Boolean(id),
  });
}
