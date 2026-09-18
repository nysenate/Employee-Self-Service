import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import TravelApplicationWorkflow from "./TravelApplicationWorkflow";

const presentation = {
  finalActionLabel: "Finish editing",
  confirmationTitle: "Finish this application?",
  confirmationBody: "Confirm the displayed changes.",
  confirmationActionLabel: "Confirm changes",
  pendingText: "Finishing…",
};

function routeAddress(name, county) {
  return {
    formattedAddressWithCounty: name,
    zip5: "12207",
    county,
    state: "NY",
    country: "United States",
  };
}

function completeDraft() {
  const albany = routeAddress("Albany, NY 12207", "Albany");
  const buffalo = routeAddress("Buffalo, NY 14202", "Erie");
  return {
    traveler: {},
    amendment: {
      purposeOfTravel: {
        eventType: { name: "Forum", displayName: "Forum" },
      },
      route: {
        outboundLegs: [
          {
            from: { address: albany },
            to: { address: buffalo },
            travelDate: "08/10/2026",
            methodOfTravelDisplayName: "Train",
          },
        ],
        returnLegs: [
          {
            from: { address: buffalo },
            to: { address: albany },
            travelDate: "08/11/2026",
            methodOfTravelDisplayName: "Train",
          },
        ],
      },
      allowances: {
        tolls: 0,
        parking: 0,
        alternateTransportation: 0,
        trainAndPlane: 0,
        registration: 0,
      },
      mealPerDiems: { isAllowedMeals: false, allMealPerDiems: [] },
      lodgingPerDiems: { allLodgingPerDiems: [] },
      mileagePerDiems: { allPerDiems: [] },
    },
  };
}

