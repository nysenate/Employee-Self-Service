import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

const SUPERVISOR_RECORDS_QUERY_KEY = ["timeRecords", "supervisor"];

/**
 * Fetches every active time record under a supervisor, keyed by employee id.
 *
 * @param supId The supervisor whose employees' records are wanted.
 * @param from Inclusive ISO start date.
 * @param to Exclusive ISO end date.
 */
export function useSupervisorRecords(supId, from, to) {
  return useQuery({
    queryKey: [...SUPERVISOR_RECORDS_QUERY_KEY, supId, from, to],
    queryFn: () => {
      // A MapView of employee id to their records nests the map under "items".
      return fetchApiJson(
        `/timerecords/supervisor?supId=${supId}&from=${from}&to=${to}`,
      ).then((body) => body.result.items);
    },
    enabled: !!supId && !!from && !!to,
    staleTime: 1000 * 60,
    throwOnError: true,
  });
}

/**
 * Approves or rejects reviewed records, one request per record as the legacy page did.
 * Call with an array of { timeRecordId, action, remarks }, where action is "submit" or "reject".
 */
export function useReviewRecords() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (reviews) => {
      return Promise.all(
        reviews.map((review) => {
          const params = new URLSearchParams({
            timeRecordId: review.timeRecordId,
            action: review.action,
          });
          if (review.remarks) {
            params.set("remarks", review.remarks);
          }
          return fetchApiJson(`/timerecords/review?${params}`, {
            method: "POST",
            payload: {},
          });
        }),
      );
    },
    onSuccess: () => {
      // A reviewed record leaves the supervisor's queue, so every supervisor list is stale.
      return queryClient.invalidateQueries({ queryKey: ["timeRecords"] });
    },
  });
}

/**
 * Sends employees an email reminder about the given records.
 * Resolves with one result per employee, each saying whether their reminder was sent.
 */
export function useSendReminders() {
  return useMutation({
    mutationFn: (records) => {
      return fetchApiJson("/timerecords/reminder", {
        method: "POST",
        payload: records,
      }).then((body) => body.result);
    },
  });
}
