export const travelQueryKeys = {
  all: ["travel"],
  newDraft: () => [...travelQueryKeys.all, "draft", "new"],
  eventTypes: () => [...travelQueryKeys.all, "event-types"],
  modesOfTransportation: () => [
    ...travelQueryKeys.all,
    "mode-of-transportation",
  ],
};
