import { useQuery, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { useEffect } from "react";

export const AUTHED_USER_QUERY_KEY = ["authedUser"];
const AUTHED_USER_STALE_TIME_MS = 30 * 1000;

/**
 * Returns a React Query result for the required authenticated user.
 *
 * Common fields:
 * - data: the authenticated employee record once loaded.
 * - isPending: true while the /employees/me request is loading
 * - isError/error: set when there is no authenticated user or when there is an error.
 *
 * Redirects to /login when the user is confirmed unauthenticated.
 */
export default function useRequireAuthedUser() {
  const queryClient = useQueryClient();
  const query = useAuthedUserNoRedirect();

  useEffect(() => {
    if (query.error?.response?.status === 401) {
      queryClient.removeQueries();
      window.location.replace("/logout");
    }
  }, [query.error, queryClient]);

  return query;
}

/**
 * The same query as useRequireAuthedUser, but does not redirect on authentication failure.
 * Useful on login/logout-adjacent pages where unauthenticated is an expected state.
 */
export function useAuthedUserNoRedirect() {
  return useQuery({
    queryKey: AUTHED_USER_QUERY_KEY,
    queryFn: () => {
      return fetchApiJson("/employees/me").then((body) => body.result);
    },
    staleTime: AUTHED_USER_STALE_TIME_MS,
    retry: (failureCount, error) => {
      if (
        error?.response?.status === 401 ||
        error?.data?.status?.authorized === false
      ) {
        return false;
      }
      return failureCount < 2;
    },
  });
}
