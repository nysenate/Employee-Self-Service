import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

export function useReviewQueue() {
  return useQuery({
    queryKey: ["travel", "review", "queue"],
    queryFn: () =>
      fetchApiJson("/travel/review/pending").then((body) => body.result.items),
    staleTime: 0,
    throwOnError: true,
  });
}
