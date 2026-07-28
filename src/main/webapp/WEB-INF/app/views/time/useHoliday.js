import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

function getQueryKey(year) {
  return ["year", year];
}

export function useHoliday(year) {
  return useQuery({
    queryKey: getQueryKey(year),
    queryFn: () => {
      return fetchApiJson(`/holidays/${year}`).then((body) => body);
    },
    staleTime: 1000 * 60 * 1,
    throwOnError: true,
  });
}
