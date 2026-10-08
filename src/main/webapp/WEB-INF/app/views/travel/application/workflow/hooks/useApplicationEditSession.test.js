import React from "react";
import {
  focusManager,
  onlineManager,
  QueryClient,
  QueryClientProvider,
} from "@tanstack/react-query";
import { act, renderHook, waitFor } from "@testing-library/react";
import { afterEach, describe, expect, it, vi } from "vitest";
import { travelQueryKeys } from "app/views/travel/shared/hooks/travelQueryKeys";
import { useApplicationEditSession } from "./useApplicationEditSession";

afterEach(() => {
  focusManager.setFocused(undefined);
  onlineManager.setOnline(true);
  vi.unstubAllGlobals();
});

function jsonResponse(result) {
  return {
    ok: true,
    json: async () => ({ result }),
  };
}

function createHarness(queryClient = new QueryClient()) {
  const wrapper = ({ children }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
  return { queryClient, wrapper };
}

describe("useApplicationEditSession", () => {
  it("loads application metadata and the authorized edit draft unchanged", async () => {
    const application = { id: 42, status: { isDisapproved: true } };
    const draft = { id: 0, amendment: { purpose: "Conference" } };
    const fetchMock = vi
      .fn()
      .mockResolvedValueOnce(jsonResponse(application))
      .mockResolvedValueOnce(jsonResponse(draft));
    vi.stubGlobal("fetch", fetchMock);
    const { wrapper } = createHarness();

    const { result } = renderHook(
      () => useApplicationEditSession({ appId: "42", operation: "resubmit" }),
      { wrapper },
    );

    await waitFor(() => expect(result.current.isSuccess).toBe(true));

    expect(fetchMock).toHaveBeenCalledTimes(2);
    expect(fetchMock).toHaveBeenNthCalledWith(
      1,
      "/api/v1/travel/applications/42",
      expect.objectContaining({ method: "GET" }),
    );
    expect(fetchMock).toHaveBeenNthCalledWith(
      2,
      "/api/v1/travel/application/edit/42",
      expect.objectContaining({ method: "GET" }),
    );
    expect(result.current.data).toEqual({ application, draft });
    expect(result.current.data.application).toBe(application);
    expect(result.current.data.draft).toBe(draft);
  });

  it.each(["", "0", "1abc", "9007199254740992"])(
    "does not request a malformed application ID: %s",
    (appId) => {
      const fetchMock = vi.fn();
      vi.stubGlobal("fetch", fetchMock);
      const { wrapper } = createHarness();

      const { result } = renderHook(
        () => useApplicationEditSession({ appId, operation: "resubmit" }),
        { wrapper },
      );

      expect(fetchMock).not.toHaveBeenCalled();
      expect(result.current.data).toBeUndefined();
      expect(result.current.isSuccess).toBe(false);
    },
  );

  it("keeps an initializer denial as an error without creating a draft", async () => {
    const deniedResponse = {
      ok: false,
      status: 401,
      statusText: "Unauthorized",
      json: async () => ({ message: "Denied" }),
    };
    const fetchMock = vi
      .fn()
      .mockResolvedValueOnce(jsonResponse({ id: 42 }))
      .mockResolvedValueOnce(deniedResponse);
    vi.stubGlobal("fetch", fetchMock);
    const { wrapper } = createHarness();

    const { result } = renderHook(
      () => useApplicationEditSession({ appId: 42, operation: "resubmit" }),
      { wrapper },
    );

    await waitFor(() => expect(result.current.isError).toBe(true));
    expect(result.current.data).toBeUndefined();
    expect(fetchMock).toHaveBeenCalledTimes(2);
    expect(fetchMock).not.toHaveBeenCalledWith(
      "/api/v1/travel/drafts",
      expect.anything(),
    );
  });

  it("uses distinct query state for distinct application IDs", async () => {
    const fetchMock = vi.fn((url) => {
      const id = Number(url.match(/\/(\d+)$/)[1]);
      return Promise.resolve(
        jsonResponse(url.includes("/edit/") ? { id: 0, appId: id } : { id }),
      );
    });
    vi.stubGlobal("fetch", fetchMock);
    const { queryClient, wrapper } = createHarness();
    const first = renderHook(
      () => useApplicationEditSession({ appId: 41, operation: "resubmit" }),
      { wrapper },
    );
    const second = renderHook(
      () => useApplicationEditSession({ appId: 42, operation: "resubmit" }),
      { wrapper },
    );

    await waitFor(() => expect(first.result.current.isSuccess).toBe(true));
    await waitFor(() => expect(second.result.current.isSuccess).toBe(true));

    expect(first.result.current.data.application.id).toBe(41);
    expect(second.result.current.data.application.id).toBe(42);
    expect(
      queryClient.getQueryData(travelQueryKeys.editorSession(41, "resubmit"))
        .application.id,
    ).toBe(41);
    expect(
      queryClient.getQueryData(travelQueryKeys.editorSession(42, "resubmit"))
        .application.id,
    ).toBe(42);
  });

  it("fetches a fresh snapshot when a canceled session is reopened", async () => {
    let version = 1;
    const fetchMock = vi.fn((url) =>
      Promise.resolve(
        jsonResponse(
          url.includes("/edit/")
            ? { id: 0, amendment: { purpose: `Draft ${version}` } }
            : { id: 42, version },
        ),
      ),
    );
    vi.stubGlobal("fetch", fetchMock);
    const { queryClient, wrapper } = createHarness();
    const first = renderHook(
      () => useApplicationEditSession({ appId: 42, operation: "resubmit" }),
      { wrapper },
    );
    await waitFor(() => expect(first.result.current.isSuccess).toBe(true));
    expect(first.result.current.data.application.version).toBe(1);

    first.unmount();
    await waitFor(() =>
      expect(
        queryClient.getQueryState(
          travelQueryKeys.editorSession(42, "resubmit"),
        ),
      ).toBeUndefined(),
    );
    version = 2;

    const reopened = renderHook(
      () => useApplicationEditSession({ appId: 42, operation: "resubmit" }),
      { wrapper },
    );
    await waitFor(() => expect(reopened.result.current.isSuccess).toBe(true));

    expect(reopened.result.current.data.application.version).toBe(2);
    expect(fetchMock).toHaveBeenCalledTimes(4);
  });

  it("waits for this mount's fetch instead of exposing cached session data", async () => {
    let resolveApplication;
    const freshApplication = new Promise((resolve) => {
      resolveApplication = resolve;
    });
    const fetchMock = vi
      .fn()
      .mockReturnValueOnce(freshApplication)
      .mockResolvedValueOnce(jsonResponse({ id: 0, fresh: true }));
    vi.stubGlobal("fetch", fetchMock);
    const { queryClient, wrapper } = createHarness();
    queryClient.setQueryData(travelQueryKeys.editorSession(42, "resubmit"), {
      application: { id: 42, cached: true },
      draft: { id: 0, cached: true },
    });

    const { result } = renderHook(
      () => useApplicationEditSession({ appId: 42, operation: "resubmit" }),
      { wrapper },
    );

    expect(result.current.isSuccess).toBe(false);
    expect(result.current.data).toBeUndefined();

    resolveApplication(jsonResponse({ id: 42, fresh: true }));
    await waitFor(() => expect(result.current.isSuccess).toBe(true));

    expect(result.current.data).toEqual({
      application: { id: 42, fresh: true },
      draft: { id: 0, fresh: true },
    });
  });

  it("does not refetch on focus or reconnect and retains its first snapshot", async () => {
    let version = 1;
    const fetchMock = vi.fn((url) =>
      Promise.resolve(
        jsonResponse(
          url.includes("/edit/")
            ? { id: 0, amendment: { purpose: `Draft ${version}` } }
            : { id: 42, version },
        ),
      ),
    );
    vi.stubGlobal("fetch", fetchMock);
    const { wrapper } = createHarness();
    const { result } = renderHook(
      () => useApplicationEditSession({ appId: 42, operation: "resubmit" }),
      { wrapper },
    );
    await waitFor(() => expect(result.current.isSuccess).toBe(true));

    focusManager.setFocused(false);
    onlineManager.setOnline(false);
    focusManager.setFocused(true);
    onlineManager.setOnline(true);
    await act(async () => Promise.resolve());
    expect(fetchMock).toHaveBeenCalledTimes(2);

    version = 2;
    await act(() => result.current.refetch());

    expect(fetchMock).toHaveBeenCalledTimes(4);
    expect(result.current.data.application.version).toBe(1);
    expect(result.current.data.draft.amendment.purpose).toBe("Draft 1");
  });
});
