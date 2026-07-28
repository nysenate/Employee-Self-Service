import { useMutation, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

const REVIEW_QUEUE_QUERY_KEY = ["travel", "review", "queue"];

export function useApproveReview() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ appReviewId, role, notes }) =>
      fetchApiJson(
        `/travel/review/${appReviewId}/approve?role=${encodeURIComponent(role)}`,
        {
          method: "POST",
          payload: { notes },
        },
      ),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: REVIEW_QUEUE_QUERY_KEY });
    },
    throwOnError: true,
  });
}
