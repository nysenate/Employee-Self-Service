import { fetchApiJson } from "app/utils/fetchJson";



/**
 * Fetch employee info based on provided parameters.
 *
 * @param {Object} params The parameters for the fetch call.
 * @returns {Promise<Object>} The result of the fetch call.
 */
export const fetchEmployeeInfo = async (params) => {
  const queryParams = new URLSearchParams();

  Object.keys(params).forEach(key => {
    if (Array.isArray(params[key])) {
      params[key].forEach(value => queryParams.append(key, value));
    } else {
      queryParams.append(key, params[key]);
    }
  });

  const path = `/employees?${queryParams.toString()}`;

  try {
    const response = await fetchApiJson(path, { method: 'GET' });
    return response;
  } catch (error) {
    console.error('Fetch error:', error);
    throw error;
  }
};
/**
 * IM NOT SURE YET UPDATE ME LATER PLZ
 *
 * @param {Object} params The parameters for the fetch call.
 * @returns {Promise<Object>} The result of the fetch call.
 */
export const fetchEmployeeSearchApi = async (params) => {
  const queryParams = new URLSearchParams();

  Object.keys(params).forEach(key => {
    if (Array.isArray(params[key])) {
      params[key].forEach(value => queryParams.append(key, value));
    } else {
      queryParams.append(key, params[key]);
    }
  });

  const path = `/employees/search?${queryParams.toString()}`;

  try {
    const response = await fetchApiJson(path, { method: 'GET' });
    return response;
  } catch (error) {
    console.error('Fetch error:', error);
    throw error;
  }
};


export const setSearchParam = (paramName, paramValue, condition = true, replace = true) => {
  const searchParams = new URLSearchParams(window.location.search);
  if (condition !== false && paramValue) {
    searchParams.set(paramName, paramValue);
  } else {
    searchParams.delete(paramName);
  }

  const newUrl = `${window.location.pathname}?${searchParams.toString()}`;

  if (replace) {
    window.history.replaceState(null, '', newUrl);
  } else {
    window.history.pushState(null, '', newUrl);
  }
};

export const getSearchParam = (paramName) => {
  const searchParams = new URLSearchParams(window.location.search);
  return searchParams.get(paramName);
};