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
});
