import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

/**
 * Check the current user's permission. Only an explicit successful grant allows
 * access, including after a failed refresh. Invalid permissions fail closed.
 * Consumers render errors locally; authorization failures are not render errors.
 */
export default function useCheckPermission(permission) {
  const enabled =
    typeof permission === "string" && permission.trim().length > 0;
  const query = useQuery({
    queryKey: ["permissions", "check", permission],
    queryFn: async () => {
      const body = await fetchApiJson(
        `/permissions/check?permission=${encodeURIComponent(permission)}`,
      );
      if (typeof body?.result?.isPermitted !== "boolean") {
        throw new Error("Invalid permission response");
      }
      return body.result;
    },
    enabled,
    staleTime: 1000 * 60 * 10,
    retry: false,
    throwOnError: false,
  });

  return {
    ...query,
    isChecking: enabled && query.isPending,
    isAllowed: enabled && query.isSuccess && query.data?.isPermitted === true,
  };
}
