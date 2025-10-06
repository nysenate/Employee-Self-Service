import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

function getQueryKey(payPeriod, year) {
  return ["payPeriod", payPeriod, "year", year];
}

export function usePayPeriods(payPeriod, year) {
  return useQuery({
    queryKey: getQueryKey(payPeriod),
    queryFn: () => {
      return fetchApiJson(`/periods/${payPeriod}?year=${year}`).then(
        (body) => body,
      );
    },
    staleTime: 1000 * 60 * 1,
    throwOnError: true,
  });
}
