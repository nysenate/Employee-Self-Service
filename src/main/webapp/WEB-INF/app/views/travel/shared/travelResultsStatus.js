export const TRAVEL_RESULTS_STATUS = {
  ready: "ready",
  refreshing: "refreshing",
  transitioning: "transitioning",
};

export function resolveTravelResultsStatus({
  isFetching,
  isPlaceholderData = false,
}) {
  if (isPlaceholderData) {
    return TRAVEL_RESULTS_STATUS.transitioning;
  }
  if (isFetching) {
    return TRAVEL_RESULTS_STATUS.refreshing;
  }
  return TRAVEL_RESULTS_STATUS.ready;
}