function renderWorkflow({
  commit,
  application = null,
  onCancel = null,
  renderCompletion = () => null,
}) {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>
        <TravelApplicationWorkflow
          initialDraft={completeDraft()}
          application={application}
          saveDraft={null}
          commit={commit}
          presentation={presentation}
          onCancel={onCancel}
          renderCompletion={renderCompletion}
        />
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

async function advanceToReview() {
  await screen.findByRole("option", { name: "Forum" });
  expect(screen.queryByRole("button", { name: "Save" })).not.toBeInTheDocument();
  fireEvent.click(screen.getByRole("button", { name: "Next" }));
  fireEvent.click(screen.getByRole("button", { name: "Next" }));
  await waitFor(() =>
    expect(screen.getByRole("button", { name: "Return" })).toHaveAttribute(
      "aria-current",
      "step",
    ),
  );
  expect(screen.queryByRole("button", { name: "Save" })).not.toBeInTheDocument();
  fireEvent.click(screen.getByRole("button", { name: "Next" }));
  await waitFor(() =>
    expect(screen.getByRole("button", { name: "Expenses" })).toHaveAttribute(
      "aria-current",
      "step",
    ),
  );
  expect(screen.queryByRole("button", { name: "Save" })).not.toBeInTheDocument();
  fireEvent.click(screen.getByRole("button", { name: "Next" }));
  await screen.findByRole("heading", { name: "Review" });
  expect(screen.queryByRole("button", { name: "Save" })).not.toBeInTheDocument();
}

describe("TravelApplicationWorkflow ports", () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    vi.stubGlobal(
      "fetch",
      vi.fn(async (url) => {
        if (String(url).includes("/config"))
          return response({ config: { googleApiKey: "test-key" } });
        if (String(url).endsWith("/travel/event-types"))
          return response([{ name: "Forum", displayName: "Forum" }]);
        if (String(url).endsWith("/travel/mode-of-transportation"))
          return response([
            { methodOfTravel: "TRAIN", displayName: "Train" },
          ]);
        return response([]);
      }),
    );
  });

  it("omits draft saving and commits the displayed draft exactly once", async () => {
    const execute = vi.fn().mockResolvedValue({ success: true });
    const onCancel = vi.fn();
    renderWorkflow({
      commit: {
        execute,
        isPending: false,
        isDisabled: false,
        renderError: vi.fn(),
      },
      renderCompletion: (result) => (
        <p>Completed {String(result.success)}</p>
      ),
      onCancel,
      application: {
        id: 82,
        submittedDateTime: "2024-07-04T09:30:00",
      },
    });

    await advanceToReview();
    expect(screen.getByText("7/04/24")).toBeVisible();
    fireEvent.click(screen.getByRole("button", { name: "Finish editing" }));
    expect(screen.getByRole("dialog")).toHaveAccessibleName(
      "Finish this application?",
    );
    expect(screen.getByText("Confirm the displayed changes.")).toBeVisible();
    const confirm = screen.getByRole("button", { name: "Confirm changes" });
    fireEvent.click(confirm);
    fireEvent.click(confirm);

    expect(await screen.findByText("Completed true")).toBeVisible();
    expect(
      screen.getByRole("button", { name: "Cancel edits" }),
    ).toBeDisabled();
    expect(onCancel).not.toHaveBeenCalled();
    expect(execute).toHaveBeenCalledOnce();
    expect(execute).toHaveBeenCalledWith(completeDraft());
    expect(
      fetch.mock.calls.some(
        ([url, options]) =>
          String(url).endsWith("/travel/drafts") && options?.method === "POST",
      ),
    ).toBe(false);
  });

  it("renders the commit port error and never renders completion on rejection", async () => {
    const error = new Error("commit failed");
    const renderError = vi.fn((received) => (
      <p role="alert">Rejected: {received.message}</p>
    ));
    renderWorkflow({
      commit: {
        execute: vi.fn().mockRejectedValue(error),
        isPending: false,
        isDisabled: false,
        renderError,
      },
      renderCompletion: () => <p>Unexpected completion</p>,
    });

    await advanceToReview();
    fireEvent.click(screen.getByRole("button", { name: "Finish editing" }));
    fireEvent.click(screen.getByRole("button", { name: "Confirm changes" }));

    expect(await screen.findByRole("alert")).toHaveTextContent(
      "Rejected: commit failed",
    );
    expect(renderError).toHaveBeenCalledWith(error);
    expect(screen.queryByText("Unexpected completion")).not.toBeInTheDocument();
  });

  it("keeps edits or explicitly discards without making a request", async () => {
    const onCancel = vi.fn();
    renderWorkflow({
      commit: {
        execute: vi.fn(),
        isPending: false,
        isDisabled: false,
        renderError: vi.fn(),
      },
      onCancel,
    });

    await screen.findByRole("option", { name: "Forum" });
    fireEvent.click(screen.getByRole("button", { name: "Cancel edits" }));
    expect(screen.getByRole("dialog")).toHaveAccessibleName(
      "Cancel editing this travel application?",
    );
    fireEvent.click(screen.getByRole("button", { name: "Keep editing" }));
    expect(onCancel).not.toHaveBeenCalled();

    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    expect(screen.getByRole("button", { name: "Outbound" })).toHaveAttribute(
      "aria-current",
      "step",
    );

    fireEvent.change(screen.getByLabelText("Travel date"), {
      target: { value: "2026-08-12" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Cancel edits" }));
    expect(screen.getByRole("dialog")).toHaveAccessibleName(
      "Cancel editing this travel application?",
    );
    expect(onCancel).not.toHaveBeenCalled();

    fireEvent.click(screen.getByRole("button", { name: "Keep editing" }));
    expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
    expect(screen.getByLabelText("Travel date")).toHaveValue("2026-08-12");
    expect(onCancel).not.toHaveBeenCalled();

    fireEvent.click(screen.getByRole("button", { name: "Cancel edits" }));
    fireEvent.click(screen.getByRole("button", { name: "Discard edits" }));
    expect(onCancel).toHaveBeenCalledOnce();
    expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
    expect(
      fetch.mock.calls.some(([, options]) =>
        ["POST", "PATCH"].includes(options?.method),
      ),
    ).toBe(false);
  });
});

function response(result) {
  return {
    ok: true,
    status: 200,
    statusText: "",
    json: async () => ({ result }),
  };
}
