import {
  createEmptyRoute,
  initializeOutboundRoute,
  initializeReturnRoute,
  toEditableRoute,
  toRouteDto,
} from "../submit/routeModel";
import {
  applyLodgingCalculation,
  expenseSignature,
  updateExpenseRow,
} from "../submit/expenseModel";
import { STANDARD_STEPS } from "./workflowSteps";

export function createWorkflowState(draft) {
  const dirtyRoute = toEditableRoute(
    draft.amendment?.route ?? createEmptyRoute(),
  );
  return {
    serverDraft: draft,
    workingDraft: draft,
    currentStepId: STANDARD_STEPS[0].id,
    completedStepIds: [],
    dirtyRoute,
    calculatedRouteBaseline: toRouteDto(dirtyRoute),
    calculatedExpenseBaseline: expenseSignature(draft),
  };
}

export function workflowReducer(state, action) {
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
    const currentIndex = stepIndex(state.currentStepId);
    if (currentIndex < 0 || currentIndex >= STANDARD_STEPS.length - 1)
      return state;
    const dirtyRoute =
      state.currentStepId === "purpose"
        ? initializeOutboundRoute(
            state.dirtyRoute,
            state.workingDraft.traveler?.empWorkLocation?.address,
          )
        : state.currentStepId === "outbound"
          ? initializeReturnRoute(state.dirtyRoute)
          : state.dirtyRoute;
    const completed = new Set([
      ...state.completedStepIds,
      state.currentStepId,
    ]);
    return {
      ...state,
      dirtyRoute,
      completedStepIds: STANDARD_STEPS.filter((step) => completed.has(step.id)).map(
        (step) => step.id,
      ),
      currentStepId: STANDARD_STEPS[currentIndex + 1].id,
    };
  },
  GO_BACK: (state) => {
    const currentIndex = stepIndex(state.currentStepId);
    if (currentIndex <= 0) return state;
    return {
      ...state,
      currentStepId: STANDARD_STEPS[currentIndex - 1].id,
    };
  },
  GO_TO_STEP: (state, action) =>
    canNavigateToStep(state, action.stepId)
      ? { ...state, currentStepId: action.stepId }
      : state,
};

function invalidateFollowingSteps(state) {
  const currentIndex = stepIndex(state.currentStepId);
  return {
    ...state,
    completedStepIds: state.completedStepIds.filter(
      (stepId) => stepIndex(stepId) < currentIndex,
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

export function canNavigateToStep(state, stepId) {
  return (
    stepIndex(stepId) >= 0 &&
    (stepId === state.currentStepId || state.completedStepIds.includes(stepId))
  );
}

function stepIndex(stepId) {
  return STANDARD_STEPS.findIndex((step) => step.id === stepId);
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
