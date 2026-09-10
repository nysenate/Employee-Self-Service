import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { travelQueryKeys } from "app/views/travel/shared/hooks/travelQueryKeys";

export function useTravelDraft(draftId) {
  const isResuming = draftId !== undefined;
  const normalizedDraftId = normalizeDraftId(draftId);

  return useQuery({
    queryKey: isResuming
      ? travelQueryKeys.draft(draftId)
      : travelQueryKeys.newDraft(),
    queryFn: () => {
      if (isResuming && normalizedDraftId === null) {
        throw new Error("Invalid travel draft ID");
      }

      const request = isResuming
        ? fetchApiJson(`/travel/drafts/${normalizedDraftId}`)
        : fetchApiJson("/travel/drafts", { method: "PUT" });
      return request.then((body) => body.result);
    },
    retry: false,
    staleTime: Infinity,
  });
}

function normalizeDraftId(draftId) {
  if (draftId === undefined) return null;
  const value = String(draftId);
  if (!/^[1-9]\d*$/.test(value)) return null;

  const parsed = Number(value);
  return Number.isSafeInteger(parsed) ? parsed : null;
}
