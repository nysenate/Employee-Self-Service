import NotificationProvider from "app/components/NotificationProvider";
import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { fireEvent, render, screen } from "@testing-library/react";
import { MemoryRouter, Route, Routes, useParams } from "react-router-dom";
import { afterEach, describe, expect, it, vi } from "vitest";
import Drafts from "./index";

afterEach(() => vi.unstubAllGlobals());

describe("travel application drafts", () => {
  it.each([true, false])(
    "reports deletion only when confirmed (success=%s), including the last draft",
    async (succeeds) => {
      let deleted = false;
      vi.stubGlobal(
        "fetch",
        vi.fn(async (_url, options = {}) => {
          if (options.method === "DELETE") {
            deleted = succeeds;
            return {
              ok: succeeds,
              status: succeeds ? 200 : 500,
              json: async () => ({ success: succeeds }),
            };
          }
          return {
            ok: true,
            json: async () => ({
              result: deleted
                ? []
                : [
                    {
                      id: 42,
                      traveler: { fullName: "Jamie Rivera" },
                      amendment: {},
                    },
                  ],
            }),
          };
        }),
      );
      render(
        <NotificationProvider>
          <QueryClientProvider
            client={
              new QueryClient({ defaultOptions: { queries: { retry: false } } })
            }
          >
            <MemoryRouter>
              <Drafts />
            </MemoryRouter>
          </QueryClientProvider>
        </NotificationProvider>,
      );
      fireEvent.click(
        await screen.findByRole("button", {
          name: "Delete Jamie Rivera's travel draft",
        }),
      );
      fireEvent.click(screen.getByRole("button", { name: "Delete draft" }));
      if (succeeds) {
        expect(await screen.findByText("No saved drafts")).toBeVisible();
        expect(screen.getByRole("status")).toHaveTextContent("Draft deleted");
        expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
      } else {
        expect(await screen.findByRole("alert")).toHaveTextContent(
          "couldn't delete",
        );
        expect(screen.getByRole("dialog")).toBeVisible();
        expect(screen.queryByText("Draft deleted")).not.toBeInTheDocument();
      }
    },
  );

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
      <NotificationProvider>
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
        </QueryClientProvider>
      </NotificationProvider>,
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
