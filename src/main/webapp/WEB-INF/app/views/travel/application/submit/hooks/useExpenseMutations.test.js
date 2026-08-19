import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { act, renderHook } from "@testing-library/react";
import { afterEach, describe, expect, it, vi } from "vitest";
import {
  useCalculateLodgingRate,
  useCalculateTravelExpenses,
} from "./useExpenseMutations";

const wrapper = ({ children }) => (
  <QueryClientProvider client={new QueryClient()}>
    {children}
  </QueryClientProvider>
);
afterEach(() => vi.unstubAllGlobals());

describe("expense mutations", () => {
  it("requests every authoritative expense calculation", async () => {
    const fetchMock = vi
      .fn()
      .mockResolvedValue({
        ok: true,
        json: async () => ({ result: { id: 1 } }),
      });
    vi.stubGlobal("fetch", fetchMock);
    const { result } = renderHook(() => useCalculateTravelExpenses(), {
      wrapper,
    });
    await act(() => result.current.mutateAsync({ id: 1 }));
    const init = fetchMock.mock.calls[0][1];
    expect(init.method).toBe("PATCH");
    expect(JSON.parse(init.body).options).toEqual([
      "ALLOWANCES",
      "MEAL_PER_DIEMS",
      "LODGING_PER_DIEMS",
      "MILEAGE_PER_DIEMS",
    ]);
  });

  it("posts the lodging date and selected address", async () => {
    const fetchMock = vi
      .fn()
      .mockResolvedValue({ ok: true, json: async () => ({ result: {} }) });
    vi.stubGlobal("fetch", fetchMock);
    const { result } = renderHook(() => useCalculateLodgingRate(), { wrapper });
    await act(() =>
      result.current.mutateAsync({
        date: "2026-08-10",
        address: { zip5: "12207" },
      }),
    );
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/v1/travel/lodging-per-diems",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({
          date: "2026-08-10",
          address: { zip5: "12207" },
        }),
      }),
    );
  });
});
