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
        return successfulResponse([
          { name: "Forum", displayName: "Forum" },
        ]);
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
        return successfulResponse([
          { name: "Forum", displayName: "Forum" },
        ]);
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
});

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
