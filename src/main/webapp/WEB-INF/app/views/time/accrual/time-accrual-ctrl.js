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
 * Fetch active years based on provided parameters.
 *
 * @param {Object} params The parameters for the fetch call.
 * @returns {Promise<Object>} The result of the fetch call.
 */
export const fetchAccrualActiveYears = async (params) => {
  const queryParams = new URLSearchParams();

  Object.keys(params).forEach(key => {
    if (Array.isArray(params[key])) {
      params[key].forEach(value => queryParams.append(key, value));
    } else {
      queryParams.append(key, params[key]);
    }
  });

  const path = `/accruals/active-years?${queryParams.toString()}`;

  try {
    const response = await fetchApiJson(path, { method: 'GET' });
    return response;
  } catch (error) {
    console.error('Fetch error:', error);
    throw error;
  }
};

/**
 * Fetch accrual summaries based on provided parameters.
 *
 * @param {Object} params The parameters for the fetch call.
 * @returns {Promise<Object>} The result of the fetch call.
 */
export const fetchAccrualSummaries = async (params) => {
  const queryParams = new URLSearchParams();

  Object.keys(params).forEach(key => {
    if (Array.isArray(params[key])) {
      params[key].forEach(value => queryParams.append(key, value));
    } else {
      queryParams.append(key, params[key]);
    }
  });

  const path = `/accruals/history?${queryParams.toString()}`;

  try {
    const response = await fetchApiJson(path, { method: 'GET' });
    return response;
  } catch (error) {
    console.error('Fetch error:', error);
    throw error;
  }
};
