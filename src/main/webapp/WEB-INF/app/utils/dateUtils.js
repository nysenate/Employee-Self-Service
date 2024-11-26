import { format, parseISO } from "date-fns";

/**
 * Converts an ISO date string to a short date format
 * i.e. 10/14/1983
 * @param isoDate an ISO date string, i.e. 2015-09-24 00:00:00
 */
export function isoToShortDate(isoDate) {
  const date = parseISO(isoDate)
  return format(date, "MM/dd/yyyy")
}

/**
 * Converts an ISO date string to a medium date format
 * i.e. Jan 1, 2022
 * @param isoDate an ISO date string, i.e. 2020-08-14T15:45:44
 */
export function isoToMediumDate(isoDate) {
  const date = parseISO(isoDate)
  return format(date, "MMM d, yyyy")
}

/**
 * Converts an ISO date string to a long date format
 * i.e. January 1, 2024
 * @param isoDate
 * @returns {string}
 */
export function isoToLongDate(isoDate) {
  const date = parseISO(isoDate)
  return format(date, "MMMM d, yyyy")
}