import { describe, expect, it } from "vitest";
import {
  normalizeOverrides,
  updateOverride,
  validateOverrides,
} from "./overrideModel";
import {
  createWorkflowState,
  needsExpenseRecalculation,
  workflowReducer,
} from "../workflow/workflowReducer";
import { ADMIN_EDIT_STEPS } from "../workflow/workflowSteps";

const draft = {
  amendment: {
    mealPerDiems: { isOverridden: true, overrideRate: 30 },
    lodgingPerDiems: { isOverridden: true, overrideRate: 200 },
  },
};

describe("administrative expense overrides", () => {
  it.each(["-1", "1.001", "1e3", "NaN", "abc"])(
    "rejects invalid currency %s",
    (amount) => {
      expect(
        validateOverrides(
          updateOverride(draft, "mealPerDiems", { overrideRate: amount }),
        ),
      ).toHaveProperty("mealPerDiems");
    },
  );
  it.each(["0", "", "0.00"])("treats %s as removing the override", (amount) => {
    const changed = updateOverride(draft, "mealPerDiems", {
      overrideRate: amount,
    });
    expect(validateOverrides(changed)).toEqual({});
    expect(normalizeOverrides(changed).amendment.mealPerDiems).toEqual({
      isOverridden: false,
      overrideRate: 0,
    });
  });
  it("removes disabled overrides and detects override-only edits for recalculation", () => {
    const state = createWorkflowState(draft, ADMIN_EDIT_STEPS);
    const changed = updateOverride(draft, "lodgingPerDiems", {
      isOverridden: false,
      overrideRate: 0,
    });
    expect(
      normalizeOverrides(changed).amendment.lodgingPerDiems.overrideRate,
    ).toBe(0);
    expect(needsExpenseRecalculation({ ...state, workingDraft: changed })).toBe(
      true,
    );
    expect(draft.amendment.lodgingPerDiems.overrideRate).toBe(200);
  });
  it("returns to Overrides from Review and invalidates review after changing an override", () => {
    let state = {
      ...createWorkflowState(draft, ADMIN_EDIT_STEPS),
      currentStepId: "expenses",
    };
    state = workflowReducer(state, { type: "COMPLETE_CURRENT_STEP" });
    expect(state.currentStepId).toBe("overrides");
    state = workflowReducer(state, { type: "COMPLETE_CURRENT_STEP" });
    expect(state.currentStepId).toBe("review");
    state = workflowReducer(state, { type: "GO_BACK" });
    expect(state.currentStepId).toBe("overrides");
    state = workflowReducer(state, {
      type: "UPDATE_DRAFT",
      draft: updateOverride(draft, "mealPerDiems", { overrideRate: "42" }),
    });
    expect(state.completedStepIds).not.toContain("overrides");
  });
});
