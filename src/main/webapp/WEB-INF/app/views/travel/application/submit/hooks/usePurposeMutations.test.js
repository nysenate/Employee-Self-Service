import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { act, renderHook } from "@testing-library/react";
import { afterEach, describe, expect, it, vi } from "vitest";
import { travelQueryKeys } from "app/views/travel/shared/hooks/travelQueryKeys";
import { useSaveTravelDraft } from "./usePurposeMutations";

afterEach(() => vi.unstubAllGlobals());

describe("useSaveTravelDraft", () => {
  it("caches the saved draft and invalidates the drafts list", async () => {
    const savedDraft = { id: 42, amendment: { totalAllowance: 125 } };
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue({
        ok: true,
        json: async () => ({ result: savedDraft }),
      }),
    );
    const queryClient = new QueryClient();
    queryClient.setQueryData(travelQueryKeys.drafts(), { result: [] });
    const wrapper = ({ children }) => (
      <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
    );
    const { result } = renderHook(() => useSaveTravelDraft(), { wrapper });

    await act(() => result.current.mutateAsync(savedDraft));

    expect(queryClient.getQueryData(travelQueryKeys.draft(42))).toEqual(
      savedDraft,
    );
    expect(
      queryClient.getQueryState(travelQueryKeys.drafts()).isInvalidated,
    ).toBe(true);
  });
});
