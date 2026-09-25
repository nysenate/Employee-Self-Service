import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import {
  MemoryRouter,
  Route,
  Routes,
  useLocation,
  useNavigate,
} from "react-router-dom";
import ResubmitTravelApplicationPage from "./index";

afterEach(() => {
  vi.unstubAllGlobals();
  delete window.google;
});

beforeEach(() => {
  window.google = {
    maps: {
      Map: vi.fn(),
      DirectionsRenderer: vi.fn(() => ({
        setMap: vi.fn(),
        setDirections: vi.fn(),
      })),
      DirectionsService: vi.fn(() => ({
        route: vi.fn((_request, callback) => callback({}, "OK")),
      })),
      TravelMode: { DRIVING: "DRIVING" },
    },
  };
});

function address(name, zip5, county) {
  return {
    formattedAddressWithCounty: name,
    zip5,
    county,
    state: "NY",
    country: "United States",
  };
}

function application(status = disapprovedStatus()) {
  return {
    id: 42,
    submittedDateTime: "2024-07-04T09:30:00",
    traveler: { fullName: "Taylor Traveler" },
    status,
  };
}

function disapprovedStatus() {
  return {
    name: "DISAPPROVED",
    label: "Disapproved",
    isDisapproved: true,
    note: "Please clarify the conference purpose.",
  };
}

function editDraft() {
  const albany = address("Albany, NY 12207", "12207", "Albany");
  const buffalo = address("Buffalo, NY 14202", "14202", "Erie");
  return {
    id: 0,
    traveler: {
      fullName: "Taylor Traveler",
      jobTitle: "Analyst",
      department: { head: { fullName: "Department Head" } },
      empWorkLocation: { address: albany },
    },
    amendment: {
      purposeOfTravel: {
        eventType: { name: "Forum", displayName: "Forum" },
        additionalPurpose: "Original purpose",
        summary: "Forum: Original purpose",
      },
      attachments: [
        { filename: "existing-file", originalName: "original-agenda.pdf" },
      ],
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
        tolls: 2,
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

function jsonResponse(body, { ok = true, status = 200 } = {}) {
  return Promise.resolve({
    ok,
    status,
    statusText: ok ? "" : "Request failed",
    json: async () => body,
  });
}

function standardFetch({ app = application(), draft = editDraft() } = {}) {
  return vi.fn((url, options = {}) => {
    if (url === "/api/v1/travel/applications/42") {
      return jsonResponse({ result: app });
    }
    if (url === "/api/v1/travel/application/edit/42") {
      return jsonResponse({ result: draft });
    }
    if (url.endsWith("/travel/event-types")) {
      return jsonResponse({
        result: [{ name: "Forum", displayName: "Forum" }],
      });
    }
    if (url.endsWith("/travel/mode-of-transportation")) {
      return jsonResponse({
        result: [{ methodOfTravel: "TRAIN", displayName: "Train" }],
      });
    }
    if (url.endsWith("/travel/drafts") && options.method === "PATCH") {
      const { draft: patchedDraft } = JSON.parse(options.body);
      return jsonResponse({
        result: {
          ...patchedDraft,
          amendment: {
            ...patchedDraft.amendment,
            totalAllowance: patchedDraft.amendment.allowances.tolls,
          },
        },
      });
    }
    if (url.endsWith("/travel/drafts/attachment")) {
      return jsonResponse({
        result: {
          items: [{ filename: "new-file", originalName: "updated-agenda.pdf" }],
        },
      });
    }
    if (url.endsWith("/travel/application/edit/resubmit/42")) {
      return jsonResponse({ success: true, message: "Updated" });
    }
    if (url.endsWith("/config")) {
      return jsonResponse({ result: { config: { googleApiKey: "test" } } });
    }
    return jsonResponse({ result: [] });
  });
}

function LocationProbe() {
  const location = useLocation();
  return (
    <output data-testid="location">
      {location.pathname}
      {location.search}|{JSON.stringify(location.state)}
    </output>
  );
}

function SwitchApplication() {
  const navigate = useNavigate();
  return (
    <button onClick={() => navigate("/travel/applications/43/resubmit")}>
      Switch application
    </button>
  );
}

function renderPage(
  initialEntry = "/travel/applications/42/resubmit",
  queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false }, mutations: { retry: false } },
  }),
) {
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[initialEntry]}>
        <SwitchApplication />
        <Routes>
          <Route
            path="/travel/applications/:appId/resubmit"
            element={<ResubmitTravelApplicationPage />}
          />
          <Route path="/travel/applications" element={<LocationProbe />} />
          <Route path="/travel" element={<LocationProbe />} />
          <Route path="/logout" element={<LocationProbe />} />
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

