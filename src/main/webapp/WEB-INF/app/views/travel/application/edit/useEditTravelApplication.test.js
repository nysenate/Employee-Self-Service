import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { act, renderHook } from "@testing-library/react";
import { afterEach, expect, it, vi } from "vitest";
import { useEditTravelApplication } from "./useEditTravelApplication";

afterEach(() => vi.unstubAllGlobals());
function harness() {
  const client = new QueryClient();
  const invalidate = vi.spyOn(client, "invalidateQueries");
  const wrapper = ({ children }) => (
    <QueryClientProvider client={client}>{children}</QueryClientProvider>
  );
  return {
    ...renderHook(() => useEditTravelApplication(42), { wrapper }),
    invalidate,
  };
}
it("posts to the edit endpoint and refreshes application and review caches", async () => {
  const fetchMock = vi
    .fn()
    .mockResolvedValue({ ok: true, json: async () => ({ success: true }) });
  vi.stubGlobal("fetch", fetchMock);
  const { result, invalidate } = harness();
  const draft = { amendment: { purposeOfTravel: "Corrected" } };
  await act(() => result.current.mutateAsync(draft));
  expect(fetchMock).toHaveBeenCalledOnce();
  expect(fetchMock).toHaveBeenCalledWith(
    "/api/v1/travel/application/edit/42",
    expect.objectContaining({ method: "POST", body: JSON.stringify(draft) }),
  );
  expect(invalidate).toHaveBeenCalledWith({
    queryKey: ["travel", "applications"],
  });
  expect(invalidate).toHaveBeenCalledWith({ queryKey: ["travel", "review"] });
});
it.each([false, undefined])(
  "does not treat an unacknowledged save as success or retry it (%s)",
  async (success) => {
    const fetchMock = vi
      .fn()
      .mockResolvedValue({ ok: true, json: async () => ({ success }) });
    vi.stubGlobal("fetch", fetchMock);
    const { result, invalidate } = harness();
    await act(async () => {
      await expect(result.current.mutateAsync({})).rejects.toThrow();
    });
    expect(invalidate).not.toHaveBeenCalled();
    expect(fetchMock).toHaveBeenCalledOnce();
  },
);
