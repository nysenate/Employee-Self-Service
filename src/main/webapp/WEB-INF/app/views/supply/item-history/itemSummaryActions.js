// Action Types
export const SET_FILTER = "SET_FILTER";
export const SET_DATE_RANGE = "SET_DATE_RANGE";
export const SET_OFFSET = "SET_OFFSET";
export const RESET_FILTERS = "RESET_FILTERS";

// Actions
export const setFilter = (filterName, filterValue) => ({
  type: SET_FILTER,
  filter: filterName,
  value: filterValue,
});

export const setDateRange = (fromDate, toDate) => ({
  type: SET_DATE_RANGE,
  fromDate: fromDate,
  toDate: toDate,
});

export const setOffset = (offset) => ({
  type: SET_OFFSET,
  offset: offset,
});

export const resetFilters = () => ({
  type: RESET_FILTERS,
});
