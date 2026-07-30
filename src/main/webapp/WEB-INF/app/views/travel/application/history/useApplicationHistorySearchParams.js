import { useCallback, useEffect, useMemo } from "react";
import { formatISO, isValid, parseISO, subMonths } from "date-fns";
import { useSearchParams } from "react-router-dom";

const MAX_LIMIT = 100;

export const APPLICATION_HISTORY_STATUS_OPTIONS = [
  { value: "", label: "All statuses" },
  { value: "DEPARTMENT_HEAD", label: "Department Head" },
  { value: "TRAVEL_UNIT", label: "Travel Unit" },
  { value: "APPROVED", label: "Approved" },
  { value: "DISAPPROVED", label: "Disapproved" },
  { value: "CANCELED", label: "Canceled" },
  { value: "NOT_APPLICABLE", label: "Not Applicable" },
];

export const APPLICATION_HISTORY_SORT_OPTIONS = [
  { value: "startDate:desc", label: "Start date: newest" },
  { value: "startDate:asc", label: "Start date: oldest" },
  { value: "submittedDate:desc", label: "Submitted: newest" },
  { value: "submittedDate:asc", label: "Submitted: oldest" },
  { value: "status:asc", label: "Status" },
];

const validStatuses = new Set(
  APPLICATION_HISTORY_STATUS_OPTIONS.map(({ value }) => value),
);
const validSorts = new Set(
  APPLICATION_HISTORY_SORT_OPTIONS.map(({ value }) => value),
);

function getDefaults() {
  return {
    fromDate: formatISO(subMonths(new Date(), 1), { representation: "date" }),
    toDate: formatISO(new Date(), { representation: "date" }),
    limit: 16,
    offset: 1,
    status: "",
    sort: "startDate:desc",
  };
}

function parseDate(value, fallback) {
  if (!value || !/^\d{4}-\d{2}-\d{2}$/.test(value)) {
    return fallback;
  }

  return isValid(parseISO(value)) ? value : fallback;
}

function parsePositiveInteger(value, fallback, max = Number.MAX_SAFE_INTEGER) {
  if (value === null || value.trim() === "") {
    return fallback;
  }

  const parsed = Number(value);
  return Number.isInteger(parsed) && parsed >= 1 && parsed <= max
    ? parsed
    : fallback;
}

export function fromSearchParams(searchParams, defaults = getDefaults()) {
  let fromDate = parseDate(searchParams.get("fromDate"), defaults.fromDate);
  let toDate = parseDate(searchParams.get("toDate"), defaults.toDate);

  // Treat an inverted manually entered range as a reversed range.
  if (fromDate > toDate) {
    [fromDate, toDate] = [toDate, fromDate];
  }

  const status = searchParams.get("status") ?? defaults.status;
  const sort = searchParams.get("sort") ?? defaults.sort;

  return {
    fromDate,
    toDate,
    limit: parsePositiveInteger(
      searchParams.get("limit"),
      defaults.limit,
      MAX_LIMIT,
    ),
    offset: parsePositiveInteger(searchParams.get("offset"), defaults.offset),
    status: validStatuses.has(status) ? status : defaults.status,
    sort: validSorts.has(sort) ? sort : defaults.sort,
  };
}

function canonicalizeSearchParams(searchParams, state) {
  const params = new URLSearchParams(searchParams);

  params.set("fromDate", state.fromDate);
  params.set("toDate", state.toDate);
  params.set("limit", state.limit.toString());
  params.set("offset", state.offset.toString());
  params.set("sort", state.sort);

  if (state.status) {
    params.set("status", state.status);
  } else {
    params.delete("status");
  }

  return params;
}

export function useApplicationHistorySearchParams() {
  const defaults = useMemo(getDefaults, []);
  const [searchParams, setSearchParams] = useSearchParams();
  const state = useMemo(
    () => fromSearchParams(searchParams, defaults),
    [defaults, searchParams],
  );

  useEffect(() => {
    const canonicalParams = canonicalizeSearchParams(searchParams, state);
    if (canonicalParams.toString() !== searchParams.toString()) {
      setSearchParams(canonicalParams, { replace: true });
    }
  }, [searchParams, setSearchParams, state]);

  const updateSearchParams = useCallback(
    (updates, { replace = true } = {}) => {
      setSearchParams(
        (currentParams) => {
          const nextParams = new URLSearchParams(currentParams);

          Object.entries(updates).forEach(([key, value]) => {
            if (value === undefined || value === null || value === "") {
              nextParams.delete(key);
            } else {
              nextParams.set(key, value.toString());
            }
          });

          return nextParams;
        },
        { replace },
      );
    },
    [setSearchParams],
  );

  return { state, updateSearchParams };
}
