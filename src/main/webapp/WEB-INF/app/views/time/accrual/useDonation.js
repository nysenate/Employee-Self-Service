import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

const DONATION_QUERY_KEY = ["donation"];

/**
 * Fetches how much sick time the employee may donate, and how much they have accrued.
 * A maxDonation of 0 means they are not eligible to donate right now.
 */
export function useDonationInfo(empId) {
  return useQuery({
    queryKey: [...DONATION_QUERY_KEY, "info", empId],
    queryFn: () => {
      return fetchApiJson(`/donation/info?empId=${empId}`).then(
        (body) => body.result,
      );
    },
    enabled: !!empId,
    staleTime: 1000 * 60,
    throwOnError: true,
  });
}

/**
 * Fetches the employee's donations for a year, most recent first.
 * Each entry is a "M/D: hours" string built by the server.
 */
export function useDonationHistory(empId, year) {
  return useQuery({
    queryKey: [...DONATION_QUERY_KEY, "history", empId, year],
    queryFn: () => {
      return fetchApiJson(`/donation/history?empId=${empId}&year=${year}`).then(
        (body) => body.result,
      );
    },
    enabled: !!empId && !!year,
    staleTime: 1000 * 60,
    throwOnError: true,
  });
}

/**
 * Donates sick time. Call with { empId, hoursToDonate }.
 * A donation is permanent, so this is only reached from the confirmation dialogs.
 */
export function useSubmitDonation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ empId, hoursToDonate }) => {
      return fetchApiJson(
        `/donation/submit?empId=${empId}&hoursToDonate=${hoursToDonate}`,
        { method: "POST", payload: {} },
      );
    },
    onSuccess: () => {
      // The donation changes both the remaining allowance and the history below it.
      return Promise.all([
        queryClient.invalidateQueries({ queryKey: DONATION_QUERY_KEY }),
        queryClient.invalidateQueries({ queryKey: ["accruals"] }),
      ]);
    },
  });
}
