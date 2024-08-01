import { fetchApiJson } from "app/utils/fetchJson";
import { hoursDiffHighlighterCustom } from "app/views/time/helpers";


export const getMaxSalaryRate = (allowance) => {
  if (allowance.salaryRecs.length > 1) {
    let max = 0;
    allowance.salaryRecs.forEach((rec) => {
      max = Math.max(max, rec.salaryRate);
    });
    return max;
  }
  return allowance.salaryRecs[0];
}

export const getTotalAllowedHours = (allowance) => {
  if (allowance) {
    const totalAllowedHours = allowance.yearlyAllowance/getMaxSalaryRate(allowance);
    console.log('Max salary Rate: ', getMaxSalaryRate(allowance), "YrAllowance: ",allowance.yearlyAllowance,'TotalAlllow: ', Math.floor(totalAllowedHours*4)/4)
    return Math.floor(totalAllowedHours*4)/4;
  }
  return 0;
};

export const getAvailableHours = (allowance, subtractingAmount) => {
  return hoursDiffHighlighterCustom(getTotalAllowedHours(allowance),subtractingAmount);
}

// Like getAvailableHours but without signs and colors provided by hoursDiffHighlighterCustom
export const getExpectedHours = (allowance, subtractingAmount) => {
  let expHrs = (getTotalAllowedHours(allowance) - subtractingAmount).toFixed(2);
  return `${expHrs}`;
}

export const consoleL = (allowance, str) => {
  console.log("Str: ", str);
  console.log("Peruseage: ", allowance);
  return `${str}`;
}

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


/**
 * Fetch Basic Allowance info with provided parameters.
 *
 * @param {Object} params The parameters for the fetch call.
 * @returns {Promise<Object>} The result of the fetch call.
 */
export const fetchPeriodAllowanceUsage = async (params) => {
  const queryParams = new URLSearchParams();

  Object.keys(params).forEach(key => {
    if (Array.isArray(params[key])) {
      params[key].forEach(value => queryParams.append(key, value));
    } else {
      queryParams.append(key, params[key]);
    }
  });

  const path = `/allowances/period?${queryParams.toString()}`;

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
export const fetchAllowancesActiveYears = async (params) => {
  const queryParams = new URLSearchParams();

  Object.keys(params).forEach(key => {
    if (Array.isArray(params[key])) {
      params[key].forEach(value => queryParams.append(key, value));
    } else {
      queryParams.append(key, params[key]);
    }
  });

  const path = `/allowances/active-years?${queryParams.toString()}`;

  try {
    const response = await fetchApiJson(path, { method: 'GET' });
    return response;
  } catch (error) {
    console.error('Fetch error:', error);
    throw error;
  }
};

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