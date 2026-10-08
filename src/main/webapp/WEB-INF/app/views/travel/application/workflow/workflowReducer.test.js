import { describe, expect, it } from "vitest";
import {
  canNavigateToStep,
  createWorkflowState,
  hasUnsavedChanges,
  needsExpenseRecalculation,
  needsRouteRecalculation,
  workflowReducer,
} from "./workflowReducer";

describe("workflow reducer", () => {
  it("honors first and last bounds and rejects unknown navigation", () => {
    const initial = createWorkflowState({ traveler: {}, amendment: {} });
    expect(workflowReducer(initial, { type: "GO_BACK" })).toBe(initial);
    expect(
      workflowReducer(initial, {
        type: "GO_TO_STEP",
        stepId: "unknown",
      }),
    ).toBe(initial);

    const review = {
      ...initial,
      currentStepId: "review",
      completedStepIds: ["purpose", "outbound", "return", "expenses"],
    };
    expect(
      workflowReducer(review, { type: "COMPLETE_CURRENT_STEP" }),
    ).toBe(review);

    const previous = workflowReducer(review, { type: "GO_BACK" });
    expect(previous.currentStepId).toBe("expenses");
    expect(previous.workingDraft).toBe(review.workingDraft);
  });

  it("tracks completion and only permits current or completed steps", () => {
    let state = createWorkflowState({ purpose: "" });

    expect(state.currentStepId).toBe("purpose");
    expect(state.completedStepIds).toEqual([]);
    expect(canNavigateToStep(state, "outbound")).toBe(false);
    state = workflowReducer(state, {
      type: "COMPLETE_CURRENT_STEP",
    });
    expect(state.currentStepId).toBe("outbound");
    expect(state.completedStepIds).toEqual(["purpose"]);
    expect(canNavigateToStep(state, "purpose")).toBe(true);
    expect(canNavigateToStep(state, "return")).toBe(false);

    state = workflowReducer(state, {
      type: "GO_TO_STEP",
      stepId: "purpose",
    });
    expect(state.currentStepId).toBe("purpose");
  });

  it("compares the working draft with the last successful baseline", () => {
    let state = createWorkflowState({ purpose: "Hearing" });
    expect(hasUnsavedChanges(state)).toBe(false);

    state = workflowReducer(state, {
      type: "UPDATE_DRAFT",
      draft: { purpose: "Forum" },
    });
    expect(hasUnsavedChanges(state)).toBe(true);

    state = workflowReducer(state, {
      type: "RESET_BASELINE",
      draft: { purpose: "Forum" },
    });
    expect(hasUnsavedChanges(state)).toBe(false);
  });

  it("requires sequential navigation after editing a completed purpose step", () => {
    let state = createWorkflowState({ traveler: {}, amendment: {} });
    state = {
      ...state,
      currentStepId: "purpose",
      completedStepIds: ["purpose", "outbound", "return", "expenses"],
    };

    state = workflowReducer(state, {
      type: "UPDATE_DRAFT",
      draft: {
        traveler: {},
        amendment: { purposeOfTravel: { eventName: "Updated" } },
      },
    });

    expect(state.completedStepIds).toEqual([]);
    expect(canNavigateToStep(state, "outbound")).toBe(false);
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
      currentStepId: "outbound",
      completedStepIds: [
        "purpose",
        "outbound",
        "return",
        "expenses",
        "review",
      ],
    };

    state = workflowReducer(state, {
      type: "UPDATE_DIRTY_ROUTE",
      route: {
        ...state.dirtyRoute,
        outboundLegs: [{ travelDate: "01/07/2026" }],
      },
    });

    expect(state.completedStepIds).toEqual(["purpose"]);
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
      currentStepId: "outbound",
      completedStepIds: ["purpose", "outbound", "return"],
    };

    const afterValidation = workflowReducer(state, {
      type: "UPDATE_DIRTY_ROUTE",
      route: structuredClone(state.dirtyRoute),
    });
    expect(afterValidation).toBe(state);
    expect(needsRouteRecalculation(afterValidation)).toBe(false);
    expect(afterValidation.completedStepIds).toEqual([
      "purpose",
      "outbound",
      "return",
    ]);

    state = workflowReducer(afterValidation, {
      type: "COMPLETE_CURRENT_STEP",
    });
    expect(state.currentStepId).toBe("return");
    expect(needsRouteRecalculation(state)).toBe(false);
  });

  it("initializes Return and keeps authoritative calculations dirty until saved", () => {
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
    let state = {
      ...createWorkflowState(draft),
      currentStepId: "outbound",
    };
    state = workflowReducer(state, {
      type: "COMPLETE_CURRENT_STEP",
    });
    expect(state.currentStepId).toBe("return");
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
    state = workflowReducer(state, {
      type: "APPLY_CALCULATED_DRAFT",
      draft: calculated,
    });
    expect(state.workingDraft).toBe(calculated);
    expect(state.dirtyRoute).toEqual(calculated.amendment.route);
    expect(needsRouteRecalculation(state)).toBe(false);
    expect(needsExpenseRecalculation(state)).toBe(false);
    expect(state.serverDraft).toBe(draft);
    expect(hasUnsavedChanges(state)).toBe(true);
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

    state = workflowReducer(state, {
      type: "UPDATE_DIRTY_ROUTE",
      route: {
        ...state.dirtyRoute,
        returnLegs: [{ travelDate: "01/07/2026" }],
      },
    });
    expect(needsRouteRecalculation(state)).toBe(true);

    state = workflowReducer(state, {
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
    state = workflowReducer(state, {
      type: "UPDATE_EXPENSE_ROW",
      group: "lodgingPerDiems",
      index: 0,
      changes: { isReimbursementRequested: false },
    });
    expect(needsExpenseRecalculation(state)).toBe(true);

    state = workflowReducer(state, {
      type: "APPLY_LODGING_CALCULATION",
      index: 0,
      calculation: { rate: 175, isReimbursementRequested: true },
    });
    expect(
      state.workingDraft.amendment.lodgingPerDiems.allLodgingPerDiems[0],
    ).toMatchObject({ rate: 175, isReimbursementRequested: false });

    state = workflowReducer(state, {
      type: "APPLY_CALCULATED_EXPENSES",
      draft: state.workingDraft,
    });
    expect(needsExpenseRecalculation(state)).toBe(false);
  });
});