async function advanceToReview() {
  await screen.findByRole("option", { name: "Forum" });
  fireEvent.click(screen.getByRole("button", { name: "Next" }));
  fireEvent.click(screen.getByRole("button", { name: "Next" }));
  await waitFor(() =>
    expect(screen.getByRole("button", { name: "Return" })).toHaveAttribute(
      "aria-current",
      "step",
    ),
  );
  fireEvent.click(screen.getByRole("button", { name: "Next" }));
  await screen.findByRole("heading", { name: "Expenses" });
  fireEvent.click(screen.getByRole("button", { name: "Next" }));
  await screen.findByRole("heading", { name: "Review" });
  fireEvent.click(screen.getByRole("button", { name: "Save and Resubmit" }));
  fireEvent.click(
    screen.getAllByRole("button", { name: "Save and Resubmit" }).at(-1),
  );
}

describe("ResubmitTravelApplication", () => {
  it("rejects an invalid route ID without making initialization requests", async () => {
    const fetchMock = vi.fn();
    vi.stubGlobal("fetch", fetchMock);
    renderPage("/travel/applications/not-a-number/resubmit");

    expect(
      await screen.findByRole("heading", {
        name: "Travel application unavailable",
      }),
    ).toBeVisible();
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("loads the authorized disapproved session and shows its identity and reason", async () => {
    const fetchMock = standardFetch();
    vi.stubGlobal("fetch", fetchMock);
    renderPage();

    expect(screen.getByRole("status")).toHaveTextContent(
      "Loading your travel application",
    );
    expect(
      await screen.findByRole("heading", { name: "Purpose" }),
    ).toBeVisible();
    expect(screen.queryByText("#42")).not.toBeInTheDocument();
    expect(screen.getByText("Taylor Traveler")).toBeVisible();
    expect(
      screen.queryByText("Please clarify the conference purpose."),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByRole("button", { name: "Save" }),
    ).not.toBeInTheDocument();
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/v1/travel/application/edit/42",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("offers logout after a successful resubmission", async () => {
    vi.stubGlobal("fetch", standardFetch());
    renderPage();
    await advanceToReview();

    await screen.findByRole("dialog", { name: "Application resubmitted" });
    fireEvent.click(screen.getByRole("button", { name: "Log out of ESS" }));
    expect(await screen.findByTestId("location")).toHaveTextContent(
      "/logout|null",
    );
  });

  it("requires fresh disapproved metadata before rendering the editor", async () => {
    const fetchMock = standardFetch({
      app: application({
        name: "PENDING_DEPARTMENT_HEAD",
        label: "Pending",
        isDisapproved: false,
      }),
    });
    vi.stubGlobal("fetch", fetchMock);
    renderPage();

    expect(
      await screen.findByRole("heading", {
        name: "This application cannot be resubmitted",
      }),
    ).toBeVisible();
    expect(
      screen.queryByRole("heading", { name: "Purpose" }),
    ).not.toBeInTheDocument();
  });

  it.each([
    [400, "MISSING_DEPARTMENT", "Department information is missing"],
    [401, "UNAUTHORIZED", "Access is unavailable"],
    [404, "NOT_FOUND", "Travel application unavailable"],
  ])(
    "renders the expected initializer state for HTTP %s",
    async (status, errorCode, title) => {
      const fetchMock = vi.fn((url) => {
        if (url.endsWith("/travel/applications/42")) {
          return jsonResponse({ result: application() });
        }
        return jsonResponse(
          { errorCode, message: "failed" },
          { ok: false, status },
        );
      });
      vi.stubGlobal("fetch", fetchMock);
      renderPage();

      expect(await screen.findByRole("heading", { name: title })).toBeVisible();
      expect(
        screen.queryByRole("heading", { name: "Purpose" }),
      ).not.toBeInTheDocument();
      expect(
        fetchMock.mock.calls.some(
          ([url, options]) =>
            url.endsWith("/travel/drafts") && options?.method === "PUT",
        ),
      ).toBe(false);
    },
  );

  it("retries a transient initialization failure without starting a blank draft", async () => {
    let editAttempts = 0;
    const fetchMock = vi.fn((url) => {
      if (url.endsWith("/travel/applications/42")) {
        return jsonResponse({ result: application() });
      }
      if (url.endsWith("/travel/application/edit/42")) {
        editAttempts += 1;
        return editAttempts === 1
          ? jsonResponse({ message: "failed" }, { ok: false, status: 500 })
          : jsonResponse({ result: editDraft() });
      }
      if (url.endsWith("/travel/event-types")) {
        return jsonResponse({
          result: [{ name: "Forum", displayName: "Forum" }],
        });
      }
      return jsonResponse({ result: [] });
    });
    vi.stubGlobal("fetch", fetchMock);
    renderPage();

    fireEvent.click(await screen.findByRole("button", { name: "Try again" }));
    expect(
      await screen.findByRole("heading", { name: "Purpose" }),
    ).toBeVisible();
    expect(editAttempts).toBe(2);
  });

  it("keeps editing or discards back to the validated history context", async () => {
    vi.stubGlobal("fetch", standardFetch());
    renderPage({
      pathname: "/travel/applications/42/resubmit",
      state: {
        returnTo: "/travel/applications?status=DISAPPROVED&offset=3&appId=42",
      },
    });
    await screen.findByRole("heading", { name: "Purpose" });

    fireEvent.click(screen.getByRole("button", { name: "Cancel edits" }));
    fireEvent.click(screen.getByRole("button", { name: "Keep editing" }));
    expect(screen.getByRole("heading", { name: "Purpose" })).toBeVisible();

    fireEvent.click(screen.getByRole("button", { name: "Cancel edits" }));
    fireEvent.click(screen.getByRole("button", { name: "Discard edits" }));
    expect(await screen.findByTestId("location")).toHaveTextContent(
      "/travel/applications?status=DISAPPROVED&offset=3&appId=42",
    );
  });

  it("ignores an unsafe return destination on cancellation", async () => {
    vi.stubGlobal("fetch", standardFetch());
    renderPage({
      pathname: "/travel/applications/42/resubmit",
      state: { returnTo: "//external.example/leave" },
    });
    await screen.findByRole("heading", { name: "Purpose" });

    fireEvent.click(screen.getByRole("button", { name: "Cancel edits" }));
    fireEvent.click(screen.getByRole("button", { name: "Discard edits" }));
    expect(await screen.findByTestId("location")).toHaveTextContent(
      "/travel/applications|null",
    );
  });

  it("remounts a fresh editor when the URL application ID changes", async () => {
    const fetchMock = vi.fn((url) => {
      const id = url.endsWith("/43") ? 43 : 42;
      if (url.includes("/travel/applications/")) {
        return jsonResponse({ result: { ...application(), id } });
      }
      if (url.includes("/travel/application/edit/")) {
        return jsonResponse({
          result: {
            ...editDraft(),
            amendment: {
              ...editDraft().amendment,
              purposeOfTravel: {
                ...editDraft().amendment.purposeOfTravel,
                additionalPurpose: `Original ${id}`,
              },
            },
          },
        });
      }
      if (url.endsWith("/travel/event-types")) {
        return jsonResponse({
          result: [{ name: "Forum", displayName: "Forum" }],
        });
      }
      return jsonResponse({ result: [] });
    });
    vi.stubGlobal("fetch", fetchMock);
    renderPage();
    const purposeField = await screen.findByLabelText(
      "Additional information (optional)",
    );
    fireEvent.change(purposeField, { target: { value: "Unsaved edit" } });
    expect(purposeField).toHaveValue("Unsaved edit");

    fireEvent.click(screen.getByRole("button", { name: "Switch application" }));
    expect(
      await screen.findByLabelText("Additional information (optional)"),
    ).toHaveValue("Original 43");
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/v1/travel/application/edit/43",
      expect.anything(),
    );
  });

  it("blocks a conflict without losing local editing and permits cancel", async () => {
    const fetchMock = standardFetch();
    fetchMock.mockImplementation((url, options) => {
      if (url.endsWith("/travel/application/edit/resubmit/42")) {
        return jsonResponse(
          { errorCode: "TRAVEL_RESUBMISSION_CONFLICT" },
          { ok: false, status: 409 },
        );
      }
      return standardFetch()(url, options);
    });
    vi.stubGlobal("fetch", fetchMock);
    renderPage();
    await advanceToReview();

    expect(
      await screen.findByText(
        "This application is no longer available for resubmission.",
      ),
    ).toBeVisible();
    expect(
      screen.getByRole("button", { name: "Save and Resubmit" }),
    ).toBeDisabled();
    fireEvent.click(screen.getByRole("button", { name: "Back" }));
    expect(
      await screen.findByRole("heading", { name: "Expenses" }),
    ).toBeVisible();
    fireEvent.click(screen.getByRole("button", { name: "Cancel edits" }));
    fireEvent.click(screen.getByRole("button", { name: "Discard edits" }));
    expect(await screen.findByTestId("location")).toHaveTextContent(
      "/travel/applications|null",
    );
    expect(
      fetchMock.mock.calls.filter(([url]) =>
        url.endsWith("/travel/application/edit/resubmit/42"),
      ),
    ).toHaveLength(1);
  });

  it("checks an uncertain outcome with GET only and retains the review draft", async () => {
    const fetchMock = standardFetch();
    let postCount = 0;
    fetchMock.mockImplementation((url, options) => {
      if (url.endsWith("/travel/application/edit/resubmit/42")) {
        postCount += 1;
        return jsonResponse({ message: "Missing acknowledgement" });
      }
      return standardFetch()(url, options);
    });
    vi.stubGlobal("fetch", fetchMock);
    renderPage();
    await advanceToReview();

    expect(
      await screen.findByText(
        /could not confirm whether your application was resubmitted/i,
      ),
    ).toBeVisible();
    expect(
      screen.getByRole("button", { name: "Save and Resubmit" }),
    ).toBeDisabled();
    fireEvent.click(
      screen.getByRole("button", { name: "Check application status" }),
    );
    expect(
      await screen.findByText(/Its status has been checked/i),
    ).toBeVisible();
    expect(
      screen.getByRole("button", { name: "Save and Resubmit" }),
    ).toBeEnabled();
    expect(screen.getByText("7/04/24")).toBeVisible();
    expect(postCount).toBe(1);
    expect(
      fetchMock.mock.calls.filter(([url]) =>
        url.endsWith("/travel/application/edit/42"),
      ),
    ).toHaveLength(1);
  });

  it("edits a rich prefilled application and resubmits once to the URL ID", async () => {
    const fetchMock = standardFetch();
    vi.stubGlobal("fetch", fetchMock);
    renderPage({
      pathname: "/travel/applications/42/resubmit",
      search: "?role=TRAVEL_ADMIN",
      state: {
        returnTo:
          "/travel/applications?status=DISAPPROVED&sort=startDate%3Adesc&offset=2&appId=42",
      },
    });

    await screen.findByRole("option", { name: "Forum" });
    fireEvent.change(
      screen.getByLabelText("Additional information (optional)"),
      {
        target: { value: "Updated purpose" },
      },
    );
    fireEvent.click(
      screen.getByRole("button", { name: "Remove original-agenda.pdf" }),
    );
    fireEvent.change(screen.getByLabelText("Attach documents"), {
      target: {
        files: [
          new File(["agenda"], "updated-agenda.pdf", {
            type: "application/pdf",
          }),
        ],
      },
    });
    expect(await screen.findByText("updated-agenda.pdf")).toBeVisible();
    fireEvent.click(screen.getByRole("button", { name: "Next" }));

    fireEvent.change(screen.getByLabelText("Outbound date"), {
      target: { value: "2026-08-12" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    await waitFor(() =>
      expect(screen.getByRole("button", { name: "Return" })).toHaveAttribute(
        "aria-current",
        "step",
      ),
    );
    fireEvent.change(screen.getByLabelText("Return date"), {
      target: { value: "2026-08-13" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    await screen.findByRole("heading", { name: "Expenses" });

    fireEvent.change(screen.getByLabelText("Tolls: $"), {
      target: { value: "19.50" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    await screen.findByRole("heading", { name: "Review" });
    expect(screen.getByText("7/04/24")).toBeVisible();
    expect(screen.queryByText("#42")).not.toBeInTheDocument();
    expect(screen.getByText(/Updated purpose/)).toBeVisible();
    expect(screen.getByText("$19.50")).toBeVisible();
    expect(
      screen.getByRole("link", { name: /updated-agenda.pdf/ }),
    ).toBeVisible();
    expect(screen.queryByText("original-agenda.pdf")).not.toBeInTheDocument();

    fireEvent.click(screen.getByRole("button", { name: "Save and Resubmit" }));
    const confirm = screen.getByRole("button", { name: "Save and Resubmit" });
    fireEvent.click(confirm);
    fireEvent.click(confirm);

    expect(
      await screen.findByRole("dialog", { name: "Application resubmitted" }),
    ).toBeVisible();
    expect(screen.queryByTestId("location")).not.toBeInTheDocument();
    fireEvent.click(screen.getByRole("button", { name: "Go back to ESS" }));
    expect(await screen.findByTestId("location")).toHaveTextContent(
      "/travel|null",
    );

    const commitCalls = fetchMock.mock.calls.filter(([url]) =>
      url.endsWith("/travel/application/edit/resubmit/42"),
    );
    expect(commitCalls).toHaveLength(1);
    const committedDraft = JSON.parse(commitCalls[0][1].body);
    expect(committedDraft.id).toBe(0);
    expect(committedDraft.amendment.purposeOfTravel.additionalPurpose).toBe(
      "Updated purpose",
    );
    expect(committedDraft.amendment.attachments).toEqual([
      { filename: "new-file", originalName: "updated-agenda.pdf" },
    ]);
    expect(committedDraft.amendment.route.outboundLegs[0].travelDate).toBe(
      "08/12/2026",
    );
    expect(committedDraft.amendment.allowances.tolls).toBe(19.5);
    expect(
      fetchMock.mock.calls.some(([url]) =>
        url.includes("/travel/application/edit/42?role"),
      ),
    ).toBe(false);
  });
});
