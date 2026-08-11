import { useReviewMutation } from "./useReviewMutation";

export function useApproveReview() {
  return useReviewMutation("approve");
}
