import { describe, expect, it } from "vitest";
import {
  canNavigateToStep,
  createWorkflowState,
  hasUnsavedChanges,
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
    expect(state.needsRouteRecalculation).toBe(true);
    expect(state.dirtyRoute.origin).toBeUndefined();
    expect(state.dirtyRoute.destinations).toBeUndefined();
    expect(state.dirtyRoute.returnLegs).toEqual(
      draft.amendment.route.returnLegs,
    );
    expect(state.workingDraft.amendment.allowances).toEqual({ parking: 10 });
  });
});
