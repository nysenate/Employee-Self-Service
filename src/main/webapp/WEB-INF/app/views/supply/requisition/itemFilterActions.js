// Action Types
export const SET_TERM = "SET_TERM";
export const SET_PAGE = "SET_PAGE";
export const SET_SORT = "SET_SORT";
export const RESET_FILTERS = "RESET_FILTERS";

// Actions
export const setTerm = (term) => ({
  type: SET_TERM,
  payload: { term },
});

export const setPage = (page) => ({
  type: SET_PAGE,
  payload: { page },
});

export const setSort = (sort) => ({
  type: SET_SORT,
  payload: { sort },
});

export const resetFilters = () => ({
  type: RESET_FILTERS,
});
