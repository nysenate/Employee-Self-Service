import { useMutation, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import {
  removeReviewFromQueue,
  REVIEW_QUEUE_QUERY_KEY,
} from "./reviewQueueCache";

export function useReviewMutation(action) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ appReviewId, role, notes }) =>
      fetchApiJson(
        `/travel/review/${appReviewId}/${action}?role=${encodeURIComponent(role)}`,
        {
          method: "POST",
          payload: { notes },
        },
      ),
    onMutate: async ({ appReviewId }) => {
      await queryClient.cancelQueries({ queryKey: REVIEW_QUEUE_QUERY_KEY });
      const previousQueue = queryClient.getQueryData(REVIEW_QUEUE_QUERY_KEY);
      queryClient.setQueryData(REVIEW_QUEUE_QUERY_KEY, (queue) =>
        removeReviewFromQueue(queue, appReviewId),
      );
      return { previousQueue };
    },
    onError: (_error, _variables, context) => {
      queryClient.setQueryData(REVIEW_QUEUE_QUERY_KEY, context?.previousQueue);
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: REVIEW_QUEUE_QUERY_KEY });
    },
  });
}
