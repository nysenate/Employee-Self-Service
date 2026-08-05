import { useTravelHistorySearchParams } from "app/views/travel/shared/hooks/useTravelHistorySearchParams";

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

const FILTERS = {
  status: {
    defaultValue: "",
    validValues: validStatuses,
  },
  sort: {
    defaultValue: "startDate:desc",
    validValues: validSorts,
    persistDefault: true,
  },
};

export function useApplicationHistorySearchParams() {
  return useTravelHistorySearchParams({
    defaultLimit: 16,
    filters: FILTERS,
  });
}
