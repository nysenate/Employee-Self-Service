export const travelQueryKeys = {
  all: ["travel"],
  drafts: () => [...travelQueryKeys.all, "drafts"],
  draft: (draftId) => [...travelQueryKeys.drafts(), "detail", String(draftId)],
  newDraft: () => [...travelQueryKeys.drafts(), "new"],
  editorSession: (appId, operation) => [
    ...travelQueryKeys.all,
    "editor-session",
    operation,
    String(appId),
  ],
  applications: () => [...travelQueryKeys.all, "applications"],
  application: (appId) => [...travelQueryKeys.applications(), appId],
  reviews: () => [...travelQueryKeys.all, "review"],
  eventTypes: () => [...travelQueryKeys.all, "event-types"],
  modesOfTransportation: () => [
    ...travelQueryKeys.all,
    "mode-of-transportation",
  ],
};
