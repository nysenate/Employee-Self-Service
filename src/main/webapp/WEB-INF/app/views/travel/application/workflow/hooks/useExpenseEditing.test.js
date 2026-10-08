import { useReducer } from "react";
import { act, renderHook } from "@testing-library/react";
import { beforeEach, expect, it, vi } from "vitest";
import { createWorkflowState, workflowReducer } from "../workflowReducer";
import { useExpenseEditing } from "./useExpenseEditing";

const { lodging } = vi.hoisted(() => ({ lodging: vi.fn() }));
vi.mock("../../submit/hooks/useExpenseMutations", () => ({
  useCalculateLodgingRate: () => ({ mutateAsync: lodging }),
}));

function draft(tolls = 0) {
  return {
    amendment: {
      allowances: {
        tolls,
        parking: 0,
        alternateTransportation: 0,
        trainAndPlane: 0,
        registration: 0,
      },
      lodgingPerDiems: {
        allLodgingPerDiems: [
          { id: 1, date: "08/10/2026", isReimbursementRequested: true },
        ],
      },
    },
  };
}
function setup() {
  const initialDraft = draft();
  const calculate = vi.fn(async (value) => value);
  const onComplete = vi.fn();
  const hook = renderHook(() => {
    const [state, dispatch] = useReducer(
      workflowReducer,
      initialDraft,
      createWorkflowState,
    );
    const editing = useExpenseEditing({
      initialDraft,
      state,
      dispatch,
      calculate,
      reportValidationResult: (errors) => !Object.keys(errors).length,
      onComplete,
    });
    return { editing, state };
  });
  return { ...hook, calculate, onComplete };
}
function deferred() {
  let resolve, reject;
  const promise = new Promise((res, rej) => {
    resolve = res;
    reject = rej;
  });
  return { promise, resolve, reject };
}
const row = { date: "08/10/2026" };
const address = { zip5: "12207" };
beforeEach(() => vi.resetAllMocks());

it("validates inputs and retains failed calculations for retry", async () => {
  const { result, calculate, onComplete } = setup();
  act(() =>
    result.current.editing.update({
      ...result.current.editing.expenses,
      tolls: "-1",
    }),
  );
  await act(() => result.current.editing.complete("next"));
  expect(result.current.editing.errors.tolls).toBeTruthy();
  expect(calculate).not.toHaveBeenCalled();
  act(() =>
    result.current.editing.update({
      ...result.current.editing.expenses,
      tolls: "12.50",
    }),
  );
  calculate.mockRejectedValueOnce(new Error("unavailable"));
  await act(() => result.current.editing.complete("next"));
  expect(result.current.editing.expenses.tolls).toBe("12.50");
  expect(result.current.editing.calculationError).toContain(
    "could not be calculated",
  );
  expect(onComplete).not.toHaveBeenCalled();
  await act(() => result.current.editing.complete("next"));
  expect(result.current.editing.calculationError).toBeNull();
  expect(onComplete).toHaveBeenCalledWith(
    "next",
    expect.objectContaining({
      amendment: expect.objectContaining({
        allowances: expect.objectContaining({ tolls: 12.5 }),
      }),
    }),
  );
});

it("skips unchanged calculations and refreshes inputs from the saved response", async () => {
  const { result, calculate, onComplete } = setup();
  onComplete.mockResolvedValue(draft(20));
  await act(() => result.current.editing.complete("save"));
  expect(calculate).not.toHaveBeenCalled();
  expect(onComplete).toHaveBeenCalledWith("save", expect.any(Object));
  expect(result.current.editing.expenses.tolls).toBe("20");
});

it("blocks duplicate actions throughout saving and preserves inputs if saving fails", async () => {
  const saving = deferred();
  const { result, onComplete } = setup();
  onComplete.mockReturnValue(saving.promise);
  let pending;
  act(() => {
    pending = result.current.editing.complete("save");
  });
  expect(result.current.editing.isPending).toBe(true);
  await act(() => result.current.editing.complete("next"));
  expect(onComplete).toHaveBeenCalledTimes(1);
  // The parent reports save errors and returns no saved draft on failure.
  await act(async () => {
    saving.resolve(undefined);
    await pending;
  });
  expect(result.current.editing.expenses.tolls).toBe("0");
  expect(result.current.editing.calculationError).toBeNull();
  expect(result.current.editing.isPending).toBe(false);
});

it("ignores a superseded lodging response and blocks completion while lookup is pending", async () => {
  const first = deferred(),
    second = deferred();
  lodging
    .mockReturnValueOnce(first.promise)
    .mockReturnValueOnce(second.promise);
  const { result, onComplete } = setup();
  let a, b;
  act(() => {
    a = result.current.editing.selectLodging(row, 0, address);
  });
  act(() => {
    b = result.current.editing.selectLodging(row, 0, { zip5: "14202" });
  });
  await act(() => result.current.editing.complete("next"));
  expect(onComplete).not.toHaveBeenCalled();
  await act(async () => {
    first.resolve({ rate: 10 });
    await a;
  });
  expect(result.current.editing.isLodgingPending).toBe(true);
  expect(
    result.current.state.workingDraft.amendment.lodgingPerDiems
      .allLodgingPerDiems[0].rate,
  ).toBeUndefined();
  await act(async () => {
    second.resolve({ rate: 20 });
    await b;
  });
  expect(result.current.editing.isLodgingPending).toBe(false);
  expect(
    result.current.state.workingDraft.amendment.lodgingPerDiems
      .allLodgingPerDiems[0].rate,
  ).toBe(20);
});

it("invalidates pre-reset requests even when a new request uses the same row", async () => {
  const first = deferred(),
    second = deferred();
  lodging
    .mockReturnValueOnce(first.promise)
    .mockReturnValueOnce(second.promise);
  const { result } = setup();
  let a, b;
  act(() => {
    a = result.current.editing.selectLodging(row, 0, address);
  });
  act(() => result.current.editing.resetFromDraft(draft(30)));
  act(() => {
    b = result.current.editing.selectLodging(row, 0, address);
  });
  await act(async () => {
    first.reject(new Error("old request"));
    await a;
  });
  expect(result.current.editing.lodgingErrors).toEqual({});
  expect(result.current.editing.isLodgingPending).toBe(true);
  expect(result.current.editing.expenses.tolls).toBe("30");
  await act(async () => {
    second.resolve({ rate: 50 });
    await b;
  });
  expect(
    result.current.state.workingDraft.amendment.lodgingPerDiems
      .allLodgingPerDiems[0].rate,
  ).toBe(50);
});

it("clears pending state when an invalid address supersedes a lodging lookup", async () => {
  const lookup = deferred();
  lodging.mockReturnValueOnce(lookup.promise);
  const { result, onComplete } = setup();
  let pending;
  act(() => {
    pending = result.current.editing.selectLodging(row, 0, address);
  });
  await act(() => result.current.editing.selectLodging(row, 0, null));
  expect(result.current.editing.isLodgingPending).toBe(false);
  expect(result.current.editing.lodgingErrors[0]).toContain("ZIP code");
  await act(async () => {
    lookup.resolve({ rate: 99 });
    await pending;
  });
  expect(
    result.current.state.workingDraft.amendment.lodgingPerDiems
      .allLodgingPerDiems[0].rate,
  ).toBeUndefined();
  await act(() => result.current.editing.complete("next"));
  expect(onComplete).not.toHaveBeenCalled();
});
