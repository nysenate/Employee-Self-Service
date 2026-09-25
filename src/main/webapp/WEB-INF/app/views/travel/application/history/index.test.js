import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { afterEach, describe, expect, it, vi } from "vitest";
import {
  MemoryRouter,
  Route,
  Routes,
  useLocation,
  useNavigate,
} from "react-router-dom";
import ApplicationHistory from "./index";

afterEach(() => vi.unstubAllGlobals());

function response(body) {
  return Promise.resolve({
    ok: true,
    status: 200,
    statusText: "",
    json: async () => body,
  });
}

function detailApplication() {
  return {
    id: 42,
    submittedDateTime: "2025-05-10T09:30:00",
    traveler: { fullName: "Independently Loaded Traveler" },
    activeAmendment: {
      startDate: "2025-06-01",
      endDate: "2025-06-02",
      purposeOfTravel: { summary: "Detail outside current results" },
      route: { outboundLegs: [], destinations: [] },
      attachments: [],
    },
    status: {
      name: "DISAPPROVED",
      label: "Disapproved",
      isDisapproved: true,
      note: "Detail-only reason",
    },
  };
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

function EditorProbe() {
  const location = useLocation();
  const navigate = useNavigate();
  return (
    <div>
      <LocationProbe />
      <button
        onClick={() => navigate(location.state.returnTo, { replace: true })}
      >
        Discard edits
      </button>
    </div>
  );
}

function renderHistory(initialEntry) {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[initialEntry]}>
        <Routes>
          <Route
            path="/travel/applications"
            element={
              <>
                <ApplicationHistory />
                <LocationProbe />
              </>
            }
          />
          <Route
            path="/travel/applications/:appId/resubmit"
            element={<EditorProbe />}
          />
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

describe("ApplicationHistory details selection", () => {
  it("opens URL-selected details even when the filtered result page is empty", async () => {
    const fetchMock = vi.fn((url) => {
      if (url === "/api/v1/travel/applications/42") {
        return response({ result: detailApplication() });
      }
      if (url.startsWith("/api/v1/travel/applications?")) {
        return response({ result: [], total: 0 });
      }
      return response({ result: [] });
    });
    vi.stubGlobal("fetch", fetchMock);
    renderHistory(
      "/travel/applications?status=DISAPPROVED&sort=startDate%3Adesc&limit=16&offset=1&appId=42",
    );

    expect(
      await screen.findByText("No travel applications match these filters"),
    ).toBeVisible();
    expect(
      await screen.findByText("Detail outside current results"),
    ).toBeVisible();
    expect(screen.getByText("Detail-only reason")).toBeVisible();

    fireEvent.click(screen.getByRole("button", { name: "Close" }));
    await waitFor(() => {
      const search = screen.getByTestId("location").textContent;
      expect(search).toContain("status=DISAPPROVED");
      expect(search).toContain("sort=startDate%3Adesc");
      expect(search).not.toContain("appId");
    });
  });

  it("updates the browser URL when a result row is selected", async () => {
    const summary = {
      id: 42,
      startDate: "2025-06-01",
      travelerName: "Summary Traveler",
      destinationSummary: "Summary destination",
      totalAllowance: 10,
      status: { name: "DISAPPROVED", isDisapproved: true },
    };
    const fetchMock = vi.fn((url) => {
      if (url === "/api/v1/travel/applications/42") {
        return response({ result: detailApplication() });
      }
      if (url.startsWith("/api/v1/travel/applications?")) {
        return response({ result: [summary], total: 1 });
      }
      return response({ result: [] });
    });
    vi.stubGlobal("fetch", fetchMock);
    renderHistory("/travel/applications?status=DISAPPROVED");

    fireEvent.click(
      await screen.findByRole("button", {
        name: /View Summary Traveler's travel application/,
      }),
    );

    await waitFor(() =>
      expect(screen.getByTestId("location")).toHaveTextContent("appId=42"),
    );
    expect(
      await screen.findByText("Detail outside current results"),
    ).toBeVisible();
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/v1/travel/applications/42",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("does not issue an application-detail request for an invalid URL ID", async () => {
    const fetchMock = vi.fn((url) => {
      if (url.startsWith("/api/v1/travel/applications?")) {
        return response({ result: [], total: 0 });
      }
      return response({ result: [] });
    });
    vi.stubGlobal("fetch", fetchMock);
    renderHistory("/travel/applications?appId=42oops");

    expect(
      await screen.findByText("No travel applications match these filters"),
    ).toBeVisible();
    expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
    expect(
      fetchMock.mock.calls.some(([url]) =>
        url.includes("/travel/applications/42oops"),
      ),
    ).toBe(false);
  });

  it("opens only the selected full disapproved detail in the editor and restores it on cancel", async () => {
    const summary = {
      id: 42,
      startDate: "2025-06-01",
      travelerName: "Summary Traveler",
      destinationSummary: "Summary destination",
      totalAllowance: 10,
      status: { name: "DISAPPROVED", isDisapproved: true },
    };
    const fetchMock = vi.fn((url) => {
      if (url === "/api/v1/travel/applications/42") {
        return response({ result: detailApplication() });
      }
      if (url.startsWith("/api/v1/travel/applications?")) {
        return response({ result: [summary], total: 1 });
      }
      return response({ result: [] });
    });
    vi.stubGlobal("fetch", fetchMock);
    renderHistory("/travel/applications?status=DISAPPROVED&offset=1");

    fireEvent.click(
      await screen.findByRole("button", {
        name: /View Summary Traveler's travel application/,
      }),
    );
    expect(await screen.findByText("Detail-only reason")).toBeVisible();
    fireEvent.click(screen.getByRole("button", { name: "Edit and Resubmit" }));
    expect(screen.getByTestId("location")).toHaveTextContent(
      '/travel/applications/42/resubmit|{"returnTo":"/travel/applications?',
    );
    expect(screen.getByTestId("location")).toHaveTextContent(
      "status=DISAPPROVED",
    );
    expect(screen.getByTestId("location")).toHaveTextContent("appId=42");

    fireEvent.click(screen.getByRole("button", { name: "Discard edits" }));
    expect(await screen.findByText("Detail-only reason")).toBeVisible();
    expect(screen.getAllByText("Disapproved")).not.toHaveLength(0);
    expect(screen.getByText("Detail outside current results")).toBeVisible();
    expect(
      fetchMock.mock.calls.every(([, options]) => options?.method === "GET"),
    ).toBe(true);
  });

  it("shows updated detail despite an empty disapproved page", async () => {
    const pendingApp = {
      ...detailApplication(),
      activeAmendment: {
        ...detailApplication().activeAmendment,
        purposeOfTravel: { summary: "Updated trip purpose" },
      },
      status: {
        name: "DEPARTMENT_HEAD",
        label: "Pending",
        isDisapproved: false,
      },
    };
    const fetchMock = vi.fn((url) => {
      if (url === "/api/v1/travel/applications/42") {
        return response({ result: pendingApp });
      }
      if (url.startsWith("/api/v1/travel/applications?")) {
        return response({ result: [], total: 0 });
      }
      return response({ result: [] });
    });
    vi.stubGlobal("fetch", fetchMock);
    renderHistory({
      pathname: "/travel/applications",
      search: "?status=DISAPPROVED&limit=16&offset=3&appId=42",
      state: { retained: "context" },
    });

    expect(await screen.findByText("Updated trip purpose")).toBeVisible();
    expect(screen.getByText("Pending")).toBeVisible();
    expect(
      screen.queryByRole("button", { name: "Edit and Resubmit" }),
    ).not.toBeInTheDocument();
    await waitFor(() => {
      expect(screen.getByTestId("location")).toHaveTextContent("offset=1");
      expect(screen.getByTestId("location")).toHaveTextContent("appId=42");
      expect(screen.getByTestId("location")).toHaveTextContent(
        '"retained":"context"',
      );
    });
  });

  it("keeps valid pagination and details when refreshed travel dates leave the date filter", async () => {
    const changedApp = {
      ...detailApplication(),
      activeAmendment: {
        ...detailApplication().activeAmendment,
        startDate: "2027-01-01",
        endDate: "2027-01-03",
        purposeOfTravel: { summary: "Trip moved outside the filter" },
      },
      status: {
        name: "DEPARTMENT_HEAD",
        label: "Pending",
        isDisapproved: false,
      },
    };
    vi.stubGlobal(
      "fetch",
      vi.fn((url) =>
        url === "/api/v1/travel/applications/42"
          ? response({ result: changedApp })
          : response({ result: [], total: 24 }),
      ),
    );
    renderHistory({
      pathname: "/travel/applications",
      search:
        "?range=custom&fromDate=2025-01-01&toDate=2025-12-31&limit=16&offset=17&appId=42",
    });

    expect(
      await screen.findByText("Trip moved outside the filter"),
    ).toBeVisible();
    expect(
      await screen.findByText("No travel applications match these filters"),
    ).toBeVisible();
    expect(screen.getByTestId("location")).toHaveTextContent("offset=17&");
    expect(screen.getByTestId("location")).toHaveTextContent("appId=42");
  });

  it.each([16, 0])(
    "resets the second-page offset when only %i results remain",
    async (total) => {
      vi.stubGlobal(
        "fetch",
        vi.fn(() => response({ result: [], total })),
      );
      renderHistory("/travel/applications?limit=16&offset=17");

      await waitFor(() =>
        expect(screen.getByTestId("location").textContent).toMatch(
          /offset=1(?:&|\|)/,
        ),
      );
    },
  );

  it("waits for a non-placeholder list response before repairing an invalid offset", async () => {
    let resolveList;
    const listResponse = new Promise((resolve) => {
      resolveList = resolve;
    });
    const fetchMock = vi.fn((url) =>
      url === "/api/v1/travel/applications/42"
        ? response({ result: detailApplication() })
        : listResponse,
    );
    vi.stubGlobal("fetch", fetchMock);
    renderHistory(
      "/travel/applications?status=DISAPPROVED&sort=startDate%3Adesc&limit=16&offset=3&appId=42",
    );

    expect(
      await screen.findByText("Detail outside current results"),
    ).toBeVisible();
    expect(screen.getByTestId("location")).toHaveTextContent("offset=3");
    resolveList(await response({ result: [], total: 0 }));
    await waitFor(() =>
      expect(screen.getByTestId("location")).toHaveTextContent("offset=1"),
    );
    await waitFor(() =>
      expect(
        fetchMock.mock.calls.filter(([url]) =>
          url.includes("/travel/applications?"),
        ),
      ).toHaveLength(2),
    );
  });
});

it("cancels from history, refreshes filtered results, and closes both dialogs", async () => {
  let canceled = false;
  const summary = {
    id: 42,
    startDate: "2025-06-01",
    travelerName: "Summary Traveler",
    destinationSummary: "Albany",
    totalAllowance: 10,
    status: detailApplication().status,
  };
  const fetchMock = vi.fn((url, options) => {
    if (options.method === "POST") {
      canceled = true;
      return response({ success: true, result: detailApplication() });
    }
    if (url === "/api/v1/travel/applications/42") {
      return response({
        result: {
          ...detailApplication(),
          ...(canceled
            ? {
                status: {
                  name: "CANCELED",
                  label: "Canceled",
                  isCanceled: true,
                },
              }
            : {}),
        },
      });
    }
    if (url.startsWith("/api/v1/travel/applications?")) {
      return response({
        result: canceled ? [] : [summary],
        total: canceled ? 0 : 1,
      });
    }
    return response({ result: [] });
  });
  vi.stubGlobal("fetch", fetchMock);
  renderHistory(
    "/travel/applications?status=DISAPPROVED&sort=startDate%3Adesc&appId=42",
  );
  fireEvent.click(
    await screen.findByRole("button", { name: "Cancel Application" }),
  );
  fireEvent.click(screen.getByRole("button", { name: "Yes" }));
  await waitFor(() =>
    expect(screen.queryByRole("dialog")).not.toBeInTheDocument(),
  );
  expect(
    await screen.findByText("No travel applications match these filters"),
  ).toBeVisible();
  expect(screen.getByTestId("location")).toHaveTextContent(
    "status=DISAPPROVED",
  );
  expect(screen.getByTestId("location")).toHaveTextContent(
    "sort=startDate%3Adesc",
  );
  expect(screen.getByTestId("location")).not.toHaveTextContent("appId");
  expect(
    fetchMock.mock.calls.filter(([url]) =>
      url.startsWith("/api/v1/travel/applications?"),
    ),
  ).toHaveLength(2);
});
