import { fetchApiJson } from "app/utils/fetchJson";

/**
 * Fetch _____ based on provided parameters.
 *
 * @param {Object} params The parameters for the fetch call.
 * @returns {Promise<Object>} The result of the fetch call.
 */
export const fetchAttendanceRecordApi = async (params) => {
  const queryParams = new URLSearchParams();

  Object.keys(params).forEach(key => {
    if (Array.isArray(params[key])) {
      params[key].forEach(value => queryParams.append(key, value));
    } else {
      queryParams.append(key, params[key]);
    }
  });

  const path = `/attendance/records?${queryParams.toString()}`;

  try {
    const response = await fetchApiJson(path, { method: 'GET' });
    // console.log("fetchAttendanceRecordApi with params: ", params, " response: ", response);
    return response;
  } catch (error) {
    console.error('Fetch error:', error);
    throw error;
  }
};

/**
 * Fetch _____ based on provided parameters.
 *
 * @param {Object} params The parameters for the fetch call.
 * @returns {Promise<Object>} The result of the fetch call.
 */
export const fetchTimeRecordApi = async (params) => {
  const queryParams = new URLSearchParams();

  Object.keys(params).forEach(key => {
    if (Array.isArray(params[key])) {
      params[key].forEach(value => queryParams.append(key, value));
    } else {
      queryParams.append(key, params[key]);
    }
  });

  const path = `/timerecords?${queryParams.toString()}`;

  try {
    const response = await fetchApiJson(path, { method: 'GET' });
    // console.log("fetchTimeRecordApi with params: ", params, " response: ", response);
    return response;
  } catch (error) {
    console.error('Fetch error:', error);
    throw error;
  }
};

/**
 * Fetch _____ based on provided parameters.
 *
 * @param {Object} params The parameters for the fetch call.
 * @returns {Promise<Object>} The result of the fetch call.
 */
export const fetchActiveYearsTimeRecordsApi = async (params) => {
  const queryParams = new URLSearchParams();

  Object.keys(params).forEach(key => {
    if (Array.isArray(params[key])) {
      params[key].forEach(value => queryParams.append(key, value));
    } else {
      queryParams.append(key, params[key]);
    }
  });

  const path = `/timerecords/activeYears?${queryParams.toString()}`;

  try {
    const response = await fetchApiJson(path, { method: 'GET' });
    // console.log("fetchActiveYearsTimeRecordsApi with params: ", params, " response: ", response);
    return response;
  } catch (error) {
    console.error('Fetch error:', error);
    throw error;
  }
};


// GRANT:::
/**
 * Fetch _____ based on provided parameters.
 *
 * @param {Object} params The parameters for the fetch call.
 * @returns {Promise<Object>} The result of the fetch call.
 */
export const fetchChainData = async (params) => {
  const queryParams = new URLSearchParams();

  Object.keys(params).forEach(key => {
    if (Array.isArray(params[key])) {
      params[key].forEach(value => queryParams.append(key, value));
    } else {
      queryParams.append(key, params[key]);
    }
  });

  const path = `/supervisor/chain?${queryParams.toString()}`;

  try {
    const response = await fetchApiJson(path, { method: 'GET' });
    // console.log("fetchChainData with params: ", params, " response: ", response);
    return response;
  } catch (error) {
    console.error('Fetch error:', error);
    throw error;
  }
};

/**
 * Fetch _____ based on provided parameters.
 *
 * @param {Object} params The parameters for the fetch call.
 * @returns {Promise<Object>} The result of the fetch call.
 */
export const fetchGrantData = async (params) => {
  const queryParams = new URLSearchParams();

  Object.keys(params).forEach(key => {
    if (Array.isArray(params[key])) {
      params[key].forEach(value => queryParams.append(key, value));
    } else {
      queryParams.append(key, params[key]);
    }
  });

  const path = `/supervisor/grants?${queryParams.toString()}`;

  try {
    const response = await fetchApiJson(path, { method: 'GET' });
    // console.log("fetchGrantData with params: ", params, " response: ", response);
    return response;
  } catch (error) {
    console.error('Fetch error:', error);
    throw error;
  }
};

/**
 * Fetch _____ based on provided parameters.
 *
 * @param {Object} params The parameters for the fetch call.
 * @returns {Promise<Object>} The result of the fetch call.
 */
export const fetchOverrideData = async (params) => {
  const queryParams = new URLSearchParams();

  Object.keys(params).forEach(key => {
    if (Array.isArray(params[key])) {
      params[key].forEach(value => queryParams.append(key, value));
    } else {
      queryParams.append(key, params[key]);
    }
  });

  const path = `/supervisor/overrides?${queryParams.toString()}`;

  try {
    const response = await fetchApiJson(path, { method: 'GET' });
    // console.log("fetchOverrideData with params: ", params, " response: ", response);
    return response;
  } catch (error) {
    console.error('Fetch error:', error);
    throw error;
  }
};
