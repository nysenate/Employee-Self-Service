import React from "react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import {
  act,
  fireEvent,
  render,
  renderHook,
  screen,
  waitFor,
} from "@testing-library/react";
import {
  focusManager,
  QueryClient,
  QueryClientProvider,
} from "@tanstack/react-query";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import EssLayout from "app/views/EssLayout";
import SubmitApplication from "app/views/travel/application/submit";
import {
  AUTHED_USER_QUERY_KEY,
  useAuthedUserNoRedirect,
} from "app/hooks/useRequireAuthedUser";
import { travelQueryKeys } from "app/views/travel/shared/hooks/travelQueryKeys";

let replace;
beforeEach(() => {
  replace = vi.fn();
  const browserWindow = window;
  vi.stubGlobal(
    "window",
    new Proxy(browserWindow, {
      get: (target, property) =>
        property === "location"
          ? {
              origin: browserWindow.location.origin,
              href: browserWindow.location.href,
              replace,
            }
          : Reflect.get(target, property, target),
    }),
  );
});

afterEach(() => {
  focusManager.setFocused(undefined);
  vi.restoreAllMocks();
  vi.unstubAllGlobals();
});

const user = { employeeId: 42, firstName: "Jamie", lastName: "Rivera" };
const eventType = { name: "Forum", displayName: "Forum" };
const response = (result) =>
  Promise.resolve({ ok: true, json: async () => ({ result }) });

function renderEditor(getUser) {
  const client = new QueryClient({
    defaultOptions: { queries: { retryDelay: 0, throwOnError: true } },
  });
  client.setQueryData(travelQueryKeys.newDraft(), {
    traveler: { fullName: "Jamie Rivera" },
    amendment: { purposeOfTravel: { eventType, additionalPurpose: "" } },
  });
  const fetchMock = vi.fn((url) => {
    if (url.endsWith("/employees/me")) return getUser();
    if (url.endsWith("/config"))
      return response({ config: { runtimeLevel: "prod" } });
    if (url.endsWith("/travel/event-types")) return response([eventType]);
    if (url.startsWith("/api/v1/timeout/ping")) {
      return Promise.resolve({
        ok: true,
        json: async () => ({ success: true, remainingInactivity: 300 }),
      });
    }
    throw new Error(`Unexpected request: ${url}`);
  });
  vi.stubGlobal("fetch", fetchMock);
  render(
    <QueryClientProvider client={client}>
      <MemoryRouter initialEntries={["/travel/applications/new"]}>
        <Routes>
          <Route element={<EssLayout />}>
            <Route
              path="/travel/applications/new"
              element={<SubmitApplication />}
            />
          </Route>
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>,
  );
  return { client, fetchMock };
}

async function focusWithStaleUser(client) {
  await act(async () => {
    client.setQueryData(AUTHED_USER_QUERY_KEY, user, {
      updatedAt: Date.now() - 31000,
    });
    focusManager.setFocused(false);
    focusManager.setFocused(true);
  });
}

