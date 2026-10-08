import { useRef, useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { travelQueryKeys } from "app/views/travel/shared/hooks/travelQueryKeys";
import { classifyApplicationSubmissionError } from "../../workflow/submissionErrors";

export function useSubmitTravelApplication() {
  const queryClient = useQueryClient();
  const [isCommitBlocked, setCommitBlocked] = useState(false);
  const blockedError = useRef(null);

  const mutation = useMutation({
    mutationFn: async (draft) => {
      // Also enforce the block at the request boundary, before React rerenders.
      if (blockedError.current) throw blockedError.current;
      try {
        const body = await fetchApiJson("/travel/drafts/submit", {
          method: "POST",
          payload: draft,
        });
        if (
          body?.success !== true ||
          !Number.isSafeInteger(body.result?.id) ||
          body.result.id <= 0
        ) {
          throw new Error(
            "The submission acknowledgement was missing or invalid.",
          );
        }
        return body.result;
      } catch (error) {
        if (classifyApplicationSubmissionError(error) !== "correction") {
          blockedError.current = error;
          setCommitBlocked(true);
        }
        throw error;
      }
    },
    // This endpoint creates an application each time; retries can duplicate it.
    retry: false,
    onSuccess: () =>
      queryClient.invalidateQueries({
        queryKey: travelQueryKeys.all,
        refetchType: "none",
      }),
  });

  return { ...mutation, isCommitBlocked };
}
