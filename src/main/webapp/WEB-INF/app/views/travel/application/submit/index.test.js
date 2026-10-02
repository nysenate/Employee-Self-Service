import NotificationProvider from "app/components/NotificationProvider";
import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import {
  act,
  fireEvent,
  render,
  screen,
  waitFor,
} from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { Link, MemoryRouter, Route, Routes } from "react-router-dom";
import { travelQueryKeys } from "app/views/travel/shared/hooks/travelQueryKeys";
import SubmitApplication from "./index";

function renderPage(
  initialEntry = "/travel/applications/new",
  queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  }),
) {
  return render(
    <NotificationProvider>
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={[initialEntry]}>
          <Link to="/travel/applications/new">Submit Travel Application</Link>
          <Link to="/travel/applications/new/42">Open draft 42</Link>
          <Link to="/travel/applications/new/43">Open draft 43</Link>
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
      </QueryClientProvider>
    </NotificationProvider>,
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
    expect(await screen.findByText(/Draft saved/i)).toBeVisible();
    const saveRequest = fetchMock.mock.calls.find(
      ([url, options]) =>
        url.endsWith("/travel/drafts") && options.method === "POST",
    );
    expect(JSON.parse(saveRequest[1].body).id).toBe(42);
  });

  it.each([
    ["Submit Travel Application", undefined, ""],
    ["Open draft 43", 43, "Second draft"],
  ])(
    "resets the cached editor when navigating to %s",
    async (link, id, contents) => {
      const queryClient = new QueryClient();
      const makeDraft = (draftId, additionalPurpose) => ({
        id: draftId,
        traveler: {},
        amendment: {
          purposeOfTravel: {
            eventType: { name: "Forum", displayName: "Forum" },
            additionalPurpose,
          },
        },
      });
      queryClient.setQueryData(travelQueryKeys.newDraft(), { traveler: {} });
      queryClient.setQueryData(
        travelQueryKeys.draft(42),
        makeDraft(42, "First draft"),
      );
      queryClient.setQueryData(
        travelQueryKeys.draft(43),
        makeDraft(43, "Second draft"),
      );
      const postedDrafts = [];
      vi.stubGlobal(
        "fetch",
        vi.fn((url, options = {}) => {
          if (url.endsWith("/config")) {
            return response({
              result: { config: { googleApiKey: "test-key" } },
            });
          }
          if (url.endsWith("/travel/drafts") && options.method === "POST") {
            const draft = JSON.parse(options.body);
            postedDrafts.push(draft);
            return response({ result: { ...draft, id: draft.id ?? 99 } });
          }
          return response({
            result: [{ name: "Forum", displayName: "Forum" }],
          });
        }),
      );

      renderPage("/travel/applications/new", queryClient);
      await screen.findByRole("option", { name: "Forum" });
      fireEvent.click(screen.getByRole("link", { name: "Open draft 42" }));
      expect(
        screen.getByLabelText("Additional information (optional)"),
      ).toHaveValue("First draft");
      fireEvent.click(screen.getByRole("button", { name: "Next" }));
      expect(screen.getByRole("button", { name: "Outbound" })).toHaveAttribute(
        "aria-current",
        "step",
      );

      fireEvent.click(screen.getByRole("link", { name: link }));
      fireEvent.click(
        await screen.findByRole("button", { name: "Leave application" }),
      );
      expect(
        await screen.findByRole("button", { name: "Purpose" }),
      ).toHaveAttribute("aria-current", "step");
      expect(screen.getByRole("button", { name: "Outbound" })).toBeDisabled();
      expect(screen.getByLabelText("Purpose")).toHaveValue(
        id === undefined ? "" : "Forum",
      );

      if (id === undefined) {
        fireEvent.change(screen.getByLabelText("Purpose"), {
          target: { value: "Forum" },
        });
      }
      expect(
        screen.getByLabelText("Additional information (optional)"),
      ).toHaveValue(contents);
      fireEvent.change(
        screen.getByLabelText("Additional information (optional)"),
        {
          target: { value: "Separate application" },
        },
      );
      fireEvent.click(screen.getByRole("button", { name: "Save" }));
      await screen.findByText("Draft saved");
      expect(postedDrafts).toHaveLength(1);
      expect(postedDrafts[0].id).toBe(id);
      expect(postedDrafts[0].amendment.purposeOfTravel.additionalPurpose).toBe(
        "Separate application",
      );

      // Saving a new draft assigns an ID without changing the editing session.
      fireEvent.click(screen.getByRole("button", { name: "Save" }));
      await waitFor(() => expect(postedDrafts).toHaveLength(2));
      expect(postedDrafts[1].id).toBe(id ?? 99);
      expect(
        screen.getByLabelText("Additional information (optional)"),
      ).toHaveValue("Separate application");
    },
  );

  it("preserves progress and local edits when the same draft's cache is refreshed", async () => {
    const queryClient = new QueryClient();
    const draft = {
      id: 42,
      traveler: {},
      amendment: {
        purposeOfTravel: {
          eventType: { name: "Forum", displayName: "Forum" },
          additionalPurpose: "Original purpose",
        },
      },
    };
    queryClient.setQueryData(travelQueryKeys.draft(42), draft);
    vi.stubGlobal(
      "fetch",
      vi.fn((url) =>
        response({
          result: url.endsWith("/config")
            ? { config: { googleApiKey: "test-key" } }
            : [{ name: "Forum", displayName: "Forum" }],
        }),
      ),
    );
    renderPage("/travel/applications/new/42", queryClient);
    await screen.findByRole("option", { name: "Forum" });
    fireEvent.change(
      screen.getByLabelText("Additional information (optional)"),
      {
        target: { value: "Unsaved purpose" },
      },
    );
    fireEvent.click(screen.getByRole("button", { name: "Next" }));

    await act(async () => {
      queryClient.setQueryData(travelQueryKeys.draft(42), {
        ...draft,
        traveler: { fullName: "Updated traveler" },
      });
    });

    expect(screen.getByRole("button", { name: "Outbound" })).toHaveAttribute(
      "aria-current",
      "step",
    );
    fireEvent.click(screen.getByRole("button", { name: "Back" }));
    expect(
      screen.getByLabelText("Additional information (optional)"),
    ).toHaveValue("Unsaved purpose");
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
