import { useQuery } from "@tanstack/react-query";
import { buildQueryString } from "app/utils/apiUtils";
import { fetchApiJson } from "app/api/fetchJson";
import { requisitionKeys } from "app/views/supply/shared/hooks/requisition.queryKeys";

/**
 * React hook that fetches and queries requisitions based on provided filters
 *
 * @param {Object} params - Search parameters for filtering requisitions
 * @param {String} [params.location] - Location to filter requisitions by
 * @param {Number} [params.customerId] - Customer ID to filter requisitions by
 * @param {String[]} [params.status] - Array of requisitions statuses to include in the results
 * @param {String} [params.from] - Start date for filtering requisitions (ISO format, e.g., '2011-12-03T10:15:30')
 * @param {String} [params.to] - End date for filtering requisitions (ISO format, e.g., '2011-12-03T10:15:30')
 * @param {Number} [params.issuerId] - Issuer ID to filter requisitions by
 * @param {String} [params.dateField] - The field to which 'from' and 'to' date filters will apply
 *   - Possible values: "ordered_date_time", "processed_date_time", "completed_date_time", "approved_date_time", "rejected_date_time"
 * @param {Boolean} [params.savedInSfms] - Filter for requisitions saved in SFMS (true) or not saved (false)
 * @param {Number} [params.itemId] - Filter for requisitions containing this specific item ID
 * @param {Boolean} [params.reconciled] - Filter for reconciled requisitions (true) or unreconciled (false)
 * @param {Number|String} [params.limit] - Maximum number of results to return
 * @param {Number} [params.offset] - Number of results to skip before returning (used with 'limit' for pagination)
 * @returns {Object} Query result object containing requisition data, loading state, and error state
 *
 * @example
 * // Fetch requisitions for a specific customer and status within a date range
 * const { data, isLoading, error } = useRequisitionSearch({
 *   customerId: 12345,
 *   status: ['PENDING', 'APPROVED'],
 *   from: '2025-01-01T00:00:00',
 *   to: '2025-03-31T23:59:59',
 *   dateField: 'ordered_date_time'
 * });
 *
 * @see Related function {@link buildQueryString} for query parameter handling
 */
export function useRequisitionSearch(params) {
  const queryString = buildQueryString(params);
  return useQuery({
    queryKey: requisitionKeys.search(params),
    queryFn: ({ signal }) => {
      return fetchApiJson(`/supply/requisitions?${queryString}`, { signal });
    },
    staleTime: 0,
    throwOnError: true,
  });
}
