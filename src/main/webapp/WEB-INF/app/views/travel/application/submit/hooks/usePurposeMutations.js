import { useMutation } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

export function useSaveTravelDraft() {
  return useMutation({
    mutationFn: (draft) =>
      fetchApiJson("/travel/drafts", { method: "POST", payload: draft }).then(
        (body) => body.result,
      ),
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
