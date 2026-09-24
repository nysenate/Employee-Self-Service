const REVIEW_QUEUE = "/travel/manage/queue";

export function editApplicationUrl(appId) {
  return `/travel/applications/${appId}/edit`;
}

export function normalizeReviewReturnTo(value) {
  if (
    typeof value !== "string" ||
    !/^\/travel\/manage\/(?:queue|review-history)(?:\?[^#]*)?$/.test(value)
  ) {
    return REVIEW_QUEUE;
  }
  try {
    decodeURIComponent(value);
    return value;
  } catch {
    return REVIEW_QUEUE;
  }
}
