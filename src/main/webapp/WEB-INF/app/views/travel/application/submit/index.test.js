import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import SubmitApplication from "./index";

function renderPage(initialEntry = "/travel/applications/new") {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[initialEntry]}>
        <Routes>
          <Route
            path="/travel/applications/new"
            element={<SubmitApplication />}
          />
          <Route
            path="/travel/applications/new/:draftId"
            element={<SubmitApplication />}
          />
          <Route
            path="/travel/applications/drafts"
            element={<h1>Travel Application Drafts</h1>}
          />
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

function response(body, { ok = true, statusText = "" } = {}) {
  return Promise.resolve({
    ok,
    statusText,
    json: () => Promise.resolve(body),
  });
}

describe("new travel application initialization", () => {
  beforeEach(() => vi.restoreAllMocks());

  it("creates a draft, displays Purpose, and keeps employee data read-only", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn((url) => {
        if (url.endsWith("/travel/drafts")) {
          return response({
            result: {
              traveler: {
                fullName: "Jamie Rivera",
                nid: "N0123",
                jobTitle: "Analyst",
                workPhone: "518-555-0100",
                empWorkLocation: {
                  address: {
                    formattedAddressWithCounty:
                      "State Street, Albany, NY 12207",
                  },
                },
                isDepartmentHead: false,
                department: { head: { fullName: "Morgan Lee" } },
              },
            },
          });
        }
        return response({ result: [] });
      }),
    );

    renderPage();

    expect(screen.getByRole("status")).toHaveTextContent(
      "Preparing your travel application",
    );
    expect(
      await screen.findByRole("heading", { name: "Purpose of Travel" }),
    ).toBeVisible();
    expect(screen.getByText("Jamie Rivera")).toBeVisible();
    expect(screen.getByText("Morgan Lee")).toBeVisible();
    expect(screen.getByText("State Street, Albany, NY 12207")).toBeVisible();
    expect(
      screen.queryByRole("textbox", { name: /traveler/i }),
    ).not.toBeInTheDocument();
    expect(fetch).toHaveBeenCalledWith(
      "/api/v1/travel/drafts",
      expect.objectContaining({ method: "PUT" }),
    );
  });

  it("loads a saved draft without creating a blank draft", async () => {
    const savedDraft = {
      id: 42,
      traveler: { fullName: "Jamie Rivera" },
      amendment: {
        purposeOfTravel: {
          eventType: { name: "Forum", displayName: "Forum" },
          additionalPurpose: "Regional policy forum",
        },
      },
    };
    const fetchMock = vi.fn((url, options = {}) => {
      if (url.endsWith("/travel/drafts/42")) {
        return response({ result: savedDraft });
      }
      if (url.endsWith("/travel/event-types")) {
        return response({
          result: [{ name: "Forum", displayName: "Forum" }],
        });
      }
      if (url.endsWith("/travel/drafts") && options.method === "POST") {
        return response({ result: savedDraft });
      }
      return response({ result: [] });
    });
    vi.stubGlobal("fetch", fetchMock);

    renderPage("/travel/applications/new/42");

    expect(screen.getByRole("status")).toHaveTextContent(
      "Loading your travel application",
    );
    expect(
      await screen.findByRole("heading", { name: "Purpose" }),
    ).toBeVisible();
    expect(screen.getByText("Continue Travel Application")).toBeVisible();
    expect(screen.getByText("Jamie Rivera")).toBeVisible();
    await screen.findByRole("option", { name: "Forum" });
    expect(screen.getByRole("combobox", { name: "Purpose" })).toHaveValue(
      "Forum",
    );
    expect(
      screen.getByLabelText("Additional information (optional)"),
    ).toHaveValue("Regional policy forum");
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/v1/travel/drafts/42",
      expect.objectContaining({ method: "GET" }),
    );
    expect(fetchMock).not.toHaveBeenCalledWith(
      "/api/v1/travel/drafts",
      expect.objectContaining({ method: "PUT" }),
    );
    const unload = new Event("beforeunload", { cancelable: true });
    window.dispatchEvent(unload);
    expect(unload.defaultPrevented).toBe(false);

    fireEvent.click(screen.getByRole("button", { name: "Save" }));
    expect(await screen.findByText(/saved as a draft/i)).toBeVisible();
    const saveRequest = fetchMock.mock.calls.find(
      ([url, options]) =>
        url.endsWith("/travel/drafts") && options.method === "POST",
    );
    expect(JSON.parse(saveRequest[1].body).id).toBe(42);
  });

  it("shows resume recovery actions without creating a draft when loading fails", async () => {
    vi.stubGlobal(
      "fetch",
      vi
        .fn()
        .mockReturnValue(
          response(
            { errorCode: "INTERNAL_ERROR" },
            { ok: false, statusText: "Server Error" },
          ),
        ),
    );

    renderPage("/travel/applications/new/42");

    expect(
      await screen.findByRole("heading", {
        name: "We couldn’t load this travel application",
      }),
    ).toBeVisible();
    expect(fetch).not.toHaveBeenCalledWith(
      "/api/v1/travel/drafts",
      expect.objectContaining({ method: "PUT" }),
    );
    fireEvent.click(screen.getByRole("button", { name: "Return to drafts" }));
    expect(
      await screen.findByRole("heading", { name: "Travel Application Drafts" }),
    ).toBeVisible();
  });

  it("rejects a malformed resume URL without initializing a blank draft", async () => {
    vi.stubGlobal("fetch", vi.fn());

    renderPage("/travel/applications/new/not-a-number");

    expect(
      await screen.findByRole("heading", {
        name: "We couldn’t load this travel application",
      }),
    ).toBeVisible();
    expect(fetch).not.toHaveBeenCalled();
  });

  it("blocks the form when department information is missing", async () => {
    vi.stubGlobal(
      "fetch",
      vi
        .fn()
        .mockReturnValue(
          response(
            { errorCode: "MISSING_DEPARTMENT", message: "No department" },
            { ok: false, statusText: "Bad Request" },
          ),
        ),
    );

    renderPage();

    expect(
      await screen.findByRole("heading", {
        name: "Department information is missing",
      }),
    ).toBeVisible();
    expect(
      screen.queryByRole("heading", { name: "Purpose of Travel" }),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByRole("button", { name: "Try again" }),
    ).not.toBeInTheDocument();
  });

  it("offers retry after a transient initialization failure", async () => {
    const fetchMock = vi
      .fn()
      .mockReturnValueOnce(
        response(
          { errorCode: "INTERNAL_ERROR" },
          { ok: false, statusText: "Server Error" },
        ),
      )
      .mockReturnValueOnce(
        response({ result: { traveler: { fullName: "Jamie Rivera" } } }),
      )
      .mockReturnValue(response({ result: [] }));
    vi.stubGlobal("fetch", fetchMock);

    renderPage();
    fireEvent.click(await screen.findByRole("button", { name: "Try again" }));

    await waitFor(() =>
      expect(
        screen.getByRole("heading", { name: "Purpose of Travel" }),
      ).toBeVisible(),
    );
    expect(fetchMock).toHaveBeenCalledTimes(3);
  });
});
