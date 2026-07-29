import {
  accrualsUsed,
  dayTotal,
  hours,
  REQUEST_HOUR_FIELDS,
} from "app/views/time/accrual/timeoff/timeOffRequestUtils";

/**
 * Validation for a time off request before it is saved or submitted.
 * Ported from the legacy TimeOffRequestValidationService
 * (assets/js/src/time/accrual/time-off-request-validation-service.js).
 */

const ERRORS = {
  numDays: "ERROR: Your request must have at least one day.",
  nullDays: "ERROR: The DATE must be entered for all days in your request.",
  duplicateDate: "ERROR: There are duplicate dates in your request.",
  zeroHours:
    "ERROR: Each day must have more than 0 hours of time off requested.",
  positiveValues: "ERROR: The requested hours off are not all positive.",
  hoursPerDay: "ERROR: There are too many hours requested in one or more days.",
  hourInterval: "ERROR: Not all numeric values are in .5 intervals.",
  miscRequirement:
    "ERROR: Misc hours and misc type must either both be filled in or both be empty.",
  accrualUsage:
    "ERROR: You are requesting more time off than your accruals allow.",
  holidayHour:
    "ERROR: Holiday hours entered does not match the Senate's holiday schedule.",
};

/**
 * Runs every check against a request, returning a message for each one that fails.
 *
 * @param days The days on the request.
 * @param accruals The employee's available { personal, vacation, sick } hours.
 * @param holidays Senate holidays covering the request, keyed by ISO date.
 * @returns An array of error messages, empty when the request is valid.
 */
export function validateTimeOffRequest(days, accruals, holidays) {
  const messages = [];

  if (days.length === 0) {
    messages.push(ERRORS.numDays);
    // Every remaining check reads the days, so there is nothing further to say.
    return messages;
  }

  if (days.some((day) => !day.date)) {
    messages.push(ERRORS.nullDays);
  }

  const dates = days.map((day) => day.date).filter(Boolean);
  if (new Set(dates).size !== dates.length) {
    messages.push(ERRORS.duplicateDate);
  }

  if (days.some((day) => dayTotal(day) <= 0)) {
    messages.push(ERRORS.zeroHours);
  }

  if (
    days.some((day) =>
      REQUEST_HOUR_FIELDS.some((field) => hours(day[field]) < 0),
    )
  ) {
    messages.push(ERRORS.positiveValues);
  }

  if (days.some((day) => dayTotal(day) > 24)) {
    messages.push(ERRORS.hoursPerDay);
  }

  if (days.some((day) => dayTotal(day) % 0.5 !== 0)) {
    messages.push(ERRORS.hourInterval);
  }

  if (days.some(hasMiscMismatch)) {
    messages.push(ERRORS.miscRequirement);
  }

  if (exceedsAccruals(days, accruals)) {
    messages.push(ERRORS.accrualUsage);
  }

  if (holidays && days.some((day) => hasWrongHolidayHours(day, holidays))) {
    messages.push(ERRORS.holidayHour);
  }

  return messages;
}

/**
 * Misc hours and a misc type only make sense together.
 *
 * The legacy check compared hours against 0, but an untouched field is null, so a type chosen
 * with no hours entered slipped through. Both directions are caught here.
 */
function hasMiscMismatch(day) {
  const mismatched = (type, value) =>
    (!type && hours(value) > 0) || (!!type && hours(value) <= 0);
  return (
    mismatched(day.miscType, day.miscHours) ||
    mismatched(day.miscType2, day.misc2Hours)
  );
}

/**
 * The request may not draw more of a given accrual than the employee has available.
 *
 * The legacy service carried this message but never called a check for it, so the request was
 * only rejected later by the server.
 */
function exceedsAccruals(days, accruals) {
  if (!accruals) {
    return false;
  }
  const used = accrualsUsed(days);
  return (
    used.vacation > hours(accruals.vacation) ||
    used.personal > hours(accruals.personal) ||
    used.sick > hours(accruals.sick)
  );
}

/**
 * Holiday hours must match the Senate holiday schedule: the scheduled hours on a holiday, and
 * nothing at all on any other day.
 *
 * The legacy check fetched the holidays and then discarded them without comparing anything, so
 * it always passed. It is a real comparison here.
 */
function hasWrongHolidayHours(day, holidays) {
  if (!day.date) {
    return false;
  }
  const scheduled = hours(holidays[day.date]?.hours);
  return hours(day.holidayHours) !== scheduled;
}
