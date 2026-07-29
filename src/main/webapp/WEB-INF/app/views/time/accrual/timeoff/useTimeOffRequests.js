import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

/**
 * Queries and mutations for time off requests.
 *
 * Every endpoint under /accruals/request returns its view object bare rather than wrapped in a
 * response envelope, except the POST which answers with { result: { requestId } }.
 */

const REQUESTS_QUERY_KEY = ["timeOffRequests"];

/** Everything time off related goes stale together whenever a request changes. */
export function useInvalidateTimeOffRequests() {
  const queryClient = useQueryClient();
  return () => queryClient.invalidateQueries({ queryKey: REQUESTS_QUERY_KEY });
}

/**
 * Fetches an employee's time off requests overlapping a date range.
 * Either bound may be omitted, in which case the server falls back to its own far date.
 */
export function useEmployeeTimeOffRequests(empId, startRange, endRange) {
  return useQuery({
    queryKey: [...REQUESTS_QUERY_KEY, "employee", empId, startRange, endRange],
    queryFn: () => {
      const params = new URLSearchParams();
      if (startRange) {
        params.set("startRange", startRange);
      }
      if (endRange) {
        params.set("endRange", endRange);
      }
      return fetchApiJson(`/accruals/request/employee/${empId}?${params}`);
    },
    enabled: !!empId,
    staleTime: 1000 * 60,
    throwOnError: true,
  });
}

/** Fetches a single request. */
export function useTimeOffRequest(requestId) {
  return useQuery({
    queryKey: [...REQUESTS_QUERY_KEY, "single", requestId],
    queryFn: () => fetchApiJson(`/accruals/request/${requestId}`),
    enabled: !!requestId,
    staleTime: 1000 * 60,
    retry: false,
  });
}

/** Fetches the approved and upcoming requests of everyone a supervisor is responsible for. */
export function useSupervisorActiveRequests(supId) {
  return useQuery({
    queryKey: [...REQUESTS_QUERY_KEY, "supervisor", "active", supId],
    queryFn: () => fetchApiJson(`/accruals/request/supervisor/${supId}/active`),
    enabled: !!supId,
    staleTime: 1000 * 60,
    throwOnError: true,
  });
}

/** Fetches the requests waiting on a supervisor's approval. */
export function useSupervisorPendingRequests(supId) {
  return useQuery({
    queryKey: [...REQUESTS_QUERY_KEY, "supervisor", "pending", supId],
    queryFn: () =>
      fetchApiJson(`/accruals/request/supervisor/${supId}/approval`),
    enabled: !!supId,
    staleTime: 1000 * 60,
    throwOnError: true,
  });
}

/**
 * Saves or submits a request. Resolves with the request id, which a request being saved for the
 * first time did not have.
 */
export function useSaveTimeOffRequest() {
  const invalidate = useInvalidateTimeOffRequests();
  return useMutation({
    mutationFn: (request) => {
      return fetchApiJson("/accruals/request", {
        method: "POST",
        payload: request,
      }).then((body) => body.result.requestId);
    },
    onSuccess: invalidate,
  });
}

/**
 * Approves or disapproves requests, one call per request.
 * Call with an array of { requestId, action, comment }, where action is APPROVE or DISAPPROVE.
 *
 * The legacy page fired these off without waiting and reported success regardless; here the
 * whole batch is awaited so a failure actually surfaces.
 */
export function useReviewTimeOffRequests() {
  const invalidate = useInvalidateTimeOffRequests();
  return useMutation({
    mutationFn: (reviews) => {
      return Promise.all(
        reviews.map((review) => {
          const params = new URLSearchParams({ action: review.action });
          // An empty comment must be left off entirely, or it is stored as a blank one.
          if (review.comment && review.comment.trim()) {
            params.set("comment", review.comment.trim());
          }
          return fetchApiJson(
            `/accruals/request/review/${review.requestId}?${params}`,
            { method: "POST", payload: {} },
          );
        }),
      );
    },
    onSuccess: invalidate,
  });
}
