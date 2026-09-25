import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import TravelApplicationWorkflow from "./TravelApplicationWorkflow";
import { ADMIN_EDIT_STEPS } from "./workflowSteps";

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
  steps,
  initialDraft = completeDraft(),
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
          steps={steps}
          initialDraft={initialDraft}
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

async function advanceToReview(overrides = false) {
  await screen.findByRole("option", { name: "Forum" });
  expect(
    screen.queryByRole("button", { name: "Save" }),
  ).not.toBeInTheDocument();
  fireEvent.click(screen.getByRole("button", { name: "Next" }));
  fireEvent.click(screen.getByRole("button", { name: "Next" }));
  await waitFor(() =>
    expect(screen.getByRole("button", { name: "Return" })).toHaveAttribute(
      "aria-current",
      "step",
    ),
  );
  expect(
    screen.queryByRole("button", { name: "Save" }),
  ).not.toBeInTheDocument();
  fireEvent.click(screen.getByRole("button", { name: "Next" }));
  await waitFor(() =>
    expect(screen.getByRole("button", { name: "Expenses" })).toHaveAttribute(
      "aria-current",
      "step",
    ),
  );
  expect(
    screen.queryByRole("button", { name: "Save" }),
  ).not.toBeInTheDocument();
  fireEvent.click(screen.getByRole("button", { name: "Next" }));
  if (overrides) return;
  await screen.findByRole("heading", { name: "Review" });
  expect(
    screen.queryByRole("button", { name: "Save" }),
  ).not.toBeInTheDocument();
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
          return response([{ methodOfTravel: "TRAIN", displayName: "Train" }]);
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
      renderCompletion: (result) => <p>Completed {String(result.success)}</p>,
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
    expect(screen.getByRole("button", { name: "Cancel edits" })).toBeDisabled();
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

  it("validates admin overrides, retains failed calculations, and commits recalculated totals", async () => {
    const execute = vi.fn().mockRejectedValue(new Error("save failed"));
    const originalFetch = fetch.getMockImplementation();
    let failCalculation = true;
    fetch.mockImplementation(async (url, options) => {
      if (options?.method === "PATCH") {
        if (failCalculation) throw new Error("calculation failed");
        const { draft } = JSON.parse(options.body);
        return response({
          ...draft,
          amendment: {
            ...draft.amendment,
            lodgingAllowance: draft.amendment.lodgingPerDiems.overrideRate,
          },
        });
      }
      return originalFetch(url, options);
    });
    renderWorkflow({
      steps: ADMIN_EDIT_STEPS,
      commit: {
        execute,
        isPending: false,
        isDisabled: false,
        renderError: (error) => <p role="alert">{error.message}</p>,
      },
    });
    await advanceToReview(true);
    expect(
      screen.getByRole("heading", { name: "Expense Overrides" }),
    ).toBeVisible();
    fireEvent.click(screen.getByRole("checkbox", { name: "Override Lodging" }));
    let input = screen.getByLabelText("Lodging total ($)");
    fireEvent.change(input, { target: { value: "-1" } });
    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    expect(input).toHaveAttribute("aria-invalid", "true");
    fireEvent.click(screen.getByRole("button", { name: "Back" }));
    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    input = screen.getByLabelText("Lodging total ($)");
    expect(input).toHaveValue("-1");

    fireEvent.change(input, { target: { value: "250.25" } });
    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    expect(
      await screen.findByText(/override totals could not be calculated/),
    ).toBeVisible();
    expect(input).toHaveValue("250.25");
    failCalculation = false;
    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    await screen.findByRole("heading", { name: "Review" });
    fireEvent.click(screen.getByRole("button", { name: "Finish editing" }));
    fireEvent.click(screen.getByRole("button", { name: "Confirm changes" }));
    expect(await screen.findByText("save failed")).toBeVisible();
    expect(execute).toHaveBeenCalledWith(
      expect.objectContaining({
        amendment: expect.objectContaining({
          lodgingPerDiems: expect.objectContaining({
            overrideRate: 250.25,
            isOverridden: true,
          }),
          lodgingAllowance: 250.25,
        }),
      }),
    );
    fireEvent.click(screen.getByRole("button", { name: "Back" }));
    expect(screen.getByLabelText("Lodging total ($)")).toHaveValue("250.25");
    fireEvent.click(screen.getByRole("checkbox", { name: "Override Lodging" }));
    expect(screen.getByLabelText("Lodging total ($)")).toBeDisabled();
    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    await screen.findByRole("heading", { name: "Review" });
    const patches = fetch.mock.calls.filter(
      ([, options]) => options?.method === "PATCH",
    );
    expect(
      JSON.parse(patches.at(-1)[1].body).draft.amendment.lodgingPerDiems
        .overrideRate,
    ).toBe(0);
  });

  it("preserves an existing override for an unchanged route and resets it after route calculation", async () => {
    const draft = completeDraft();
    draft.amendment.lodgingPerDiems = {
      ...draft.amendment.lodgingPerDiems,
      isOverridden: true,
      overrideRate: 200,
    };
    const originalFetch = fetch.getMockImplementation();
    fetch.mockImplementation(async (url, options) => {
      if (options?.method === "PATCH") {
        const { draft: requested, options: patches } = JSON.parse(options.body);
        expect(patches).toEqual(["ROUTE"]);
        return response({
          ...requested,
          amendment: {
            ...requested.amendment,
            lodgingPerDiems: {
              allLodgingPerDiems: [],
              isOverridden: false,
              overrideRate: 0,
            },
          },
        });
      }
      return originalFetch(url, options);
    });
    renderWorkflow({
      initialDraft: draft,
      steps: ADMIN_EDIT_STEPS,
      commit: { execute: vi.fn(), isPending: false, renderError: vi.fn() },
    });
    await advanceToReview(true);
    expect(
      screen.getByRole("checkbox", { name: "Override Lodging" }),
    ).toBeChecked();
    expect(screen.getByLabelText("Lodging total ($)")).toHaveValue("200");
    expect(
      fetch.mock.calls.some(([, options]) => options?.method === "PATCH"),
    ).toBe(false);
    fireEvent.click(screen.getByRole("button", { name: "Return (completed)" }));
    expect(screen.getByLabelText("Return date")).toHaveAccessibleDescription(
      "Your trip begins Aug 10, 2026.",
    );
    expect(screen.getByLabelText("Return date")).toHaveAttribute(
      "min",
      "2026-08-10",
    );
    fireEvent.change(screen.getByLabelText("Return date"), {
      target: { value: "2026-08-12" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    await screen.findByRole("heading", { name: "Expenses" });
    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    expect(
      screen.getByRole("checkbox", { name: "Override Lodging" }),
    ).not.toBeChecked();
    expect(screen.getByLabelText("Lodging total ($)")).toHaveValue("0");
    expect(
      fetch.mock.calls.filter(([, options]) => options?.method === "PATCH"),
    ).toHaveLength(1);
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

    fireEvent.change(screen.getByLabelText("Outbound date"), {
      target: { value: "2026-08-12" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Cancel edits" }));
    expect(screen.getByRole("dialog")).toHaveAccessibleName(
      "Cancel editing this travel application?",
    );
    expect(onCancel).not.toHaveBeenCalled();

    fireEvent.click(screen.getByRole("button", { name: "Keep editing" }));
    expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
    expect(screen.getByLabelText("Outbound date")).toHaveValue("2026-08-12");
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
