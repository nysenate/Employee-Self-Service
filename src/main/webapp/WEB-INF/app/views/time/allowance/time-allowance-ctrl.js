import { fetchApiJson } from "app/utils/fetchJson";
import { hoursDiffHighlighterCustom } from "app/views/time/helpers";

/**
 * Compute remaining allowance, hours, and total hours
 * based on the highest salary present during the given date range
 *
 * @param allowance
 * @param {Object} dateRange - object with params 'beginDate' and 'endDate'
 */
export function computeRemaining (allowance, dateRange) {
  let highestRate = 0;
  allowance.salaryRecs.forEach((salaryRec) => {
    // Select only temporary salaries that are effective during the record date range
    if (salaryRec.payType === 'TE' &&
      new Date(salaryRec.effectDate) < new Date(dateRange.endDate) &&
      new Date(dateRange.beginDate) < new Date(salaryRec.endDate)) {
      if (salaryRec.salaryRate > highestRate) {
        highestRate = salaryRec.salaryRate;
      }
    }
  });

  // Not exist yet: remainingAllowance, remainingHours, totalHours
  allowance.remainingAllowance = allowance.yearlyAllowance - allowance.moneyUsed;
  allowance.remainingHours = allowance.remainingAllowance / highestRate;
  allowance.remainingHours = Math.floor(allowance.remainingHours*4)/4;;
  allowance.totalHours = allowance.hoursUsed + allowance.remainingHours;
}

/**
 * Get the number of available work hours at the selected salary rate
 *  such that the record cost does not exceed the employee's annual allowance
 * @returns {number}
 */
export function getAvailableHours (allowance, tempWorkHours) {
  let remainingHours = (allowance || {}).remainingHours;
  console.log('remainingHours', remainingHours);
  console.log('remainingHours - tempWorkHours',remainingHours - tempWorkHours);
  return remainingHours - tempWorkHours;
}


// export const getAvailableHours = (allowance, subtractingAmount) => {
//   return hoursDiffHighlighterCustom(getTotalAllowedHours(allowance),subtractingAmount);
// }


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