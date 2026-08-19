import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { renderHook, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { useCalculateTravelRoute } from "./useRouteMutations";

describe("route calculation mutation", () => {
  it("PATCHes the complete draft with only the ROUTE option", async () => {
    const calculated = { amendment: { route: { origin: {} } } };
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ result: calculated }),
    });
    vi.stubGlobal("fetch", fetchMock);
    const wrapper = ({ children }) => (
      <QueryClientProvider client={new QueryClient()}>
        {children}
      </QueryClientProvider>
    );
    const { result } = renderHook(() => useCalculateTravelRoute(), { wrapper });
    const draft = {
      amendment: { route: { outboundLegs: [], returnLegs: [] } },
    };
    result.current.mutate(draft);
    await waitFor(() => expect(result.current.isSuccess).toBe(true));

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/v1/travel/drafts",
      expect.objectContaining({
        method: "PATCH",
        body: JSON.stringify({ options: ["ROUTE"], draft }),
      }),
    );
    expect(result.current.data).toBe(calculated);
  });
});
