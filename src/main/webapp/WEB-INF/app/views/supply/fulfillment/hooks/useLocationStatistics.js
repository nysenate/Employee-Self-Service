import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { requisitionKeys } from "app/views/supply/shared/hooks/requisition.queryKeys";

/**
 * Return item order counts for each location for the given month and year.
 * @param year The year.
 * @param month The month, 1 based index.
 * @returns {UseQueryResult<*|undefined, DefaultError>}
 */
export function useLocationStatistics(year, month) {
  return useQuery({
    queryKey: requisitionKeys.locationStatistics(year, month),
    queryFn: () => {
      return fetchApiJson(
        `/supply/statistics/locations?year=${year}&month=${month}`,
      ).then((body) => body.result.items);
    },
    staleTime: 1000 * 60,
    throwOnError: true,
  });
}
