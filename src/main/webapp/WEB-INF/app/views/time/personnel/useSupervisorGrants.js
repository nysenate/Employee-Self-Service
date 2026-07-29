import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

const GRANTS_QUERY_KEY = ["supervisorGrants"];

/**
 * Fetches the chain of supervisors above an employee: the supervisors they may delegate their
 * employees' record approvals to.
 */
export function useSupervisorChain(empId) {
  return useQuery({
    queryKey: ["supervisorChain", empId],
    queryFn: () => {
      return fetchApiJson(`/supervisor/chain?empId=${empId}`).then(
        (body) => body.result.supChain,
      );
    },
    enabled: !!empId,
    staleTime: 1000 * 60 * 5,
    throwOnError: true,
  });
}

/** Fetches the grants this supervisor has handed out. */
export function useSupervisorGrants(supId) {
  return useQuery({
    queryKey: [...GRANTS_QUERY_KEY, supId],
    queryFn: () => {
      return fetchApiJson(`/supervisor/grants?supId=${supId}`).then(
        (body) => body.grants,
      );
    },
    enabled: !!supId,
    staleTime: 1000 * 60 * 5,
    throwOnError: true,
  });
}

/** Fetches the grants other supervisors have handed to this one. */
export function useSupervisorOverrides(supId) {
  return useQuery({
    queryKey: ["supervisorOverrides", supId],
    queryFn: () => {
      return fetchApiJson(`/supervisor/overrides?supId=${supId}`).then(
        (body) => body.overrides,
      );
    },
    enabled: !!supId,
    staleTime: 1000 * 60 * 5,
    throwOnError: true,
  });
}

/**
 * Saves changed grants. Call with an array of
 * { granteeSupervisorId, granterSupervisorId, active, startDate, endDate }.
 */
export function useSaveSupervisorGrants() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (grants) => {
      return fetchApiJson("/supervisor/grants", {
        method: "POST",
        payload: grants,
      });
    },
    onSuccess: () => {
      // A grant changes who may review whose records, so the supervisor groups go stale too.
      return Promise.all([
        queryClient.invalidateQueries({ queryKey: GRANTS_QUERY_KEY }),
        queryClient.invalidateQueries({ queryKey: ["supEmpGroup"] }),
      ]);
    },
  });
}
