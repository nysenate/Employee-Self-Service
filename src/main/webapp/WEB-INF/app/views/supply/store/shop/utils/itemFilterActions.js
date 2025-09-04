// Action Types
export const SET_TERM = "SET_TERM";
export const SET_OFFSET = "SET_OFFSET";
export const SET_SORT = "SET_SORT";
export const RESET_FILTERS = "RESET_FILTERS";
export const TOGGLE_CATEGORY = "TOGGLE_CATEGORY";
export const CLEAR_CATEGORIES = "CLEAR_CATEGORIES";

// Actions
export const setTerm = (term) => ({
  type: SET_TERM,
  payload: { term },
});

export const setOffset = (offset) => ({
  type: SET_OFFSET,
  payload: { offset },
});

export const setSort = (sort) => ({
  type: SET_SORT,
  payload: { sort },
});

export const resetFilters = () => ({
  type: RESET_FILTERS,
});

export const toggleCategory = (checked, category) => ({
  type: TOGGLE_CATEGORY,
  payload: { checked, category },
});

export const clearCategories = () => ({
  type: CLEAR_CATEGORIES,
});
