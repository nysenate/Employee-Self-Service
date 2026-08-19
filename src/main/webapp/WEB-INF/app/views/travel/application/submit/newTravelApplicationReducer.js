import {
  createEmptyRoute,
  initializeOutboundRoute,
  initializeReturnRoute,
  toEditableRoute,
  toRouteDto,
} from "./routeModel";
import {
  applyLodgingCalculation,
  expenseSignature,
  updateExpenseRow,
} from "./expenseModel";

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
    calculatedExpenseBaseline: expenseSignature(draft),
  };
}

export function newTravelApplicationReducer(state, action) {
  const reduce = ACTION_REDUCERS[action.type];
  return reduce ? reduce(state, action) : state;
}

const ACTION_REDUCERS = {
  UPDATE_DRAFT: (state, action) =>
    invalidateFollowingSteps({
      ...state,
      workingDraft: action.draft,
    }),
  UPDATE_DIRTY_ROUTE: (state, action) =>
    routesEqual(state.dirtyRoute, action.route)
      ? state
      : invalidateFollowingSteps({
          ...state,
          dirtyRoute: invalidateCalculatedRoute(action.route),
        }),
  APPEND_ATTACHMENTS: (state, action) =>
    invalidateFollowingSteps({
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
    }),
  RESET_BASELINE: (state, action) => ({
    ...state,
    serverDraft: action.draft,
    workingDraft: action.draft,
    dirtyRoute: action.draft.amendment?.route
      ? toEditableRoute(action.draft.amendment.route)
      : state.dirtyRoute,
    calculatedRouteBaseline: action.draft.amendment?.route
      ? toRouteDto(toEditableRoute(action.draft.amendment.route))
      : state.calculatedRouteBaseline,
    calculatedExpenseBaseline: expenseSignature(action.draft),
  }),
  APPLY_CALCULATED_DRAFT: (state, action) => ({
    ...state,
    serverDraft: action.draft,
    workingDraft: action.draft,
    dirtyRoute: toEditableRoute(action.draft.amendment.route),
    calculatedRouteBaseline: toRouteDto(
      toEditableRoute(action.draft.amendment.route),
    ),
    calculatedExpenseBaseline: expenseSignature(action.draft),
  }),
  APPLY_CALCULATED_EXPENSES: (state, action) => ({
    ...state,
    workingDraft: action.draft,
    calculatedExpenseBaseline: expenseSignature(action.draft),
  }),
  UPDATE_EXPENSE_ROW: (state, action) =>
    invalidateFollowingSteps({
      ...state,
      workingDraft: updateExpenseRow(
        state.workingDraft,
        action.group,
        action.index,
        action.changes,
      ),
    }),
  APPLY_LODGING_CALCULATION: (state, action) =>
    invalidateFollowingSteps({
      ...state,
      workingDraft: applyLodgingCalculation(
        state.workingDraft,
        action.index,
        action.calculation,
      ),
    }),
  COMPLETE_CURRENT_STEP: (state) => {
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
  },
  GO_BACK: (state) => ({
    ...state,
    currentStep: Math.max(0, state.currentStep - 1),
  }),
  GO_TO_STEP: (state, action) =>
    canNavigateToStep(state, action.step)
      ? { ...state, currentStep: action.step }
      : state,
};

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

export function needsExpenseRecalculation(state) {
  return (
    expenseSignature(state.workingDraft) !== state.calculatedExpenseBaseline
  );
}
