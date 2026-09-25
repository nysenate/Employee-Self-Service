import { useReducer } from "react";
import { act, renderHook } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { createWorkflowState, workflowReducer } from "../workflowReducer";
import { useRouteAdvancement } from "./useRouteAdvancement";

const { lookup, calculate } = vi.hoisted(() => ({
  lookup: vi.fn(),
  calculate: vi.fn(),
}));
vi.mock("app/views/travel/shared/hooks/useAddressCounty", () => ({
  useAddressCounty: () => ({ mutateAsync: lookup }),
}));
vi.mock("../../submit/hooks/useRouteMutations", () => ({
  useCalculateTravelRoute: () => ({ mutateAsync: calculate }),
}));

function setup(
  step = "return",
  { missingCounty = false, longTrip = false } = {},
) {
  const address = (name, county) => ({
    formattedAddressWithCounty: name,
    county,
    zip5: "12207",
    state: "NY",
    country: "United States",
  });
  const albany = { address: address("Albany", "Albany") };
  const buffalo = { address: address("Buffalo", missingCounty ? "" : "Erie") };
  const leg = (from, to, travelDate) => ({
    from,
    to,
    travelDate,
    methodOfTravelDisplayName: "Train",
  });
  const draft = {
    amendment: {
      route: {
        outboundLegs: [leg(albany, buffalo, "08/10/2026")],
        returnLegs: [leg(buffalo, albany, "08/11/2026")],
      },
    },
  };
  const onCalculated = vi.fn();
  const onComplete = vi.fn();
  const reportValidationResult = vi.fn((errors) => !Object.keys(errors).length);
  const hook = renderHook(() => {
    const [state, dispatch] = useReducer(workflowReducer, draft, (initial) => {
      const initialState = createWorkflowState(initial);
      initialState.currentStepId = step;
      // A changed return date requires calculation, including after county prompts.
      initialState.dirtyRoute.returnLegs[0].travelDate = longTrip
        ? "08/20/2026"
        : "08/12/2026";
      return initialState;
    });
    const route = useRouteAdvancement({
      state,
      dispatch,
      reportValidationResult,
      onCalculated,
      onComplete,
    });
    return { route, state };
  });
  return { ...hook, onCalculated, onComplete, reportValidationResult };
}

beforeEach(() => {
  vi.resetAllMocks();
  lookup.mockResolvedValue("");
  calculate.mockImplementation(async (draft) => draft);
});

describe("route advancement", () => {
  it("reports validation errors before looking up counties or calculating", async () => {
    const { result, onComplete, reportValidationResult } = setup();
    act(() =>
      result.current.route.updateRoute({
        ...result.current.state.dirtyRoute,
        returnLegs: [
          { ...result.current.state.dirtyRoute.returnLegs[0], travelDate: "" },
        ],
      }),
    );
    await act(() => result.current.route.advanceReturn("next"));
    expect(result.current.route.errors["segment-0-date"]).toBeTruthy();
    expect(reportValidationResult).toHaveBeenCalled();
    expect(lookup).not.toHaveBeenCalled();
    expect(calculate).not.toHaveBeenCalled();
    expect(onComplete).not.toHaveBeenCalled();
  });

  it("cancels an outbound county prompt and resumes only after the county is supplied", async () => {
    const { result, onComplete } = setup("outbound", { missingCounty: true });
    await act(() => result.current.route.advanceOutbound());
    expect(result.current.route.pendingCounty.addressText).toBe("Buffalo");
    act(() => result.current.route.cancelCounty());
    expect(result.current.route.pendingCounty).toBeNull();
    expect(onComplete).not.toHaveBeenCalled();
    await act(() => result.current.route.advanceOutbound());
    await act(() => result.current.route.submitCounty("Erie"));
    expect(
      result.current.state.dirtyRoute.outboundLegs[0].to.address.county,
    ).toBe("Erie");
    expect(onComplete).toHaveBeenCalledTimes(1);
    expect(onComplete).toHaveBeenCalledWith("next");
    expect(calculate).not.toHaveBeenCalled();
  });

  it.each(["save", "next"])(
    "preserves %s across a county prompt and long-trip confirmation",
    async (action) => {
      const { result, onComplete, onCalculated } = setup("return", {
        missingCounty: true,
        longTrip: true,
      });
      await act(() => result.current.route.advanceReturn(action));
      expect(result.current.route.pendingCounty).not.toBeNull();
      await act(() => result.current.route.submitCounty("Erie"));
      expect(result.current.route.pendingCounty).toBeNull();
      expect(result.current.route.showLongTripWarning).toBe(true);
      expect(calculate).not.toHaveBeenCalled();
      expect(onComplete).not.toHaveBeenCalled();
      await act(() => result.current.route.confirmLongTrip());
      expect(calculate).toHaveBeenCalledTimes(1);
      const calculated = calculate.mock.calls[0][0];
      expect(calculated.amendment.route.returnLegs[0].from.address.county).toBe(
        "Erie",
      );
      expect(onCalculated).toHaveBeenCalledWith(calculated);
      expect(onComplete).toHaveBeenCalledWith(action, calculated);
      expect(result.current.route.isPending).toBe(false);
    },
  );

  it("returns to editing when long-trip confirmation is dismissed", async () => {
    const { result, onComplete } = setup("return", { longTrip: true });
    await act(() => result.current.route.advanceReturn("next"));
    act(() => result.current.route.reviewLongTrip());
    expect(result.current.route.showLongTripWarning).toBe(false);
    expect(result.current.state.dirtyRoute.returnLegs[0].travelDate).toBe(
      "08/20/2026",
    );
    expect(calculate).not.toHaveBeenCalled();
    expect(onComplete).not.toHaveBeenCalled();
  });

  it("blocks overlapping return actions while county lookup is pending", async () => {
    let resolveCounty;
    lookup.mockReturnValue(
      new Promise((resolve) => {
        resolveCounty = resolve;
      }),
    );
    const { result, onComplete } = setup("return", { missingCounty: true });
    let advancement;
    act(() => {
      advancement = result.current.route.advanceReturn("next");
    });
    expect(result.current.route.isPending).toBe(true);
    await act(() => result.current.route.advanceReturn("save"));
    expect(lookup).toHaveBeenCalledTimes(1);
    await act(async () => {
      resolveCounty("Erie");
      await advancement;
    });
    expect(onComplete).toHaveBeenCalledTimes(1);
    expect(onComplete.mock.calls[0][0]).toBe("next");
    expect(result.current.route.isPending).toBe(false);
  });

  it("retains the route after calculation failure and allows retry", async () => {
    calculate.mockRejectedValueOnce({ response: { status: 502 } });
    const { result, onComplete, onCalculated } = setup();
    await act(() => result.current.route.advanceReturn("next"));
    expect(result.current.route.calculationError).toContain(
      "third-party travel service",
    );
    expect(result.current.route.isPending).toBe(false);
    expect(result.current.state.dirtyRoute.returnLegs[0].travelDate).toBe(
      "08/12/2026",
    );
    expect(onCalculated).not.toHaveBeenCalled();
    expect(onComplete).not.toHaveBeenCalled();
    await act(() => result.current.route.advanceReturn("next"));
    expect(result.current.route.calculationError).toBeNull();
    expect(onComplete).toHaveBeenCalledTimes(1);
  });
});
