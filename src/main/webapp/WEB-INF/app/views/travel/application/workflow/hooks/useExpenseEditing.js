import { useRef, useState } from "react";
import { useCalculateLodgingRate } from "../../submit/hooks/useExpenseMutations";
import {
  applyEditableExpenses,
  createEditableExpenses,
  validateExpenses,
} from "../../submit/expenseModel";
import { needsExpenseRecalculation } from "../workflowReducer";

/** Owns expense inputs and calculations. The parent saves and navigates in
 * onComplete, returning the saved draft (if any) and handling save errors.
 * calculate is shared with the workflow's administrative override step.
 */
export function useExpenseEditing({
  initialDraft,
  state,
  dispatch,
  calculate,
  reportValidationResult,
  onComplete,
}) {
  const [expenses, setExpenses] = useState(() =>
    createEditableExpenses(initialDraft),
  );
  const [expenseErrors, setExpenseErrors] = useState({});
  const [lodgingErrors, setLodgingErrors] = useState({});
  const [pendingLodgingRows, setPendingLodgingRows] = useState({});
  const [expenseCalculationError, setExpenseCalculationError] = useState(null);
  const [isPending, setIsPending] = useState(false);
  const expenseActionPendingRef = useRef(false);
  const lodgingRequestRef = useRef({});
  const calculateLodging = useCalculateLodgingRate();

  function update(expenses) {
    setExpenseErrors({});
    setExpenses(expenses);
    dispatch({
      type: "UPDATE_DRAFT",
      draft: applyEditableExpenses(state.workingDraft, expenses),
    });
  }

  function resetFromDraft(draft) {
    setExpenses(createEditableExpenses(draft));
    setExpenseErrors({});
    setLodgingErrors({});
    setPendingLodgingRows({});
    setExpenseCalculationError(null);
    lodgingRequestRef.current = {};
  }

  function clearPendingLodging(index) {
    delete lodgingRequestRef.current[index];
    setPendingLodgingRows((current) => {
      const next = { ...current };
      delete next[index];
      return next;
    });
  }

  async function completeExpenseAction(action) {
    if (
      expenseActionPendingRef.current ||
      Object.keys(lodgingRequestRef.current).length
    )
      return;
    setExpenseCalculationError(null);
    const errors = validateExpenses(expenses);
    setExpenseErrors(errors);
    if (!reportValidationResult(errors) || Object.keys(lodgingErrors).length)
      return;
    expenseActionPendingRef.current = true;
    setIsPending(true);
    try {
      let draftToUse = applyEditableExpenses(state.workingDraft, expenses);
      if (needsExpenseRecalculation({ ...state, workingDraft: draftToUse })) {
        draftToUse = await calculate(draftToUse);
        dispatch({ type: "APPLY_CALCULATED_EXPENSES", draft: draftToUse });
        setExpenses(createEditableExpenses(draftToUse));
      }
      if (action === "save") {
        const savedDraft = await onComplete(action, draftToUse);
        if (savedDraft) setExpenses(createEditableExpenses(savedDraft));
      } else {
        onComplete(action, draftToUse);
      }
    } catch {
      setExpenseCalculationError(
        "Your expense totals could not be calculated. Your entered information is still available; please try again.",
      );
    } finally {
      expenseActionPendingRef.current = false;
      setIsPending(false);
    }
  }

  async function handleLodgingSelect(row, index, address) {
    const requestId = {};
    lodgingRequestRef.current[index] = requestId;
    if (!address?.zip5) {
      clearPendingLodging(index);
      setLodgingErrors((current) => ({
        ...current,
        [index]: "Select a recognized hotel address containing a ZIP code.",
      }));
      return;
    }
    setLodgingErrors((current) => {
      const next = { ...current };
      delete next[index];
      return next;
    });
    setPendingLodgingRows((current) => ({ ...current, [index]: true }));
    dispatch({
      type: "UPDATE_EXPENSE_ROW",
      group: "lodgingPerDiems",
      index,
      changes: { address },
    });
    try {
      const result = await calculateLodging.mutateAsync({
        date: row.date,
        address,
      });
      if (requestId !== lodgingRequestRef.current[index]) return;
      dispatch({
        type: "APPLY_LODGING_CALCULATION",
        index,
        calculation: result,
      });
    } catch {
      if (requestId !== lodgingRequestRef.current[index]) return;
      setLodgingErrors((current) => ({
        ...current,
        [index]:
          "The lodging rate could not be calculated. Select another address or try again.",
      }));
    } finally {
      if (requestId === lodgingRequestRef.current[index]) {
        clearPendingLodging(index);
      }
    }
  }

  return {
    expenses,
    errors: expenseErrors,
    lodgingErrors,
    pendingLodgingRows,
    calculationError: expenseCalculationError,
    isPending,
    isLodgingPending: Object.keys(pendingLodgingRows).length > 0,
    update,
    resetFromDraft,
    complete: completeExpenseAction,
    selectLodging: handleLodgingSelect,
  };
}
