import { useMutation } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

export function useCalculateTravelRoute() {
  return useMutation({
    mutationFn: (draft) =>
      fetchApiJson("/travel/drafts", {
        method: "PATCH",
        payload: { options: ["ROUTE"], draft },
      }).then((body) => body.result),
  });
}
