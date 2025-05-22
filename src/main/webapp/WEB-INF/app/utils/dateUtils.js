import { format, isValid, parseISO } from "date-fns";

/**
 * Converts an ISO date string to a short date format
 * i.e. 10/14/1983
 * @param isoDate an ISO date string, i.e. 2015-09-24 00:00:00
 * @returns {string} isoDate in short form or an empty string if isoDate is invalid.
 */
export function isoToShortDate(isoDate) {
  return formatDate(isoDate, "MM/dd/yyyy");
}

/**
 * Converts an ISO date string to a short date with time format
 * i.e. 10/14/1983 02:31pm
 * @param isoDatetime
 * @returns {string|string}
 */
export function isoToShortDateTime(isoDatetime) {
  return formatDate(isoDatetime, "MM/dd/yyyy h:mmaaa");
}

/**
 * Converts an ISO date string to a medium date format
 * i.e. Jan 1, 2022
 * @param isoDate an ISO date string, i.e. 2020-08-14T15:45:44
 * @returns {string} isoDate in medium form or an empty string if isoDate is invalid.
 */
export function isoToMediumDate(isoDate) {
  return formatDate(isoDate, "MMM d, yyyy");
}

/**
 * Converts an ISO date string to a long date format
 * i.e. January 1, 2024
 * @param isoDate
 * @returns {string} isoDate in long form or an empty string if isoDate is invalid.
 */
export function isoToLongDate(isoDate) {
  return formatDate(isoDate, "MMMM d, yyyy");
}

/**
 * Returns true if dateString is a valid date.
 * @param dateString
 * @returns {boolean}
 */
export function isValidDateString(dateString) {
  if (!dateString || typeof dateString !== "string") return false;
  const date = new Date(dateString);
  return !isNaN(date.getTime()) && dateString.length >= 10; // Basic ISO date length check
}

function formatDate(isoDate, formatStr) {
  // Prevent type error from calling parseISO with a non string.
  if (typeof isoDate !== "string") {
    return "";
  }
  const date = parseISO(isoDate);
  if (!isValid(date)) {
    return "";
  }
  return format(date, formatStr);
}
