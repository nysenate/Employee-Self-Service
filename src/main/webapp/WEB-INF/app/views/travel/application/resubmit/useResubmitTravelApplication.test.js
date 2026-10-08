import React from "react";
import {
  QueryClient,
  QueryClientProvider,
  QueryObserver,
} from "@tanstack/react-query";
import { act, renderHook, waitFor } from "@testing-library/react";
import { afterEach, describe, expect, it, vi } from "vitest";
import { fetchApiJson } from "app/api/fetchJson";
import { REVIEW_QUEUE_QUERY_KEY } from "app/views/travel/reviewer/queue/reviewQueueCache";
import { useResubmitTravelApplication } from "./useResubmitTravelApplication";

afterEach(() => vi.unstubAllGlobals());

function createHarness(queryClient = new QueryClient()) {
  const wrapper = ({ children }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
  return { queryClient, wrapper };
}

function errorResponse(status, errorCode) {
  return {
    ok: false,
    status,
    statusText: "Request failed",
    json: async () => ({ errorCode, message: "failed" }),
  };
}

function applicationResponse(isDisapproved) {
  return {
    ok: true,
    json: async () => ({
      result: { id: 42, status: { isDisapproved } },
    }),
  };
}

async function expectMutationFailure(result, draft) {
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

describe("useResubmitTravelApplication", () => {
  it("posts the working draft to the URL application ID and returns SimpleResponse", async () => {
    const acknowledgement = { success: true, message: "Application updated" };
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => acknowledgement,
    });
    vi.stubGlobal("fetch", fetchMock);
    const { wrapper } = createHarness();
    const { result } = renderHook(() => useResubmitTravelApplication(42), {
      wrapper,
    });
    const draft = { id: 0, amendment: { purpose: "Updated purpose" } };

    const response = await act(() => result.current.mutateAsync(draft));

    expect(response).toBe(acknowledgement);
    expect(fetchMock).toHaveBeenCalledTimes(1);
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/v1/travel/application/edit/resubmit/42",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify(draft),
      }),
    );
    expect(fetchMock.mock.calls[0][0]).not.toContain("/travel/drafts");
  });

  it("does not refresh caches or report success when the POST fails", async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: false,
      status: 500,
      statusText: "Server Error",
      json: async () => ({ message: "failed" }),
    });
    vi.stubGlobal("fetch", fetchMock);
    const { queryClient, wrapper } = createHarness();
    const invalidate = vi.spyOn(queryClient, "invalidateQueries");
    const { result } = renderHook(() => useResubmitTravelApplication(42), {
      wrapper,
    });

    await expectMutationFailure(result, { id: 0 });

    expect(result.current.isSuccess).toBe(false);
    expect(invalidate).not.toHaveBeenCalled();
    expect(fetchMock).toHaveBeenCalledTimes(1);
  });

  it("keeps an acknowledged commit successful when the active cache GET fails", async () => {
    const acknowledgement = { success: true, message: "Application updated" };
    const fetchMock = vi.fn((url) => {
      if (url.endsWith("/travel/review/pending")) {
        return Promise.resolve({
          ok: false,
          status: 500,
          statusText: "Server Error",
          json: async () => ({ message: "refresh failed" }),
        });
      }
      return Promise.resolve({
        ok: true,
        json: async () => acknowledgement,
      });
    });
    vi.stubGlobal("fetch", fetchMock);
    const { queryClient, wrapper } = createHarness(
      new QueryClient({ defaultOptions: { queries: { retry: false } } }),
    );
    const queueObserver = new QueryObserver(queryClient, {
      queryKey: REVIEW_QUEUE_QUERY_KEY,
      queryFn: () =>
        fetchApiJson("/travel/review/pending").then(
          (body) => body.result.items,
        ),
      initialData: [],
      staleTime: Infinity,
    });
    const stopObservingQueue = queueObserver.subscribe(() => {});
    const { result } = renderHook(() => useResubmitTravelApplication(42), {
      wrapper,
    });

    const response = await act(() => result.current.mutateAsync({ id: 0 }));

    expect(response).toBe(acknowledgement);
    expect(result.current.isSuccess).toBe(true);
    expect(result.current.error).toBeNull();
    expect(
      fetchMock.mock.calls.filter(([url]) =>
        url.endsWith("/travel/application/edit/resubmit/42"),
      ),
    ).toHaveLength(1);
    expect(
      fetchMock.mock.calls.filter(([url]) =>
        url.endsWith("/travel/review/pending"),
      ),
    ).toHaveLength(1);
    stopObservingQueue();
  });

  it.each([
    [409, undefined, "conflict", true],
    [400, "TRAVEL_RESUBMISSION_CONFLICT", "conflict", true],
    [401, undefined, "access", true],
    [403, undefined, "access", true],
    [400, undefined, "correction", false],
    [422, undefined, "correction", false],
  ])(
    "classifies HTTP %s / %s as %s with blocked=%s",
    async (status, errorCode, recoveryType, isBlocked) => {
      const fetchMock = vi
        .fn()
        .mockResolvedValue(errorResponse(status, errorCode));
      vi.stubGlobal("fetch", fetchMock);
      const { wrapper } = createHarness();
      const { result } = renderHook(() => useResubmitTravelApplication(42), {
        wrapper,
      });

      await expectMutationFailure(result, { id: 0 });

      expect(result.current.recovery.type).toBe(recoveryType);
      expect(result.current.isCommitBlocked).toBe(isBlocked);
      expect(result.current.isSuccess).toBe(false);
      expect(fetchMock).toHaveBeenCalledOnce();
    },
  );

  it.each([
    ["server failure", () => Promise.resolve(errorResponse(500))],
    ["network failure", () => Promise.reject(new TypeError("offline"))],
    [
      "unparseable response",
      () =>
        Promise.resolve({
          ok: true,
          json: async () => {
            throw new SyntaxError("invalid JSON");
          },
        }),
    ],
    [
      "missing success acknowledgement",
      () =>
        Promise.resolve({ ok: true, json: async () => ({ message: "ok" }) }),
    ],
  ])("blocks retry after an uncertain %s", async (_label, response) => {
    const fetchMock = vi.fn(response);
    vi.stubGlobal("fetch", fetchMock);
    const { wrapper } = createHarness();
    const { result } = renderHook(() => useResubmitTravelApplication(42), {
      wrapper,
    });

    await expectMutationFailure(result, { id: 0 });

    expect(result.current.recovery.type).toBe("unknown-outcome");
    expect(result.current.isCommitBlocked).toBe(true);
    expect(result.current.isSuccess).toBe(false);
    expect(fetchMock).toHaveBeenCalledOnce();
  });

  it("uses a GET-only status check to reenable retry for a disapproved application", async () => {
    const draft = Object.freeze({
      id: 0,
      amendment: Object.freeze({ purpose: "Unsaved edit" }),
    });
    const fetchMock = vi
      .fn()
      .mockResolvedValueOnce(errorResponse(500))
      .mockResolvedValueOnce(applicationResponse(true));
    vi.stubGlobal("fetch", fetchMock);
    const { wrapper } = createHarness();
    const { result } = renderHook(() => useResubmitTravelApplication(42), {
      wrapper,
    });
    await expectMutationFailure(result, draft);

    await act(() => result.current.checkApplicationStatus());
    await waitFor(() =>
      expect(result.current.recovery.type).toBe("retryable-status"),
    );

    expect(result.current.isCommitBlocked).toBe(false);
    expect(result.current.isSuccess).toBe(false);
    expect(draft.amendment.purpose).toBe("Unsaved edit");
    expect(fetchMock).toHaveBeenCalledTimes(2);
    expect(fetchMock).toHaveBeenNthCalledWith(
      2,
      "/api/v1/travel/applications/42",
      expect.objectContaining({ method: "GET" }),
    );
    expect(
      fetchMock.mock.calls.some(([url]) =>
        url.endsWith("/travel/application/edit/42"),
      ),
    ).toBe(false);
  });

  it("keeps commit blocked without inferring success when status changed", async () => {
    const fetchMock = vi
      .fn()
      .mockResolvedValueOnce(errorResponse(500))
      .mockResolvedValueOnce(applicationResponse(false));
    vi.stubGlobal("fetch", fetchMock);
    const { wrapper } = createHarness();
    const { result } = renderHook(() => useResubmitTravelApplication(42), {
      wrapper,
    });
    await expectMutationFailure(result, { id: 0 });

    await act(() => result.current.checkApplicationStatus());
    await waitFor(() =>
      expect(result.current.recovery.type).toBe("status-changed"),
    );

    expect(result.current.isCommitBlocked).toBe(true);
    expect(result.current.isSuccess).toBe(false);
    expect(fetchMock).toHaveBeenCalledTimes(2);
  });

  it("allows another GET status check after a failed check without repeating POST", async () => {
    const fetchMock = vi
      .fn()
      .mockRejectedValueOnce(new TypeError("POST outcome unknown"))
      .mockResolvedValueOnce(errorResponse(500))
      .mockResolvedValueOnce(applicationResponse(true));
    vi.stubGlobal("fetch", fetchMock);
    const { wrapper } = createHarness();
    const { result } = renderHook(() => useResubmitTravelApplication(42), {
      wrapper,
    });
    await expectMutationFailure(result, { id: 0 });

    await act(() => result.current.checkApplicationStatus());
    await waitFor(() =>
      expect(result.current.recovery.type).toBe("status-check-failed"),
    );
    expect(result.current.isCommitBlocked).toBe(true);

    await act(() => result.current.checkApplicationStatus());
    await waitFor(() =>
      expect(result.current.recovery.type).toBe("retryable-status"),
    );

    expect(result.current.isCommitBlocked).toBe(false);
    expect(fetchMock).toHaveBeenCalledTimes(3);
    expect(
      fetchMock.mock.calls.filter(([, options]) => options?.method === "POST"),
    ).toHaveLength(1);
    expect(
      fetchMock.mock.calls.filter(([, options]) => options?.method === "GET"),
    ).toHaveLength(2);
  });
});
