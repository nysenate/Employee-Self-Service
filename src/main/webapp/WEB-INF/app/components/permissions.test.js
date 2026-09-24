import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { act, fireEvent, render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { afterEach, describe, expect, it, vi } from "vitest";
import RequirePermission from "./RequirePermission";
import PermissionGate from "./PermissionGate";
import Navigation from "./Navigation";

const response = (isPermitted) => ({
  ok: true,
  json: async () => ({ result: { isPermitted } }),
});
afterEach(() => vi.unstubAllGlobals());

function setup(fetchMock, permission = "supply:requisition:approve") {
  vi.stubGlobal("fetch", fetchMock);
  const client = new QueryClient({
    defaultOptions: { queries: { retry: false, gcTime: 0 } },
  });
  const mounted = vi.fn();
  function Page() {
    mounted();
    return <div>Protected page</div>;
  }
  function App({ required }) {
    return (
      <QueryClientProvider client={client}>
        <MemoryRouter>
          <Navigation>
            <Navigation.Link to="/public">Public link</Navigation.Link>
            <Navigation.Section name="Restricted section" permission={required}>
              <Navigation.Link to="/restricted" permission={required}>
                Restricted link
              </Navigation.Link>
            </Navigation.Section>
          </Navigation>
          <PermissionGate permission={required}>
            <button>Protected action</button>
          </PermissionGate>
          <RequirePermission permission={required}>
            <Page />
          </RequirePermission>
        </MemoryRouter>
      </QueryClientProvider>
    );
  }
  const view = render(<App required={permission} />);
  return {
    client,
    mounted,
    changePermission: (required) => view.rerender(<App required={required} />),
  };
}
function expectHidden() {
  expect(screen.queryByText("Protected page")).not.toBeInTheDocument();
  expect(
    screen.queryByRole("button", { name: "Protected action" }),
  ).not.toBeInTheDocument();
  expect(
    screen.queryByRole("link", { name: "Restricted link" }),
  ).not.toBeInTheDocument();
}
describe("shared permission controls", () => {
  it("waits before mounting and shares one encoded permission request", async () => {
    let resolve;
    const fetchMock = vi.fn(
      () =>
        new Promise((done) => {
          resolve = done;
        }),
    );
    const { mounted } = setup(fetchMock);
    expectHidden();
    expect(mounted).not.toHaveBeenCalled();
    expect(screen.getByRole("link", { name: "Public link" })).toBeVisible();
    await act(async () => resolve(response(true)));
    expect(await screen.findByText("Protected page")).toBeVisible();
    expect(
      screen.getByRole("button", { name: "Protected action" }),
    ).toBeVisible();
    expect(screen.getByRole("link", { name: "Restricted link" })).toBeVisible();
    expect(fetchMock).toHaveBeenCalledTimes(1);
    expect(fetchMock.mock.calls[0][0]).toBe(
      "/api/v1/permissions/check?permission=supply%3Arequisition%3Aapprove",
    );
  });
  it("denies without mounting content", async () => {
    const { mounted } = setup(vi.fn().mockResolvedValue(response(false)));
    expect(await screen.findByRole("alert")).toHaveTextContent(
      "You do not have permission",
    );
    expectHidden();
    expect(mounted).not.toHaveBeenCalled();
  });
  it.each([undefined, null, "", "   "])(
    "fails closed for invalid required permission %s",
    (required) => {
      const fetchMock = vi.fn();
      const { changePermission } = setup(fetchMock, null);
      changePermission(required);
      expect(screen.getByRole("alert")).toHaveTextContent(
        "access could not be verified",
      );
      expect(screen.queryByText("Protected page")).not.toBeInTheDocument();
      expect(
        screen.queryByRole("button", { name: "Protected action" }),
      ).not.toBeInTheDocument();
      expect(fetchMock).not.toHaveBeenCalled();
    },
  );
  it.each([undefined, null, "true", 1])(
    "rejects malformed permission responses %s",
    async (value) => {
      const { mounted } = setup(vi.fn().mockResolvedValue(response(value)));
      expect(await screen.findByRole("alert")).toHaveTextContent(
        "access could not be verified",
      );
      expectHidden();
      expect(mounted).not.toHaveBeenCalled();
    },
  );
  it("contains network failures and allows retry", async () => {
    const fetchMock = vi
      .fn()
      .mockRejectedValueOnce(new Error("Offline"))
      .mockResolvedValue(response(true));
    setup(fetchMock);
    expect(await screen.findByRole("alert")).toHaveTextContent(
      "access could not be verified",
    );
    expectHidden();
    fireEvent.click(screen.getByRole("button", { name: "Try again" }));
    expect(await screen.findByText("Protected page")).toBeVisible();
    expect(fetchMock).toHaveBeenCalledTimes(2);
  });
  it.each([false, "error"])(
    "removes content after a refresh returns %s",
    async (result) => {
      const fetchMock = vi.fn().mockResolvedValue(response(true));
      const { client } = setup(fetchMock);
      await screen.findByText("Protected page");
      if (result === "error") fetchMock.mockRejectedValue(new Error("Offline"));
      else fetchMock.mockResolvedValue(response(false));
      await act(async () => {
        await client.invalidateQueries({ queryKey: ["permissions"] });
      });
      await screen.findByRole("alert");
      expectHidden();
    },
  );
  it("recovers when navigating from a denied page to an allowed page", async () => {
    const fetchMock = vi
      .fn()
      .mockResolvedValueOnce(response(false))
      .mockResolvedValue(response(true));
    const { changePermission } = setup(fetchMock);
    await screen.findByRole("alert");
    changePermission("core:pec-report-generation");
    expect(await screen.findByText("Protected page")).toBeVisible();
    expect(screen.queryByRole("alert")).not.toBeInTheDocument();
  });
});
