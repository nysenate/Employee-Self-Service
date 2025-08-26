import { useQuery } from "@tanstack/react-query";
import { getPayPeriods } from "app/api/payPeriod";

function getQueryKey(payPeriod, year) {
  return ["payPeriod", payPeriod, "year", year];
}

export function usePayPeriods(payPeriod, year) {
  return useQuery({
    queryKey: getQueryKey(payPeriod),
    queryFn: () => {
      return getPayPeriods(payPeriod, year).then((body) => body);
    },
    staleTime: 60000,
    throwOnError: true,
  });
}
