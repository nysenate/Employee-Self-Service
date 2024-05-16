import { format, parseISO } from "date-fns";

/**
 * Converts an ISO date string to a short date format, i.e. 10/14/1983
 * @param isoDate an ISO date string, i.e. 2015-09-24 00:00:00
 */
export function isoToShortDate(isoDate) {
  const date = parseISO(isoDate)
  return format(date, "MM/dd/yyyy")
}
