import { useQuery } from "@tanstack/react-query";
import { getHolidays } from "app/api/holiday";

function getQueryKey(year) {
  return ["year", year];
}

export function useHoliday(year) {
  return useQuery({
    queryKey: getQueryKey(year),
    queryFn: () => {
      return getHolidays(year).then((body) => body);
    },
    staleTime: 60000,
    throwOnError: true,
  });
}
