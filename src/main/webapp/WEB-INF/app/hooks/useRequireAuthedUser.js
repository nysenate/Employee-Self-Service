import { useNavigate } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { useEffect } from "react";

/**
 * Returns a React Query result for the required authenticated user.
 *
 * Common fields:
 * - data: the authenticated employee record once loaded.
 * - isPending: true while the /employees/me request is loading
 * - isError/error: set when there is no authenticated user or when there is an error.
 *
 * Redirects to /logout when the user is confirmed unauthenticated.
 */
export default function useRequireAuthedUser() {
  const navigate = useNavigate();
  const query = useAuthedUserNoRedirect();

  useEffect(() => {
    if (query.error?.response?.status === 401) {
      navigate("/logout");
    }
  }, [query.error, navigate]);

  return query;
}

/**
 * The same query as useRequireAuthedUser, but does not redirect on authentication failure.
 * Useful on login/logout-adjacent pages where unauthenticated is an expected state.
 */
export function useAuthedUserNoRedirect() {
  return useQuery({
    queryKey: ["authedUser"],
    queryFn: () => {
      return fetchApiJson("/employees/me").then((body) => body.result);
    },
    staleTime: 0,
    retry: (failureCount, error) => {
      if (error?.data?.status?.authorized === false) {
        // User is confirmed to be not authorized, no need to retry.
        return false;
      } else if (Number(failureCount) > 2) {
        // Only retry up to 3 times.
        return false;
      }
      return true;
    },
  });
}
