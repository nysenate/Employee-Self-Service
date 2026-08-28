import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { Link, MemoryRouter, Route, Routes } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import NewTravelApplication from "./NewTravelApplication";

function renderWorkflow(draft = { traveler: {} }) {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={["/travel/applications/new"]}>
        <Routes>
          <Route
            path="/travel/applications/new"
            element={<NewTravelApplication draft={draft} />}
          />
          <Route
            path="/travel/applications"
            element={<h1>Travel history</h1>}
          />
          <Route path="/travel" element={<h1>Travel home</h1>} />
          <Route path="/logout" element={<h1>Logging out</h1>} />
        </Routes>
        <Link to="/travel/applications">Travel history link</Link>
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

const routeAddress = (name, county) => ({
  formattedAddressWithCounty: name,
  zip5: "12207",
  county,
  state: "NY",
  country: "United States",
});

function returnReadyDraft() {
  const albany = routeAddress("Albany, NY 12207", "Albany");
  const buffalo = routeAddress("Buffalo, NY 14202", "");
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
            travelDate: "08/18/2026",
            methodOfTravelDisplayName: "Train",
          },
        ],
      },
    },
  };
}

function calculatedExpenseDraft(draft) {
  return {
    ...draft,
    amendment: {
      ...draft.amendment,
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

async function advanceToReturn() {
  await screen.findByRole("option", { name: "Forum" });
  fireEvent.click(screen.getByRole("button", { name: "Next" }));
  fireEvent.click(screen.getByRole("button", { name: "Next" }));
  await waitFor(() =>
    expect(screen.getByRole("button", { name: "Return" })).toHaveAttribute(
      "aria-current",
      "step",
    ),
  );
}

describe("new travel application workflow shell", () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    vi.stubGlobal(
      "fetch",
      vi.fn().mockImplementation(async (url) => ({
        ok: true,
        json: async () =>
          String(url).includes("/config")
            ? { result: { config: { googleApiKey: "test-key" } } }
            : { result: [{ name: "Forum", displayName: "Forum" }] },
      })),
    );
  });

  it("marks progress, disables future steps, and retains data through Back", async () => {
    renderWorkflow();

    const purpose = screen.getByRole("button", { name: "Purpose" });
    expect(purpose).toHaveAttribute("aria-current", "step");
    expect(screen.getByRole("button", { name: "Outbound" })).toBeDisabled();

    await screen.findByRole("option", { name: "Forum" });
    fireEvent.change(await screen.findByLabelText("Purpose"), {
      target: { value: "Forum" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Next" }));

    expect(
      screen.getByRole("button", { name: /Purpose.*completed/ }),
    ).toBeEnabled();
    expect(screen.getByRole("button", { name: "Outbound" })).toHaveAttribute(
      "aria-current",
      "step",
    );
    expect(
      screen.queryByRole("button", { name: "Save" }),
    ).not.toBeInTheDocument();

    fireEvent.click(screen.getByRole("button", { name: "Back" }));
    expect(await screen.findByLabelText("Purpose")).toHaveValue("Forum");
  });

  it("warns before leaving dirty work and lets the user remain", async () => {
    renderWorkflow();
    await screen.findByRole("option", { name: "Forum" });
    fireEvent.change(await screen.findByLabelText("Purpose"), {
      target: { value: "Forum" },
    });

    const unload = new Event("beforeunload", { cancelable: true });
    window.dispatchEvent(unload);
    expect(unload.defaultPrevented).toBe(true);

    fireEvent.click(screen.getByRole("link", { name: "Travel history link" }));
    expect(
      screen.getByRole("dialog", { name: "Leave this application?" }),
    ).toBeVisible();
    fireEvent.click(
      screen.getByRole("button", { name: "Stay on application" }),
    );

    expect(screen.getByLabelText("Purpose")).toHaveValue("Forum");
    expect(
      screen.queryByRole("heading", { name: "Travel history" }),
    ).not.toBeInTheDocument();
  });

  it("leaves without a warning when the draft is unchanged", async () => {
    renderWorkflow();
    fireEvent.click(screen.getByRole("link", { name: "Travel history link" }));
    expect(
      await screen.findByRole("heading", { name: "Travel history" }),
    ).toBeVisible();
    expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
  });

  it("continues to the requested destination when leaving is confirmed", async () => {
    renderWorkflow();
    await screen.findByRole("option", { name: "Forum" });
    fireEvent.change(await screen.findByLabelText("Purpose"), {
      target: { value: "Forum" },
    });
    fireEvent.click(screen.getByRole("link", { name: "Travel history link" }));
    fireEvent.click(screen.getByRole("button", { name: "Leave application" }));

    expect(
      await screen.findByRole("heading", { name: "Travel history" }),
    ).toBeVisible();
  });

  it("resolves counties, confirms a long trip, and advances with calculated values", async () => {
    const draft = returnReadyDraft();
    const fetchMock = vi.fn(async (url, options = {}) => {
      if (String(url).includes("/config")) {
        return successfulResponse({ config: { googleApiKey: "test-key" } });
      }
      if (String(url).endsWith("/travel/event-types")) {
        return successfulResponse([{ name: "Forum", displayName: "Forum" }]);
      }
      if (String(url).endsWith("/travel/mode-of-transportation")) {
        return successfulResponse([
          { methodOfTravel: "TRAIN", displayName: "Train" },
        ]);
      }
      if (String(url).includes("/travel/geocode")) {
        return jsonResponse({
          results: [
            {
              address_components: [
                {
                  long_name: "Erie County",
                  types: ["administrative_area_level_2"],
                },
              ],
            },
          ],
        });
      }
      if (options.method === "PATCH") {
        const submittedDraft = JSON.parse(options.body).draft;
        return successfulResponse({
          ...submittedDraft,
          amendment: {
            ...submittedDraft.amendment,
            allowances: { meals: [{ reimbursement: 42 }] },
          },
        });
      }
      return successfulResponse(draft);
    });
    vi.stubGlobal("fetch", fetchMock);

    renderWorkflow(draft);
    await advanceToReturn();
    fireEvent.click(screen.getByRole("button", { name: "Next" }));

    expect(
      await screen.findByRole("dialog", { name: "Confirm your travel dates" }),
    ).toBeVisible();
    expect(fetchMock).not.toHaveBeenCalledWith(
      "/api/v1/travel/drafts",
      expect.objectContaining({ method: "PATCH" }),
    );

    fireEvent.click(screen.getByRole("button", { name: "Dates are correct" }));
    await waitFor(() =>
      expect(screen.getByRole("button", { name: "Expenses" })).toHaveAttribute(
        "aria-current",
        "step",
      ),
    );
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/v1/travel/drafts",
      expect.objectContaining({
        method: "PATCH",
        body: expect.stringContaining('"options":["ROUTE"]'),
      }),
    );
    expect(
      fetchMock.mock.calls.filter(([url]) => String(url).includes("geocode")),
    ).not.toHaveLength(0);
    const patchBody = JSON.parse(
      fetchMock.mock.calls.find(([, options]) => options.method === "PATCH")[1]
        .body,
    );
    expect(
      patchBody.draft.amendment.route.returnLegs[0].from.address.county,
    ).toBe("Erie");
  });

  it("retains Return entries and blocks Expenses when route calculation fails", async () => {
    const draft = returnReadyDraft();
    draft.amendment.route.outboundLegs[0].to.address.county = "Erie";
    draft.amendment.route.returnLegs[0].from.address.county = "Erie";
    const fetchMock = vi.fn(async (url, options = {}) => {
      if (String(url).includes("/config")) {
        return successfulResponse({ config: { googleApiKey: "test-key" } });
      }
      if (String(url).endsWith("/travel/event-types")) {
        return successfulResponse([{ name: "Forum", displayName: "Forum" }]);
      }
      if (String(url).endsWith("/travel/mode-of-transportation")) {
        return successfulResponse([
          { methodOfTravel: "TRAIN", displayName: "Train" },
        ]);
      }
      if (options.method === "PATCH") {
        return failedResponse(502, { code: "DATA_PROVIDER_ERROR" });
      }
      return successfulResponse(draft);
    });
    vi.stubGlobal("fetch", fetchMock);

    renderWorkflow(draft);
    await advanceToReturn();
    fireEvent.change(screen.getByLabelText("Travel date"), {
      target: { value: "2026-08-19" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    fireEvent.click(
      await screen.findByRole("button", { name: "Dates are correct" }),
    );

    expect(await screen.findByRole("alert")).toHaveTextContent(
      "third-party travel service is unavailable",
    );
    expect(screen.getByRole("button", { name: "Return" })).toHaveAttribute(
      "aria-current",
      "step",
    );
    expect(screen.getByLabelText("Travel date")).toHaveValue("2026-08-19");
    expect(screen.getByRole("button", { name: "Expenses" })).toBeDisabled();
  });

  it("recalculates changed expenses and advances with the authoritative response", async () => {
    const draft = calculatedExpenseDraft(returnReadyDraft());
    draft.amendment.route.outboundLegs[0].to.address.county = "Erie";
    draft.amendment.route.returnLegs[0].from.address.county = "Erie";
    const patchBodies = [];
    const fetchMock = vi.fn(async (url, options = {}) => {
      if (String(url).includes("/config")) {
        return successfulResponse({ config: { googleApiKey: "test-key" } });
      }
      if (String(url).endsWith("/travel/event-types")) {
        return successfulResponse([{ name: "Forum", displayName: "Forum" }]);
      }
      if (String(url).endsWith("/travel/mode-of-transportation")) {
        return successfulResponse([
          { methodOfTravel: "TRAIN", displayName: "Train" },
        ]);
      }
      if (options.method === "PATCH") {
        const body = JSON.parse(options.body);
        patchBodies.push(body);
        const calculated = calculatedExpenseDraft(body.draft);
        if (body.options.includes("ALLOWANCES")) {
          calculated.amendment.totalAllowance = 25;
        }
        return successfulResponse(calculated);
      }
      return successfulResponse(draft);
    });
    vi.stubGlobal("fetch", fetchMock);

    renderWorkflow(draft);
    await advanceToReturn();
    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    await waitFor(() =>
      expect(screen.getByRole("button", { name: "Expenses" })).toHaveAttribute(
        "aria-current",
        "step",
      ),
    );
    fireEvent.change(screen.getByLabelText(/^Tolls/), {
      target: { value: "25" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Next" }));

    await screen.findByRole("heading", { name: "Review" });
    expect(patchBodies).toHaveLength(1);
    expect(patchBodies[0].options).toEqual([
      "ALLOWANCES",
      "MEAL_PER_DIEMS",
      "LODGING_PER_DIEMS",
      "MILEAGE_PER_DIEMS",
    ]);
    expect(patchBodies[0].draft.amendment.allowances.tolls).toBe(25);

    fireEvent.click(screen.getByRole("button", { name: "Save" }));
    expect(await screen.findByText(/saved as a draft/i)).toBeVisible();
    expect(screen.getByRole("heading", { name: "Review" })).toBeVisible();
    expect(
      fetchMock.mock.calls.some(
        ([url, options]) =>
          String(url).endsWith("/travel/drafts") && options.method === "POST",
      ),
    ).toBe(true);
    const unload = new Event("beforeunload", { cancelable: true });
    window.dispatchEvent(unload);
    expect(unload.defaultPrevented).toBe(false);
  });

  it("does not recalculate unchanged expenses returned by route calculation", async () => {
    const draft = calculatedExpenseDraft(returnReadyDraft());
    draft.amendment.route.outboundLegs[0].to.address.county = "Erie";
    draft.amendment.route.returnLegs[0].from.address.county = "Erie";
    const patchBodies = [];
    vi.stubGlobal(
      "fetch",
      vi.fn(async (url, options = {}) => {
        if (String(url).includes("/config")) {
          return successfulResponse({ config: { googleApiKey: "test-key" } });
        }
        if (String(url).endsWith("/travel/event-types")) {
          return successfulResponse([{ name: "Forum", displayName: "Forum" }]);
        }
        if (String(url).endsWith("/travel/mode-of-transportation")) {
          return successfulResponse([
            { methodOfTravel: "TRAIN", displayName: "Train" },
          ]);
        }
        if (options.method === "PATCH") {
          const body = JSON.parse(options.body);
          patchBodies.push(body);
          return successfulResponse(calculatedExpenseDraft(body.draft));
        }
        return successfulResponse(draft);
      }),
    );

    renderWorkflow(draft);
    await advanceToReturn();
    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    await waitFor(() =>
      expect(screen.getByRole("button", { name: "Expenses" })).toHaveAttribute(
        "aria-current",
        "step",
      ),
    );
    fireEvent.click(screen.getByRole("button", { name: "Next" }));

    await screen.findByRole("heading", { name: "Review" });
    expect(patchBodies).toHaveLength(0);
  });

  it("confirms, locks, and completes submission before leaving the workflow", async () => {
    const draft = calculatedExpenseDraft(returnReadyDraft());
    draft.amendment.route.outboundLegs[0].to.address.county = "Erie";
    draft.amendment.route.returnLegs[0].from.address.county = "Erie";
    const submission = deferredPromise();
    vi.stubGlobal(
      "fetch",
      vi.fn(async (url) => {
        if (String(url).includes("/config"))
          return successfulResponse({ config: { googleApiKey: "test-key" } });
        if (String(url).endsWith("/travel/event-types"))
          return successfulResponse([{ name: "Forum", displayName: "Forum" }]);
        if (String(url).endsWith("/travel/mode-of-transportation"))
          return successfulResponse([
            { methodOfTravel: "TRAIN", displayName: "Train" },
          ]);
        if (String(url).endsWith("/travel/drafts/submit"))
          return submission.promise;
        return successfulResponse(draft);
      }),
    );

    renderWorkflow(draft);
    await advanceToReturn();
    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    await waitFor(() =>
      expect(screen.getByRole("button", { name: "Expenses" })).toHaveAttribute(
        "aria-current",
        "step",
      ),
    );
    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    await screen.findByRole("heading", { name: "Review" });

    fireEvent.click(screen.getByRole("button", { name: "Submit application" }));
    expect(
      screen.getByRole("dialog", { name: "Submit travel application?" }),
    ).toBeVisible();
    fireEvent.click(screen.getByRole("button", { name: "Submit application" }));
    expect(
      await screen.findByText(/Submitting your travel application/),
    ).toBeVisible();
    expect(screen.getByRole("button", { name: "Back" })).toBeDisabled();
    expect(screen.getByRole("button", { name: "Save" })).toBeDisabled();
    expect(
      screen.getByRole("button", { name: /Purpose.*completed/ }),
    ).toBeDisabled();
    fireEvent.click(screen.getByRole("link", { name: "Travel history link" }));
    expect(screen.getByRole("heading", { name: "Review" })).toBeVisible();
    expect(
      screen.queryByRole("dialog", { name: "Leave this application?" }),
    ).not.toBeInTheDocument();

    submission.resolve(successfulResponse({ id: 42 }));
    expect(
      await screen.findByRole("dialog", { name: "Application submitted" }),
    ).toBeVisible();
    fireEvent.click(screen.getByRole("button", { name: "Go back to ESS" }));
    expect(
      await screen.findByRole("heading", { name: "Travel home" }),
    ).toBeVisible();
  });

  it("retains review data after failure and requires confirmation before retry", async () => {
    const draft = calculatedExpenseDraft(returnReadyDraft());
    draft.amendment.route.outboundLegs[0].to.address.county = "Erie";
    draft.amendment.route.returnLegs[0].from.address.county = "Erie";
    let submitAttempts = 0;
    vi.stubGlobal(
      "fetch",
      vi.fn(async (url) => {
        if (String(url).includes("/config"))
          return successfulResponse({ config: { googleApiKey: "test-key" } });
        if (String(url).endsWith("/travel/event-types"))
          return successfulResponse([{ name: "Forum", displayName: "Forum" }]);
        if (String(url).endsWith("/travel/mode-of-transportation"))
          return successfulResponse([
            { methodOfTravel: "TRAIN", displayName: "Train" },
          ]);
        if (String(url).endsWith("/travel/drafts/submit")) {
          submitAttempts += 1;
          return failedResponse(500, { message: "failed" });
        }
        return successfulResponse(draft);
      }),
    );

    renderWorkflow(draft);
    await advanceToReturn();
    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    await waitFor(() =>
      expect(screen.getByRole("button", { name: "Expenses" })).toHaveAttribute(
        "aria-current",
        "step",
      ),
    );
    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    await screen.findByRole("heading", { name: "Review" });
    fireEvent.click(screen.getByRole("button", { name: "Submit application" }));
    fireEvent.click(screen.getByRole("button", { name: "Submit application" }));

    expect(await screen.findByRole("alert")).toHaveTextContent(
      "could not be submitted",
    );
    expect(screen.getByRole("heading", { name: "Review" })).toBeVisible();
    fireEvent.click(screen.getByRole("button", { name: "Submit application" }));
    expect(
      screen.getByRole("dialog", { name: "Submit travel application?" }),
    ).toBeVisible();
    expect(submitAttempts).toBe(1);
  });
});

function deferredPromise() {
  let resolve;
  const promise = new Promise((resolvePromise) => {
    resolve = resolvePromise;
  });
  return { promise, resolve };
}

function successfulResponse(result) {
  return jsonResponse({ result });
}

function jsonResponse(body) {
  return {
    ok: true,
    status: 200,
    statusText: "",
    json: async () => body,
  };
}

function failedResponse(status, data) {
  return {
    ok: false,
    status,
    statusText: "Server Error",
    json: async () => data,
  };
}
