export const WORKFLOW_STEPS = Object.freeze([
  "Purpose",
  "Outbound",
  "Return",
  "Expenses",
  "Review",
]);

export function createWorkflowState(draft) {
  return {
    serverDraft: draft,
    workingDraft: draft,
    currentStep: 0,
    furthestCompletedStep: -1,
  };
}

export function newTravelApplicationReducer(state, action) {
  switch (action.type) {
    case "UPDATE_DRAFT":
      return { ...state, workingDraft: action.draft };
    case "APPEND_ATTACHMENTS":
      return {
        ...state,
        workingDraft: {
          ...state.workingDraft,
          amendment: {
            ...state.workingDraft.amendment,
            attachments: [
              ...(state.workingDraft.amendment?.attachments ?? []),
              ...action.attachments,
            ],
          },
        },
      };
    case "RESET_BASELINE":
      return {
        ...state,
        serverDraft: action.draft,
        workingDraft: action.draft,
      };
    case "COMPLETE_CURRENT_STEP": {
      if (state.currentStep >= WORKFLOW_STEPS.length - 1) return state;
      return {
        ...state,
        furthestCompletedStep: Math.max(
          state.furthestCompletedStep,
          state.currentStep,
        ),
        currentStep: state.currentStep + 1,
      };
    }
    case "GO_BACK":
      return { ...state, currentStep: Math.max(0, state.currentStep - 1) };
    case "GO_TO_STEP":
      return canNavigateToStep(state, action.step)
        ? { ...state, currentStep: action.step }
        : state;
    default:
      return state;
  }
}

export function canNavigateToStep(state, step) {
  return (
    Number.isInteger(step) &&
    step >= 0 &&
    step < WORKFLOW_STEPS.length &&
    (step === state.currentStep || step <= state.furthestCompletedStep)
  );
}

export function hasUnsavedChanges(state) {
  return (
    JSON.stringify(state.workingDraft) !== JSON.stringify(state.serverDraft)
  );
}
