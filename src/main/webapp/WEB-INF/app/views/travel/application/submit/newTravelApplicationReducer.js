import {
  createEmptyRoute,
  initializeOutboundRoute,
  initializeReturnRoute,
  toEditableRoute,
  toRouteDto,
} from "./routeModel";

export const WORKFLOW_STEPS = Object.freeze([
  "Purpose",
  "Outbound",
  "Return",
  "Expenses",
  "Review",
]);

export function createWorkflowState(draft) {
  const dirtyRoute = toEditableRoute(
    draft.amendment?.route ?? createEmptyRoute(),
  );
  return {
    serverDraft: draft,
    workingDraft: draft,
    currentStep: 0,
    furthestCompletedStep: -1,
    dirtyRoute,
    calculatedRouteBaseline: toRouteDto(dirtyRoute),
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
      if (routesEqual(state.dirtyRoute, action.route)) return state;
      return invalidateFollowingSteps({
        ...state,
        dirtyRoute: invalidateCalculatedRoute(action.route),
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
        dirtyRoute: action.draft.amendment?.route
          ? toEditableRoute(action.draft.amendment.route)
          : state.dirtyRoute,
        calculatedRouteBaseline: action.draft.amendment?.route
          ? toRouteDto(toEditableRoute(action.draft.amendment.route))
          : state.calculatedRouteBaseline,
      };
    case "APPLY_CALCULATED_DRAFT":
      return {
        ...state,
        serverDraft: action.draft,
        workingDraft: action.draft,
        dirtyRoute: toEditableRoute(action.draft.amendment.route),
        calculatedRouteBaseline: toRouteDto(
          toEditableRoute(action.draft.amendment.route),
        ),
      };
    case "COMPLETE_CURRENT_STEP": {
      if (state.currentStep >= WORKFLOW_STEPS.length - 1) return state;
      const dirtyRoute =
        state.currentStep === 0
          ? initializeOutboundRoute(
              state.dirtyRoute,
              state.workingDraft.traveler?.empWorkLocation?.address,
            )
          : state.currentStep === 1
            ? initializeReturnRoute(state.dirtyRoute)
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

function routesEqual(first, second) {
  return JSON.stringify(first) === JSON.stringify(second);
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
  const serverRoute = toRouteDto(
    toEditableRoute(state.serverDraft.amendment?.route ?? createEmptyRoute()),
  );
  return (
    JSON.stringify(state.workingDraft) !== JSON.stringify(state.serverDraft) ||
    JSON.stringify(toRouteDto(state.dirtyRoute)) !== JSON.stringify(serverRoute)
  );
}

export function needsRouteRecalculation(state) {
  return !routesEqual(
    toRouteDto(state.dirtyRoute),
    state.calculatedRouteBaseline,
  );
}
