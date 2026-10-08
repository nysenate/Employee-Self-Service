import { useMutation } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

export function useCalculateTravelExpenses() {
  return useMutation({
    mutationFn: (draft) =>
      fetchApiJson("/travel/drafts", {
        method: "PATCH",
        payload: {
          options: [
            "ALLOWANCES",
            "MEAL_PER_DIEMS",
            "LODGING_PER_DIEMS",
            "MILEAGE_PER_DIEMS",
          ],
          draft,
        },
      }).then((body) => body.result),
  });
}

export function useCalculateLodgingRate() {
  return useMutation({
    mutationFn: ({ date, address }) =>
      fetchApiJson("/travel/lodging-per-diems", {
        method: "POST",
        payload: { date, address },
      }).then((body) => body.result),
  });
}
