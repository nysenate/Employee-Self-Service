import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { travelQueryKeys } from "app/views/travel/shared/hooks/travelQueryKeys";

export function useTravelApp(id, { throwOnError = true } = {}) {
  return useQuery({
    queryKey: travelQueryKeys.application(id),
    queryFn: () => fetchApiJson(`/travel/applications/${id}`),
    staleTime: 0,
    throwOnError,
    enabled: Boolean(id),
  });
}
