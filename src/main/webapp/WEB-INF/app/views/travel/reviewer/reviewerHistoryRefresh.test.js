import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { act, render, screen } from "@testing-library/react";
import { afterEach, describe, expect, it, vi } from "vitest";
import { refreshTravelAfterResubmission } from "../application/resubmit/resubmissionCache";
import TravelAppReviewModal from "./history/TravelAppReviewModal";
import ReviewerActionModal from "./queue/ReviewerActionModal";

afterEach(() => vi.unstubAllGlobals());

function review(isResubmitted) {
  return {
    id: 99,
    pendingReviewerRole: "DEPARTMENT_HEAD",
    actions: [
      {
        id: 701,
        dateTime: "2026-04-01T10:00:00",
        user: { lastName: "Reviewer One" },
        notes: "Original disapproval reason",
        isDisapproval: true,
        isApproval: false,
      },
    ],
    travelApplication: {
      id: 42,
      submittedDateTime: "2026-03-01T09:00:00",
      traveler: { fullName: "Traveler One" },
      activeAmendment: {
        purposeOfTravel: {
          summary: isResubmitted
            ? "Updated conference purpose"
            : "Original conference purpose",
        },
        route: { outboundLegs: [], destinations: [] },
        attachments: [],
      },
      status: {
        name: isResubmitted ? "DEPARTMENT_HEAD" : "DISAPPROVED",
        isPending: isResubmitted,
      },
    },
  };
}

function renderModal(kind, queryClient) {
  const reviewSummary = { appReviewId: 99 };
  render(
    <QueryClientProvider client={queryClient}>
      {kind === "queue" ? (
        <ReviewerActionModal
          reviewSummary={reviewSummary}
          setIsOpen={vi.fn()}
        />
      ) : (
        <TravelAppReviewModal
          reviewSummary={reviewSummary}
          onOpenChange={vi.fn()}
        />
      )}
    </QueryClientProvider>,
  );
}

describe.each(["queue", "history"])("%s review modal", (kind) => {
  it("uses the shared action form and refreshes review detail after resubmission", async () => {
    let isResubmitted = false;
    const fetchMock = vi.fn((url) =>
      Promise.resolve({
        ok: true,
        status: 200,
        json: async () => ({
          result: url.endsWith("/travel/review/99")
            ? review(isResubmitted)
            : [],
        }),
      }),
    );
    vi.stubGlobal("fetch", fetchMock);
    const queryClient = new QueryClient({
      defaultOptions: { queries: { retry: false } },
    });
    renderModal(kind, queryClient);

    expect(
      await screen.findByText("Original conference purpose"),
    ).toBeVisible();
    expect(screen.getByText("Original disapproval reason")).toBeVisible();
    expect(screen.queryByText("Resubmitted by user")).not.toBeInTheDocument();

    isResubmitted = true;
    await act(async () => refreshTravelAfterResubmission(queryClient));

    expect(await screen.findByText("Updated conference purpose")).toBeVisible();
    expect(screen.getAllByText("Resubmitted by user")).toHaveLength(1);
    expect(screen.getByText("Original disapproval reason")).toBeVisible();
    expect(screen.getByText("Reviewer One")).toBeVisible();
    expect(screen.getByText("4/01/26")).toBeVisible();
    expect(
      fetchMock.mock.calls.filter(([url]) => url.endsWith("/travel/review/99")),
    ).toHaveLength(2);
  });
});
