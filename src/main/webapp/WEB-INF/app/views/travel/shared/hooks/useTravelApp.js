import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

export function useTravelApp(id) {
  return useQuery({
    queryKey: ["travel", "applications", id],
    queryFn: () => fetchApiJson(`/travel/applications/${id}`),
    staleTime: 0,
    throwOnError: true,
    enabled: Boolean(id),
  });
}
