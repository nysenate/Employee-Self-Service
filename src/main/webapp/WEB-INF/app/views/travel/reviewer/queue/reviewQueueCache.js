export const REVIEW_QUEUE_QUERY_KEY = ["travel", "review", "queue"];

export function removeReviewFromQueue(queue, appReviewId) {
  if (!queue) return queue;

  return Object.fromEntries(
    Object.entries(queue).map(([role, reviews]) => [
      role,
      Array.isArray(reviews)
        ? reviews.filter((review) => review.appReviewId !== appReviewId)
        : reviews,
    ]),
  );
}
