import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { REVIEW_QUEUE_QUERY_KEY } from "./reviewQueueCache";

export function useReviewQueue() {
  return useQuery({
    queryKey: REVIEW_QUEUE_QUERY_KEY,
    queryFn: () =>
      fetchApiJson("/travel/review/pending").then((body) => body.result.items),
    staleTime: 0,
    throwOnError: true,
  });
}
