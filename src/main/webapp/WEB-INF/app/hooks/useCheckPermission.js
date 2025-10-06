import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

export default function useCheckPermission(permission) {
  return useQuery({
    queryKey: ["permissions", "check", permission],
    queryFn: () => {
      return fetchApiJson(`/permissions/check?permission=${permission}`).then(
        (body) => body.result,
      );
    },
    enabled: !!permission,
    staleTime: 1000 * 60 * 10,
    throwOnError: true,
  });
}
