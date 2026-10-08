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
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ success: true, result: { id: 42 } }),
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
      vi.fn().mockResolvedValue({
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

  it.each([
    ["network error", () => Promise.reject(new TypeError("offline"))],
    ...[408, 409, 429, 500, 503].map((status) => [
      `HTTP ${status}`,
      () => Promise.resolve(errorResponse(status)),
    ]),
    [
      "truncated response",
      () =>
        Promise.resolve({
          ok: true,
          json: async () => {
            throw new SyntaxError("invalid JSON");
          },
        }),
    ],
    ...[
      null,
      { result: { id: 42 } },
      { success: false, result: { id: 42 } },
      { success: true },
      { success: true, result: { id: 0 } },
    ].map((body) => [
      `invalid acknowledgement ${JSON.stringify(body)}`,
      () => Promise.resolve({ ok: true, json: async () => body }),
    ]),
  ])(
    "blocks another POST after %s, including after mutation reset",
    async (_label, response) => {
      const fetchMock = vi.fn(response);
      vi.stubGlobal("fetch", fetchMock);
      const { result, invalidate } = renderSubmissionHook();
      const draft = { id: 0, amendment: { totalAllowance: 125 } };

      await expectFailure(result, draft);
      expect(result.current.isCommitBlocked).toBe(true);
      expect(result.current.isSuccess).toBe(false);
      expect(invalidate).not.toHaveBeenCalled();
      expect(fetchMock).toHaveBeenCalledOnce();

      act(() => result.current.reset());
      await expectFailure(result, { ...draft, id: 7 });
      expect(result.current.isCommitBlocked).toBe(true);
      expect(fetchMock).toHaveBeenCalledOnce();
    },
  );

  it.each([401, 403])(
    "blocks submission after access rejection %s",
    async (status) => {
      const fetchMock = vi.fn().mockResolvedValue(errorResponse(status));
      vi.stubGlobal("fetch", fetchMock);
      const { result } = renderSubmissionHook();

      await expectFailure(result, { id: 7 });
      await expectFailure(result, { id: 7 });

      expect(result.current.isCommitBlocked).toBe(true);
      expect(fetchMock).toHaveBeenCalledOnce();
    },
  );

  it.each([400, 422])(
    "permits a deliberate retry after validation rejection %s",
    async (status) => {
      const fetchMock = vi
        .fn()
        .mockResolvedValueOnce(errorResponse(status))
        .mockResolvedValueOnce({
          ok: true,
          json: async () => ({ success: true, result: { id: 42 } }),
        });
      vi.stubGlobal("fetch", fetchMock);
      const { result } = renderSubmissionHook();

      await expectFailure(result, { id: 7 });
      expect(result.current.isCommitBlocked).toBe(false);
      expect(fetchMock).toHaveBeenCalledOnce();

      await act(() => result.current.mutateAsync({ id: 7 }));
      expect(result.current.isSuccess).toBe(true);
      expect(fetchMock).toHaveBeenCalledTimes(2);
    },
  );
});

function errorResponse(status) {
  return {
    ok: false,
    status,
    statusText: "Request failed",
    json: async () => ({ message: "failed" }),
  };
}

function renderSubmissionHook() {
  // A global retry policy must never repeat a submission POST automatically.
  const queryClient = new QueryClient({
    defaultOptions: { mutations: { retry: 2, retryDelay: 0 } },
  });
  const invalidate = vi.spyOn(queryClient, "invalidateQueries");
  const wrapper = ({ children }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
  return {
    ...renderHook(() => useSubmitTravelApplication(), { wrapper }),
    invalidate,
  };
}

async function expectFailure(result, draft) {
  let failure;
  await act(async () => {
    try {
      await result.current.mutateAsync(draft);
    } catch (error) {
      failure = error;
    }
  });
  expect(failure).toBeInstanceOf(Error);
}
