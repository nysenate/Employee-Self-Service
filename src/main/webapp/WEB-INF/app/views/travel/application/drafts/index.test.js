import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { fireEvent, render, screen } from "@testing-library/react";
import { MemoryRouter, Route, Routes, useParams } from "react-router-dom";
import { afterEach, describe, expect, it, vi } from "vitest";
import Drafts from "./index";

afterEach(() => vi.unstubAllGlobals());

describe("travel application drafts", () => {
  it("continues the selected draft at its application URL", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue({
        ok: true,
        json: async () => ({
          result: [
            {
              id: 42,
              traveler: { fullName: "Jamie Rivera" },
              amendment: {},
            },
          ],
        }),
      }),
    );
    const queryClient = new QueryClient({
      defaultOptions: { queries: { retry: false } },
    });

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={["/travel/applications/drafts"]}>
          <Routes>
            <Route path="/travel/applications/drafts" element={<Drafts />} />
            <Route
              path="/travel/applications/new/:draftId"
              element={<SelectedDraft />}
            />
          </Routes>
        </MemoryRouter>
      </QueryClientProvider>,
    );

    fireEvent.click(await screen.findByRole("button", { name: "Continue" }));

    expect(
      await screen.findByRole("heading", { name: "Continuing draft 42" }),
    ).toBeVisible();
  });
});

function SelectedDraft() {
  const { draftId } = useParams();
  return <h1>Continuing draft {draftId}</h1>;
}
