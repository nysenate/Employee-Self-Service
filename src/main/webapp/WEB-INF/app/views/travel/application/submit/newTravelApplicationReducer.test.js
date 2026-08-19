import { describe, expect, it } from "vitest";
import {
  canNavigateToStep,
  createWorkflowState,
  hasUnsavedChanges,
  needsExpenseRecalculation,
  needsRouteRecalculation,
  newTravelApplicationReducer,
} from "./newTravelApplicationReducer";

describe("new travel application reducer", () => {
  it("tracks completion and only permits current or completed steps", () => {
    let state = createWorkflowState({ purpose: "" });

    expect(canNavigateToStep(state, 1)).toBe(false);
    state = newTravelApplicationReducer(state, {
      type: "COMPLETE_CURRENT_STEP",
    });
    expect(state.currentStep).toBe(1);
    expect(state.furthestCompletedStep).toBe(0);
    expect(canNavigateToStep(state, 0)).toBe(true);
    expect(canNavigateToStep(state, 2)).toBe(false);

    state = newTravelApplicationReducer(state, { type: "GO_TO_STEP", step: 0 });
    expect(state.currentStep).toBe(0);
  });

  it("compares the working draft with the last successful baseline", () => {
    let state = createWorkflowState({ purpose: "Hearing" });
    expect(hasUnsavedChanges(state)).toBe(false);

    state = newTravelApplicationReducer(state, {
      type: "UPDATE_DRAFT",
      draft: { purpose: "Forum" },
    });
    expect(hasUnsavedChanges(state)).toBe(true);

    state = newTravelApplicationReducer(state, {
      type: "RESET_BASELINE",
      draft: { purpose: "Forum" },
    });
    expect(hasUnsavedChanges(state)).toBe(false);
  });

  it("requires sequential navigation after editing a completed purpose step", () => {
    let state = createWorkflowState({ traveler: {}, amendment: {} });
    state = { ...state, currentStep: 0, furthestCompletedStep: 3 };

    state = newTravelApplicationReducer(state, {
      type: "UPDATE_DRAFT",
      draft: {
        traveler: {},
        amendment: { purposeOfTravel: { eventName: "Updated" } },
      },
    });

    expect(state.furthestCompletedStep).toBe(-1);
    expect(canNavigateToStep(state, 1)).toBe(false);
  });

  it("invalidates derived calculations after editing a completed route step", () => {
    const draft = {
      amendment: {
        route: {
          outboundLegs: [{ travelDate: "01/05/2026" }],
          returnLegs: [{ travelDate: "01/06/2026" }],
          origin: { city: "Albany" },
          destinations: [{ city: "Buffalo" }],
        },
        allowances: { parking: 10 },
      },
    };
    let state = {
      ...createWorkflowState(draft),
      currentStep: 1,
      furthestCompletedStep: 4,
    };

    state = newTravelApplicationReducer(state, {
      type: "UPDATE_DIRTY_ROUTE",
      route: {
        ...state.dirtyRoute,
        outboundLegs: [{ travelDate: "01/07/2026" }],
      },
    });

    expect(state.furthestCompletedStep).toBe(0);
    expect(needsRouteRecalculation(state)).toBe(true);
    expect(state.dirtyRoute.origin).toBeUndefined();
    expect(state.dirtyRoute.destinations).toBeUndefined();
    expect(state.dirtyRoute.returnLegs[0]).toMatchObject(
      draft.amendment.route.returnLegs[0],
    );
    expect(state.workingDraft.amendment.allowances).toEqual({ parking: 10 });
  });

  it("preserves route calculations when revisiting Outbound without changes", () => {
    const calculatedRoute = {
      outboundLegs: [{ travelDate: "01/05/2026" }],
      returnLegs: [{ travelDate: "01/06/2026" }],
      origin: { city: "Albany" },
      destinations: [{ city: "Buffalo" }],
    };
    const draft = { amendment: { route: calculatedRoute } };
    let state = {
      ...createWorkflowState(draft),
      currentStep: 1,
      furthestCompletedStep: 2,
    };

    const afterValidation = newTravelApplicationReducer(state, {
      type: "UPDATE_DIRTY_ROUTE",
      route: structuredClone(state.dirtyRoute),
    });
    expect(afterValidation).toBe(state);
    expect(needsRouteRecalculation(afterValidation)).toBe(false);
    expect(afterValidation.furthestCompletedStep).toBe(2);

    state = newTravelApplicationReducer(afterValidation, {
      type: "COMPLETE_CURRENT_STEP",
    });
    expect(state.currentStep).toBe(2);
    expect(needsRouteRecalculation(state)).toBe(false);
  });

  it("initializes Return after Outbound and applies authoritative calculations", () => {
    const draft = {
      amendment: {
        route: {
          outboundLegs: [
            {
              from: { addressText: "Albany" },
              to: { addressText: "Buffalo" },
              methodOfTravelDisplayName: "Train",
            },
          ],
          returnLegs: [],
        },
      },
    };
    let state = { ...createWorkflowState(draft), currentStep: 1 };
    state = newTravelApplicationReducer(state, {
      type: "COMPLETE_CURRENT_STEP",
    });
    expect(state.currentStep).toBe(2);
    expect(state.dirtyRoute.returnLegs[0]).toMatchObject({
      from: { addressText: "Buffalo" },
      to: { addressText: "Albany" },
      methodOfTravelDisplayName: "Train",
    });

    const calculated = {
      ...draft,
      amendment: {
        ...draft.amendment,
        route: { ...state.dirtyRoute, origin: { city: "Albany" } },
      },
    };
    state = newTravelApplicationReducer(state, {
      type: "APPLY_CALCULATED_DRAFT",
      draft: calculated,
    });
    expect(state.workingDraft).toBe(calculated);
    expect(state.dirtyRoute).toEqual(calculated.amendment.route);
    expect(needsRouteRecalculation(state)).toBe(false);
    expect(needsExpenseRecalculation(state)).toBe(false);
    expect(state.serverDraft).toBe(calculated);
    expect(hasUnsavedChanges(state)).toBe(false);
  });

  it("does not recalculate after route edits are reverted to the calculated baseline", () => {
    const draft = {
      amendment: {
        route: {
          outboundLegs: [{ travelDate: "01/05/2026" }],
          returnLegs: [{ travelDate: "01/06/2026" }],
        },
      },
    };
    let state = createWorkflowState(draft);
    const calculatedRoute = structuredClone(state.dirtyRoute);

    state = newTravelApplicationReducer(state, {
      type: "UPDATE_DIRTY_ROUTE",
      route: {
        ...state.dirtyRoute,
        returnLegs: [{ travelDate: "01/07/2026" }],
      },
    });
    expect(needsRouteRecalculation(state)).toBe(true);

    state = newTravelApplicationReducer(state, {
      type: "UPDATE_DIRTY_ROUTE",
      route: calculatedRoute,
    });
    expect(needsRouteRecalculation(state)).toBe(false);
  });

  it("tracks calculated expenses and applies lodging results to the latest draft", () => {
    const draft = {
      amendment: {
        allowances: { tolls: 0 },
        lodgingPerDiems: {
          allLodgingPerDiems: [
            { id: 1, rate: 100, isReimbursementRequested: true },
          ],
        },
      },
    };
    let state = createWorkflowState(draft);
    state = newTravelApplicationReducer(state, {
      type: "UPDATE_EXPENSE_ROW",
      group: "lodgingPerDiems",
      index: 0,
      changes: { isReimbursementRequested: false },
    });
    expect(needsExpenseRecalculation(state)).toBe(true);

    state = newTravelApplicationReducer(state, {
      type: "APPLY_LODGING_CALCULATION",
      index: 0,
      calculation: { rate: 175, isReimbursementRequested: true },
    });
    expect(
      state.workingDraft.amendment.lodgingPerDiems.allLodgingPerDiems[0],
    ).toMatchObject({ rate: 175, isReimbursementRequested: false });

    state = newTravelApplicationReducer(state, {
      type: "APPLY_CALCULATED_EXPENSES",
      draft: state.workingDraft,
    });
    expect(needsExpenseRecalculation(state)).toBe(false);
  });
});
