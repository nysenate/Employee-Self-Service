import { useNavigate } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

/**
 * Returns basic info about the currently authenticated user or redirects
 * to /logout if no one is authenticated.
 */
export default function useAuthedUser() {
  const navigate = useNavigate();
  const query = useAuthedUserNoRedirect();

  if (query.isError) {
    if (query.error.response.status === 401) {
      navigate("/logout");
    }
  }

  return query;
}

/**
 * Similar to useAuthedUser, but does not log the user out if they are
 * not authenticated.
 */
export function useAuthedUserNoRedirect() {
  return useQuery({
    queryKey: ["authedUser"],
    queryFn: () => {
      return fetchApiJson("/employees/me").then((body) => body.result);
    },
    staleTime: 0,
    retry: (failureCount, error) => {
      console.log(failureCount);
      if (!error?.data?.status?.authorized) {
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
