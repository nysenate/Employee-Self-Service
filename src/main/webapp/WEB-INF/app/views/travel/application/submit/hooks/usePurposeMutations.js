import { useMutation, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { travelQueryKeys } from "app/views/travel/shared/hooks/travelQueryKeys";

export function useSaveTravelDraft() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (draft) =>
      fetchApiJson("/travel/drafts", { method: "POST", payload: draft }).then(
        (body) => body.result,
      ),
    onSuccess: (savedDraft) => {
      queryClient.setQueryData(
        travelQueryKeys.draft(savedDraft.id),
        savedDraft,
      );
      queryClient.invalidateQueries({
        queryKey: travelQueryKeys.drafts(),
        exact: true,
        refetchType: "none",
      });
    },
  });
}

export function useUploadSupportingDocuments() {
  return useMutation({
    mutationFn: (files) => {
      const formData = new FormData();
      files.forEach((file) => formData.append("file", file));
      return fetchApiJson("/travel/drafts/attachment", {
        method: "POST",
        payload: formData,
      }).then((body) => body.result?.items ?? body.result ?? []);
    },
  });
}
