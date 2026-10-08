const HISTORY_PATH = "/travel/applications";
const HISTORY_SEARCH_KEYS = new Set([
  "range",
  "fromDate",
  "toDate",
  "status",
  "sort",
  "limit",
  "offset",
]);

export function parseApplicationId(value) {
  const text = String(value);
  if (!/^[1-9]\d*$/.test(text)) return null;

  const parsed = Number(text);
  return Number.isSafeInteger(parsed) ? parsed : null;
}

export function historyDetailsUrl(appId, historySearch = "") {
  const sourceParams = new URLSearchParams(historySearch);
  const historyParams = new URLSearchParams();

  sourceParams.forEach((value, key) => {
    if (HISTORY_SEARCH_KEYS.has(key)) {
      historyParams.append(key, value);
    }
  });
  historyParams.set("appId", String(appId));

  return `${HISTORY_PATH}?${historyParams.toString()}`;
}

export function resubmitUrl(appId) {
  return `${HISTORY_PATH}/${appId}/resubmit`;
}

export function normalizeHistoryReturnTo(value) {
  if (typeof value !== "string") return HISTORY_PATH;
  if (!/^\/travel\/applications(?:\?[^#]*)?$/.test(value)) {
    return HISTORY_PATH;
  }

  try {
    decodeURIComponent(value);
    return value;
  } catch {
    return HISTORY_PATH;
  }
}
