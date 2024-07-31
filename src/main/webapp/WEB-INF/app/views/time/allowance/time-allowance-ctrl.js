import { fetchApiJson } from "app/utils/fetchJson";

/**
 * Fetch Basic Allowance info with provided parameters.
 *
 * @param {Object} params The parameters for the fetch call.
 * @returns {Promise<Object>} The result of the fetch call.
 */
export const fetchAllowance = async (params) => {
  const queryParams = new URLSearchParams();

  Object.keys(params).forEach(key => {
    if (Array.isArray(params[key])) {
      params[key].forEach(value => queryParams.append(key, value));
    } else {
      queryParams.append(key, params[key]);
    }
  });

  const path = `/allowances?${queryParams.toString()}`;

  try {
    const response = await fetchApiJson(path, { method: 'GET' });
    return response;
  } catch (error) {
    console.error('Fetch error:', error);
    throw error;
  }
};