import { useRef, useState } from "react";
import { needsExpenseRecalculation } from "../workflow/workflowReducer";
import {
  applyOverrides,
  editableOverrides,
  normalizeOverrides,
  validateOverrides,
} from "./overrideModel";

export function useExpenseOverrides(initialDraft) {
  const [overrides, setOverrides] = useState(() =>
    editableOverrides(initialDraft),
  );
  const [errors, setErrors] = useState({});
  const [calculationError, setCalculationError] = useState(null);
  const pendingRef = useRef(false);

  function update(draft) {
    setErrors({});
    setOverrides(editableOverrides(draft));
  }

  function resetFromDraft(draft) {
    setOverrides(editableOverrides(draft));
    setErrors({});
    setCalculationError(null);
  }

  async function complete({ state, calculate, reportValidationResult }) {
    if (pendingRef.current) return null;
    const draft = applyOverrides(state.workingDraft, overrides);
    const validationErrors = validateOverrides(draft);
    setErrors(validationErrors);
    if (!reportValidationResult(validationErrors)) return null;

    pendingRef.current = true;
    setCalculationError(null);
    try {
      const normalized = normalizeOverrides(draft);
      const calculated = needsExpenseRecalculation({
        ...state,
        workingDraft: normalized,
      })
        ? await calculate(normalized)
        : normalized;
      setOverrides(editableOverrides(calculated));
      return calculated;
    } catch {
      setCalculationError(
        "Your override totals could not be calculated. Your changes are still available; please try again.",
      );
      return null;
    } finally {
      pendingRef.current = false;
    }
  }

  return {
    errors,
    calculationError,
    preview: (draft) => applyOverrides(draft, overrides),
    isDirty: (draft) =>
      JSON.stringify(overrides) !== JSON.stringify(editableOverrides(draft)),
    update,
    resetFromDraft,
    complete,
  };
}
