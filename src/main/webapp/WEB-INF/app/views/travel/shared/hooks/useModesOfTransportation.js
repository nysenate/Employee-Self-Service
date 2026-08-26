import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { travelQueryKeys } from "./travelQueryKeys";

export function useModesOfTransportation() {
  return useQuery({
    queryKey: travelQueryKeys.modesOfTransportation(),
    queryFn: () =>
      fetchApiJson("/travel/mode-of-transportation").then(
        (body) => body.result ?? [],
      ),
    staleTime: Infinity,
  });
}
