import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { render, screen } from "@testing-library/react";
import { afterEach, describe, expect, it, vi } from "vitest";
import TravelAppReviewForm from "./TravelAppReviewForm";

afterEach(() => vi.unstubAllGlobals());

function action(id, kind, dateTime, user, notes) {
  return {
    id,
    dateTime,
    user: { lastName: user },
    notes,
    isApproval: kind === "approval",
    isDisapproval: kind === "disapproval",
  };
}

const disapprovalOne = action(
  101,
  "disapproval",
  "2026-04-01T10:00:00",
  "Reviewer One",
  "First reason retained",
);
const approval = action(
  102,
  "approval",
  "2026-04-10T10:00:00",
  "Reviewer Two",
  "Approval note retained",
);
const disapprovalTwo = action(
  103,
  "disapproval",
  "2026-04-07T10:00:00",
  "Reviewer Three",
  "Second reason retained",
);

function review(actions, status = "DISAPPROVED") {
  return {
    id: 99,
    actions,
    travelApplication: {
      id: 42,
      traveler: { fullName: "Traveler One" },
      activeAmendment: {
        purposeOfTravel: { summary: "Conference" },
        route: { outboundLegs: [], destinations: [] },
        attachments: [],
      },
      status: { name: status, isPending: status === "PENDING" },
    },
  };
}

function renderReview(appReview) {
  vi.stubGlobal(
    "fetch",
    vi.fn(async () => ({ ok: true, json: async () => ({ result: [] }) })),
  );
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  render(
    <QueryClientProvider client={queryClient}>
      <TravelAppReviewForm appReview={appReview} />
    </QueryClientProvider>,
  );
}

function actionHistoryText() {
  return screen.getByText("Previous Actions").parentElement.textContent;
}

describe("TravelAppReviewForm inferred action history", () => {
  it("shows no actions or invented marker for a pending application with no review actions", () => {
    renderReview(review([], "PENDING"));

    expect(screen.getByText("No Actions")).toBeVisible();
    expect(screen.queryByText("Resubmitted by user")).not.toBeInTheDocument();
  });

  it("shows a current disapproval without inferring a resubmission", () => {
    renderReview(review([disapprovalOne]));

    expect(actionHistoryText()).toMatch(
      /Reviewer One.*Disapproved.*First reason retained/,
    );
    expect(screen.queryByText("Resubmitted by user")).not.toBeInTheDocument();
    expect(screen.getByText("4/01/26")).toBeVisible();
  });

  it("inserts one marker after the final disapproval when the application is pending", () => {
    renderReview(review([disapprovalOne], "PENDING"));

    expect(screen.getAllByText("Resubmitted by user")).toHaveLength(1);
    expect(actionHistoryText()).toMatch(
      /4\/01\/26.*Reviewer One.*Disapproved.*First reason retained.*Resubmitted by user/,
    );
  });

  it("places the inferred marker between disapproval and later approval", () => {
    renderReview(review([disapprovalOne, approval], "APPROVED"));

    expect(screen.getAllByText("Resubmitted by user")).toHaveLength(1);
    expect(actionHistoryText()).toMatch(
      /4\/01\/26.*Reviewer One.*Disapproved.*First reason retained.*Resubmitted by user.*4\/10\/26.*Reviewer Two.*Approved.*Approval note retained/,
    );
  });

  it("marks an earlier disapproval but not the current later disapproval", () => {
    renderReview(review([disapprovalOne, disapprovalTwo]));

    expect(screen.getAllByText("Resubmitted by user")).toHaveLength(1);
    expect(actionHistoryText()).toMatch(
      /First reason retained.*Resubmitted by user.*4\/07\/26.*Reviewer Three.*Disapproved.*Second reason retained/,
    );
  });

  it("retains action chronology, users, dates, and notes through repeated reject/resubmit cycles", () => {
    renderReview(
      review([disapprovalOne, disapprovalTwo, approval], "APPROVED"),
    );

    expect(screen.getAllByText("Resubmitted by user")).toHaveLength(2);
    expect(actionHistoryText()).toMatch(
      /4\/01\/26.*Reviewer One.*Disapproved.*First reason retained.*Resubmitted by user.*4\/07\/26.*Reviewer Three.*Disapproved.*Second reason retained.*Resubmitted by user.*4\/10\/26.*Reviewer Two.*Approved.*Approval note retained/,
    );
  });

  it("does not invent a marker for an approved app without prior disapproval", () => {
    renderReview(review([approval], "APPROVED"));

    expect(actionHistoryText()).toMatch(
      /Reviewer Two.*Approved.*Approval note retained/,
    );
    expect(screen.queryByText("Resubmitted by user")).not.toBeInTheDocument();
  });

  it("documents the known inference limit for a canceled final application", () => {
    renderReview(review([disapprovalOne], "CANCELED"));

    expect(actionHistoryText()).toMatch(
      /Reviewer One.*Disapproved.*First reason retained/,
    );
    expect(screen.queryByText("Resubmitted by user")).not.toBeInTheDocument();
  });

  it("renders no fabricated date, user, or note on an inferred marker", () => {
    renderReview(review([disapprovalOne], "PENDING"));

    const marker = screen.getByText("Resubmitted by user");
    expect(marker.textContent).toBe("Resubmitted by user");
    expect(marker.parentElement.textContent).toBe("Resubmitted by user");
    expect(screen.getAllByText("Reviewer One")).toHaveLength(1);
    expect(screen.getAllByText("First reason retained")).toHaveLength(1);
  });
});
