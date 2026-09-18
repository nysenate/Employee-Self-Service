import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { renderHook, waitFor } from "@testing-library/react";
import { afterEach, describe, expect, it, vi } from "vitest";
import { travelQueryKeys } from "./travelQueryKeys";
import { useTravelApp } from "./useTravelApp";

afterEach(() => vi.unstubAllGlobals());

function createHarness() {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  const wrapper = ({ children }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
  return { queryClient, wrapper };
}

describe("useTravelApp", () => {
  it("loads application detail with the numeric shared query key", async () => {
    const application = { id: 42, detail: "complete" };
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ result: application }),
    });
    vi.stubGlobal("fetch", fetchMock);
    const { queryClient, wrapper } = createHarness();

    const { result } = renderHook(() => useTravelApp(42), { wrapper });
    await waitFor(() => expect(result.current.isSuccess).toBe(true));

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/v1/travel/applications/42",
      expect.objectContaining({ method: "GET" }),
    );
    expect(queryClient.getQueryData(travelQueryKeys.application(42))).toEqual({
      result: application,
    });
  });

  it("returns an error to callers that opt out of error-boundary throwing", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue({
        ok: false,
        status: 403,
        statusText: "Forbidden",
        json: async () => ({ message: "Denied" }),
      }),
    );
    const { wrapper } = createHarness();

    const { result } = renderHook(
      () => useTravelApp(42, { throwOnError: false }),
      { wrapper },
    );
    await waitFor(() => expect(result.current.isError).toBe(true));

    expect(result.current.data).toBeUndefined();
    expect(result.current.error.response.status).toBe(403);
  });

  it("does not request an application when no validated ID is supplied", () => {
    const fetchMock = vi.fn();
    vi.stubGlobal("fetch", fetchMock);
    const { wrapper } = createHarness();

    const { result } = renderHook(
      () => useTravelApp(null, { throwOnError: false }),
      { wrapper },
    );

    expect(result.current.isPending).toBe(true);
    expect(fetchMock).not.toHaveBeenCalled();
  });
});
