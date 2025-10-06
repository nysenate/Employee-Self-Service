import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

function getQueryKey(empId, detail) {
  return ["employee", "detail", empId, "full", detail];
}

export function useEmployee(empId, detail = true) {
  return useQuery({
    queryKey: getQueryKey(empId, detail),
    queryFn: () => {
      return fetchApiJson(`/employees?empId=${empId}&detail=${detail}`).then(
        (body) => body.employee,
      );
    },
    enable: !!empId,
    staleTime: 60000,
    throwOnError: true,
  });
}
