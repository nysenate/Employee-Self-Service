import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

function getQueryKey(payPeriodType, year) {
  return ["payPeriods", payPeriodType, year];
}

/**
 * Fetches the pay periods of the given type that fall within the given year.
 * @param payPeriodType The pay period type, i.e. "AF" for affirmation pay periods.
 * @param year The four digit year to fetch pay periods for.
 */
export function usePayPeriods(payPeriodType, year) {
  return useQuery({
    queryKey: getQueryKey(payPeriodType, year),
    queryFn: () => {
      return fetchApiJson(`/periods/${payPeriodType}?year=${year}`).then(
        (body) => body.periods,
      );
    },
    enabled: !!payPeriodType && !!year,
    staleTime: 1000 * 60 * 10,
    throwOnError: true,
  });
}
