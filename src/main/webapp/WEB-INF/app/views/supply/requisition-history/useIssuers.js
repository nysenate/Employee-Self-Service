import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

function getQueryKey() {
  return ["supply", "issuers", "list"];
}

export function useIssuers() {
  return useQuery({
    queryKey: getQueryKey(),
    queryFn: () => {
      return fetchApiJson(`/supply/employees/issuers`).then(
        (body) => body.result,
      );
    },
    staleTime: 1000 * 60 * 5,
    throwOnError: true,
  });
}
