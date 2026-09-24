import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { MemoryRouter, useLocation } from "react-router-dom";
import { fireEvent, render, screen } from "@testing-library/react";
import { afterEach, expect, it, vi } from "vitest";
import ReviewerActionModal from "../../reviewer/queue/ReviewerActionModal";
import TravelAppReviewModal from "../../reviewer/history/TravelAppReviewModal";
import { normalizeReviewReturnTo } from "./editRoutes";

vi.mock("../../shared/components/TravelAppReviewForm", () => ({
  default: () => <p>Application details</p>,
}));
vi.mock("../../reviewer/queue/ApproveConfirmDialog", () => ({
  default: () => null,
}));
vi.mock("../../reviewer/queue/DisapproveConfirmDialog", () => ({
  default: () => null,
}));
afterEach(() => vi.unstubAllGlobals());
function Location() {
  const location = useLocation();
  return (
    <p data-testid="location">
      {location.pathname} {location.state?.returnTo}
    </p>
  );
}
function modal(kind, role) {
  vi.stubGlobal(
    "fetch",
    vi.fn(async (url) => ({
      ok: true,
      json: async () => ({
        result: url.endsWith("/roles")
          ? { allRoles: [{ name: role }] }
          : {
              appReviewId: 99,
              pendingReviewerRole: "TRAVEL_ADMIN",
              travelApplication: { id: 42 },
            },
      }),
    })),
  );
  render(
    <QueryClientProvider client={new QueryClient()}>
      <MemoryRouter initialEntries={[`/travel/manage/${kind}?offset=12`]}>
        <Location />
        {kind === "queue" ? (
          <ReviewerActionModal
            reviewSummary={{ appReviewId: 99 }}
            setIsOpen={vi.fn()}
          />
        ) : (
          <TravelAppReviewModal
            reviewSummary={{ appReviewId: 99 }}
            onOpenChange={vi.fn()}
          />
        )}
      </MemoryRouter>
    </QueryClientProvider>,
  );
}
it.each(["queue", "review-history"])(
  "opens editing from %s and retains the return location",
  async (kind) => {
    modal(kind, "TRAVEL_ADMIN");
    fireEvent.click(
      await screen.findByRole("button", { name: "Edit Application" }),
    );
    expect(screen.getByTestId("location")).toHaveTextContent(
      `/travel/applications/42/edit /travel/manage/${kind}?offset=12`,
    );
  },
);
it.each(["queue", "review-history"])(
  "hides editing from Secretary-only users in %s",
  async (kind) => {
    modal(kind, "SECRETARY_OF_THE_SENATE");
    await screen.findByText("Application details");
    expect(
      screen.queryByRole("button", { name: "Edit Application" }),
    ).not.toBeInTheDocument();
  },
);
it.each([
  "https://example.com",
  "//example.com",
  "/travel/applications",
  "/travel/manage/queue/other",
  "/travel/manage/queue?x=%bad",
])("rejects invalid return path %s", (value) => {
  expect(normalizeReviewReturnTo(value)).toBe("/travel/manage/queue");
});
