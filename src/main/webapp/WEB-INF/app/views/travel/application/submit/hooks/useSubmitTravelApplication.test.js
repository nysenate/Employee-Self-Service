import React from "react";
import {
  QueryClient,
  QueryClientProvider,
  QueryObserver,
} from "@tanstack/react-query";
import { act, renderHook } from "@testing-library/react";
import { afterEach, describe, expect, it, vi } from "vitest";
import { travelQueryKeys } from "app/views/travel/shared/hooks/travelQueryKeys";
import { useSubmitTravelApplication } from "./useSubmitTravelApplication";

afterEach(() => vi.unstubAllGlobals());

describe("useSubmitTravelApplication", () => {
  it("submits the displayed draft and invalidates travel data after success", async () => {
    const fetchMock = vi
      .fn()
      .mockResolvedValue({
        ok: true,
        json: async () => ({ result: { id: 42 } }),
      });
    vi.stubGlobal("fetch", fetchMock);
    const queryClient = new QueryClient();
    const invalidate = vi.spyOn(queryClient, "invalidateQueries");
    const initializeDraft = vi.fn();
    const newDraftObserver = new QueryObserver(queryClient, {
      queryKey: travelQueryKeys.newDraft(),
      queryFn: initializeDraft,
      initialData: { id: 7 },
      staleTime: Infinity,
    });
    const stopObservingNewDraft = newDraftObserver.subscribe(() => {});
    const wrapper = ({ children }) => (
      <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
    );
    const { result } = renderHook(() => useSubmitTravelApplication(), {
      wrapper,
    });
    const draft = { id: 7, amendment: { totalAllowance: 125 } };

    await act(() => result.current.mutateAsync(draft));

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/v1/travel/drafts/submit",
      expect.objectContaining({ method: "POST", body: JSON.stringify(draft) }),
    );
    expect(invalidate).toHaveBeenCalledWith({
      queryKey: travelQueryKeys.all,
      refetchType: "none",
    });
    expect(initializeDraft).not.toHaveBeenCalled();
    stopObservingNewDraft();
  });

  it("does not invalidate travel data when submission fails", async () => {
    vi.stubGlobal(
      "fetch",
      vi
        .fn()
        .mockResolvedValue({
          ok: false,
          statusText: "Error",
          json: async () => ({ message: "failed" }),
        }),
    );
    const queryClient = new QueryClient({
      defaultOptions: { mutations: { retry: false } },
    });
    const invalidate = vi.spyOn(queryClient, "invalidateQueries");
    const wrapper = ({ children }) => (
      <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
    );
    const { result } = renderHook(() => useSubmitTravelApplication(), {
      wrapper,
    });

    await expect(
      act(() => result.current.mutateAsync({ id: 7 })),
    ).rejects.toThrow();
    expect(invalidate).not.toHaveBeenCalled();
  });
});
