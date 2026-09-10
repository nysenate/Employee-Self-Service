export const travelQueryKeys = {
  all: ["travel"],
  drafts: () => [...travelQueryKeys.all, "drafts"],
  draft: (draftId) => [...travelQueryKeys.drafts(), "detail", String(draftId)],
  newDraft: () => [...travelQueryKeys.drafts(), "new"],
  eventTypes: () => [...travelQueryKeys.all, "event-types"],
  modesOfTransportation: () => [
    ...travelQueryKeys.all,
    "mode-of-transportation",
  ],
};
