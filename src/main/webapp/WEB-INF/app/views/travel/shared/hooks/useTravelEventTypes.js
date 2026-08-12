import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { travelQueryKeys } from "./travelQueryKeys";

export function useTravelEventTypes() {
  return useQuery({
    queryKey: travelQueryKeys.eventTypes(),
    queryFn: () =>
      fetchApiJson("/travel/event-types").then((body) => body.result),
    staleTime: Infinity,
  });
}
