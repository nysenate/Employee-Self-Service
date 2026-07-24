import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

function getQueryKey(year) {
  return ["holidays", year];
}

function getRangeQueryKey(fromDate, toDate) {
  return ["holidays", fromDate, toDate];
}

/**
 * Fetches the senate holidays that fall within the given date range, keyed by ISO date.
 * Unofficial holidays are excluded, matching the legacy time entry page.
 * @param fromDate Inclusive ISO start date.
 * @param toDate Inclusive ISO end date.
 */
export function useHolidaysDuring(fromDate, toDate) {
  return useQuery({
    queryKey: getRangeQueryKey(fromDate, toDate),
    queryFn: () => {
      return fetchApiJson(
        `/holidays?fromDate=${fromDate}&toDate=${toDate}`,
      ).then((body) =>
        body.holidays.reduce((map, holiday) => {
          if (!holiday.unofficial) {
            map[holiday.date] = holiday;
          }
          return map;
        }, {}),
      );
    },
    enabled: !!fromDate && !!toDate,
    staleTime: 1000 * 60 * 10,
    throwOnError: true,
  });
}

/**
 * Fetches the senate holidays that fall within the given year.
 * @param year The four digit year to fetch holidays for.
 */
export function useHoliday(year) {
  return useQuery({
    queryKey: getQueryKey(year),
    queryFn: () => {
      return fetchApiJson(`/holidays?year=${year}`).then(
        (body) => body.holidays,
      );
    },
    enabled: !!year,
    staleTime: 1000 * 60 * 10,
    throwOnError: true,
  });
}
