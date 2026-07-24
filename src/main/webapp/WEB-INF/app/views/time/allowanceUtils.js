/**
 * Temporary employee allowance helpers, ported from the legacy AllowanceUtils
 * AngularJS service (assets/js/src/time/allowance/allowance-utils.js).
 */

/** Rounds a number down to a multiple of the given increment. */
function roundDown(number, multiple) {
  const inverse = 1 / multiple;
  return Math.floor(number * inverse) / inverse;
}

/**
 * Returns the allowance with its remaining money, remaining hours and total hours filled in.
 *
 * Remaining hours are worth the highest temporary salary rate in effect during the given
 * date range, so that an employee paid at more than one rate can never over report.
 *
 * @param allowance The allowance usage for a single year.
 * @param dateRange An object with "beginDate" and "endDate" ISO date properties.
 */
export function computeRemaining(allowance, dateRange) {
  if (!allowance) {
    return allowance;
  }

  const highestRate = (allowance.salaryRecs || [])
    .filter(
      (salaryRec) =>
        salaryRec.payType === "TE" &&
        salaryRec.effectDate <= dateRange.endDate &&
        dateRange.beginDate <= salaryRec.endDate,
    )
    .reduce((highest, salaryRec) => Math.max(highest, salaryRec.salaryRate), 0);

  const remainingAllowance = allowance.yearlyAllowance - allowance.moneyUsed;
  const remainingHours = roundDown(remainingAllowance / highestRate, 0.25);

  return {
    ...allowance,
    remainingAllowance,
    remainingHours,
    totalHours: allowance.hoursUsed + remainingHours,
  };
}

/**
 * Returns the work hours still available at the selected salary rate, such that the cost of
 * the record does not exceed the employee's annual allowance.
 */
export function getAvailableHours(allowance, tempWorkHours) {
  return (allowance || {}).remainingHours - tempWorkHours;
}
