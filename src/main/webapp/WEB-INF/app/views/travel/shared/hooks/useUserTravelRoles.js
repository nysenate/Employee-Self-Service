import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

/**
 * Fetch the Travel related roles assigned to the current user.
 */
export function useUserTravelRoles() {
  return useQuery({
    queryKey: ["user-roles"],
    queryFn: () => fetchApiJson(`/travel/roles`).then((body) => body.result),
    staleTime: 1000 * 60 * 1,
    throwOnError: true,
  });
}
