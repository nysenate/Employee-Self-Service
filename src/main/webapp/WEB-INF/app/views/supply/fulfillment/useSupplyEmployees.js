import React from "react";
import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

export function useSupplyEmployees() {
  return useQuery({
    queryKey: ["supply", "employees"],
    queryFn: () => {
      return fetchApiJson(`/supply/employees`).then((body) => body.result);
    },
    staleTime: 1000 * 60 * 5,
    throwOnError: true,
  });
}
