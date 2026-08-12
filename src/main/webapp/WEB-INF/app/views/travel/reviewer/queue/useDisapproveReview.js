import { useReviewMutation } from "./useReviewMutation";

export function useDisapproveReview() {
  return useReviewMutation("disapprove");
}
