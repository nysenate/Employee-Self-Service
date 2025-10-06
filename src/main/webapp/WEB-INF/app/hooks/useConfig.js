import { fetchApiJson } from "app/api/fetchJson";
import { useQuery } from "@tanstack/react-query";

export function useConfig() {
  return useQuery({
    queryKey: ["config"],
    queryFn: () => fetchApiJson("/config").then((body) => body.result.config),
    staleTime: 1000 * 60 * 10,
    throwOnError: true,
  });
}