describe("authenticated-user verification", () => {
  it("leaves an unauthenticated login-page query local without retrying or redirecting", async () => {
    const client = new QueryClient({
      defaultOptions: { queries: { retryDelay: 0, throwOnError: true } },
    });
    vi.stubGlobal(
      "fetch",
      vi.fn(async () => ({
        ok: false,
        status: 401,
        statusText: "Unauthorized",
        json: async () => ({ status: { authorized: false } }),
      })),
    );
    const { result } = renderHook(() => useAuthedUserNoRedirect(), {
      wrapper: ({ children }) => (
        <QueryClientProvider client={client}>{children}</QueryClientProvider>
      ),
    });
    await waitFor(() => expect(result.current.isError).toBe(true));
    expect(fetch).toHaveBeenCalledTimes(1);
    expect(replace).not.toHaveBeenCalled();
  });

  it.each(["network failure", 408, 429, 503])(
    "%s on a stale-user focus refresh preserves unsaved Travel edits through retry",
    async (kind) => {
      let getUser = () => response(user);
      const { client, fetchMock } = renderEditor(() => getUser());
      const loggedError = vi
        .spyOn(console, "error")
        .mockImplementation(() => {});
      await screen.findByRole("option", { name: "Forum" });
      const input = screen.getByLabelText("Additional information (optional)");
      fireEvent.change(input, { target: { value: "Unsaved trip details" } });
      getUser =
        kind === "network failure"
          ? () => Promise.reject(new TypeError("Failed to fetch"))
          : () =>
              Promise.resolve({
                ok: false,
                status: kind,
                statusText: "Temporary error",
                json: async () => ({ message: "Temporary outage" }),
              });

      await focusWithStaleUser(client);
      expect(await screen.findByRole("alert")).toHaveTextContent(
        "Trouble connecting to ESS",
      );
      expect(client.getQueryData(AUTHED_USER_QUERY_KEY)).toEqual(user);
      expect(
        fetchMock.mock.calls.filter(([url]) => url.endsWith("/employees/me")),
      ).toHaveLength(4);
      expect(screen.getByLabelText("Additional information (optional)")).toBe(
        input,
      );
      expect(input).toHaveValue("Unsaved trip details");
      expect(screen.getByRole("navigation", { name: "Main" })).toBeVisible();
      expect(replace).not.toHaveBeenCalled();

      let resolveUser;
      getUser = () =>
        new Promise((resolve) => {
          resolveUser = resolve;
        });
      const retry = screen.getByRole("button", { name: "Reconnect to ESS" });
      fireEvent.click(retry);
      await waitFor(() => expect(retry).toBeDisabled());
      expect(screen.getByLabelText("Additional information (optional)")).toBe(
        input,
      );
      expect(input).toHaveValue("Unsaved trip details");
      await act(async () => resolveUser(await response(user)));
      await waitFor(() =>
        expect(screen.queryByRole("alert")).not.toBeInTheDocument(),
      );
      expect(screen.getByLabelText("Additional information (optional)")).toBe(
        input,
      );
      expect(input).toHaveValue("Unsaved trip details");
      expect(replace).not.toHaveBeenCalled();
      loggedError.mockRestore();
    },
  );

  it("keeps the editor mounted during a successful background verification", async () => {
    let getUser = () => response(user);
    const { client } = renderEditor(() => getUser());
    await screen.findByRole("option", { name: "Forum" });
    const input = screen.getByLabelText("Additional information (optional)");
    fireEvent.change(input, { target: { value: "Unsaved trip details" } });
    let resolveUser;
    getUser = () =>
      new Promise((resolve) => {
        resolveUser = resolve;
      });
    await focusWithStaleUser(client);
    expect(client.getQueryState(AUTHED_USER_QUERY_KEY).fetchStatus).toBe(
      "fetching",
    );
    expect(input).toHaveValue("Unsaved trip details");
    expect(screen.getByLabelText("Additional information (optional)")).toBe(
      input,
    );
    await act(async () => resolveUser(await response(user)));
    expect(screen.getByLabelText("Additional information (optional)")).toBe(
      input,
    );
    expect(input).toHaveValue("Unsaved trip details");
  });

  it("does not mount protected content when initial verification fails", async () => {
    vi.spyOn(console, "error").mockImplementation(() => {});
    renderEditor(() => Promise.reject(new TypeError("Failed to fetch")));
    expect(
      await screen.findByRole("heading", { name: "Something went wrong" }),
    ).toBeVisible();
    expect(
      screen.queryByLabelText("Additional information (optional)"),
    ).not.toBeInTheDocument();
  });

  it("keeps the heartbeat running while a transient verification failure is displayed", async () => {
    const intervals = vi.spyOn(globalThis, "setInterval");
    const clearedIntervals = vi.spyOn(globalThis, "clearInterval");
    let getUser = () => response(user);
    const { client, fetchMock } = renderEditor(() => getUser());
    await screen.findByRole("option", { name: "Forum" });
    const pingIndex = intervals.mock.calls.findIndex(
      ([, delay]) => delay === 15000,
    );
    const pingCallback = intervals.mock.calls[pingIndex][0];
    const pingInterval = intervals.mock.results[pingIndex].value;
    getUser = () => Promise.reject(new TypeError("Failed to fetch"));
    await focusWithStaleUser(client);
    expect(await screen.findByRole("alert")).toHaveTextContent(
      "Trouble connecting to ESS",
    );
    expect(clearedIntervals).not.toHaveBeenCalledWith(pingInterval);
    await act(async () => pingCallback());
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/v1/timeout/ping?active=true",
      expect.objectContaining({ method: "POST" }),
    );
    expect(
      screen.getByLabelText("Additional information (optional)"),
    ).toBeVisible();
    expect(replace).not.toHaveBeenCalled();
  });

  it.each(["JSON", "unreadable body"])(
    "clears cached user data and redirects immediately for a background 401 with %s",
    async (body) => {
      vi.spyOn(console, "error").mockImplementation(() => {});
      let getUser = () => response(user);
      const { client, fetchMock } = renderEditor(() => getUser());
      client.setQueryData(["private-test-data"], { secret: "cached" });
      await screen.findByRole("option", { name: "Forum" });
      fireEvent.change(
        screen.getByLabelText("Additional information (optional)"),
        {
          target: { value: "Unsaved trip details" },
        },
      );
      replace.mockImplementation(() => {
        const unload = new Event("beforeunload", { cancelable: true });
        window.dispatchEvent(unload);
        expect(unload.defaultPrevented).toBe(false);
      });
      getUser = () =>
        Promise.resolve({
          ok: false,
          status: 401,
          statusText: "Unauthorized",
          json:
            body === "JSON"
              ? async () => ({ status: { authorized: false } })
              : async () => {
                  throw new SyntaxError("Invalid JSON");
                },
        });
      await focusWithStaleUser(client);
      await waitFor(() => expect(replace).toHaveBeenCalledWith("/logout"));
      expect(client.getQueryData(AUTHED_USER_QUERY_KEY)).toBeUndefined();
      expect(client.getQueryData(["private-test-data"])).toBeUndefined();
      expect(
        fetchMock.mock.calls.filter(([url]) => url.endsWith("/employees/me")),
      ).toHaveLength(2);
      expect(
        screen.queryByLabelText("Additional information (optional)"),
      ).not.toBeInTheDocument();
      expect(
        screen.queryByText("Trouble connecting to ESS"),
      ).not.toBeInTheDocument();
    },
  );

  it("does not retry or retain protected content on a 403 refresh", async () => {
    vi.spyOn(console, "error").mockImplementation(() => {});
    let getUser = () => response(user);
    const { client, fetchMock } = renderEditor(() => getUser());
    await screen.findByRole("option", { name: "Forum" });
    getUser = () =>
      Promise.resolve({
        ok: false,
        status: 403,
        statusText: "Forbidden",
        json: async () => ({ status: { authorized: false } }),
      });
    await focusWithStaleUser(client);
    expect(
      await screen.findByRole("heading", { name: "Something went wrong" }),
    ).toBeVisible();
    expect(
      screen.queryByLabelText("Additional information (optional)"),
    ).not.toBeInTheDocument();
    expect(
      fetchMock.mock.calls.filter(([url]) => url.endsWith("/employees/me")),
    ).toHaveLength(2);
    expect(replace).not.toHaveBeenCalled();
  });
});
