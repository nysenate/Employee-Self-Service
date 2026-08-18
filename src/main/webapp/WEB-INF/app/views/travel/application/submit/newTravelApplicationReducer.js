import { createEmptyRoute, initializeOutboundRoute } from "./routeModel";

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
    dirtyRoute: draft.amendment?.route ?? createEmptyRoute(),
    needsRouteRecalculation: false,
  };
}

export function newTravelApplicationReducer(state, action) {
  switch (action.type) {
    case "UPDATE_DRAFT":
      return invalidateFollowingSteps({
        ...state,
        workingDraft: action.draft,
      });
    case "UPDATE_DIRTY_ROUTE":
      return invalidateFollowingSteps({
        ...state,
        dirtyRoute: invalidateCalculatedRoute(action.route),
        needsRouteRecalculation: true,
      });
    case "APPEND_ATTACHMENTS":
      return invalidateFollowingSteps({
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
      });
    case "RESET_BASELINE":
      return {
        ...state,
        serverDraft: action.draft,
        workingDraft: action.draft,
        dirtyRoute: action.draft.amendment?.route ?? state.dirtyRoute,
        needsRouteRecalculation: false,
      };
    case "COMPLETE_CURRENT_STEP": {
      if (state.currentStep >= WORKFLOW_STEPS.length - 1) return state;
      const dirtyRoute =
        state.currentStep === 0
          ? initializeOutboundRoute(
              state.dirtyRoute,
              state.workingDraft.traveler?.empWorkLocation?.address,
            )
          : state.dirtyRoute;
      return {
        ...state,
        dirtyRoute,
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

function invalidateFollowingSteps(state) {
  return {
    ...state,
    furthestCompletedStep: Math.min(
      state.furthestCompletedStep,
      state.currentStep - 1,
    ),
  };
}

function invalidateCalculatedRoute(route) {
  const editableRoute = { ...route };
  delete editableRoute.origin;
  delete editableRoute.destinations;
  return editableRoute;
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
    JSON.stringify(state.workingDraft) !== JSON.stringify(state.serverDraft) ||
    JSON.stringify(state.dirtyRoute) !==
      JSON.stringify(state.serverDraft.amendment?.route ?? createEmptyRoute())
  );
}
