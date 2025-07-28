export const requisitionKeys = {
  all: () => ["supply", "requisitions"],
  detail: (id) => [...requisitionKeys.all(), "detail", id],
  history: (id) => [...requisitionKeys.detail(id), "history"],
  search: (query) => [...requisitionKeys.all(), "search", query],
  locationStatistics: (year, month) => [
    ...requisitionKeys.all(),
    "location",
    "statistics",
    year,
    month,
  ],
};
