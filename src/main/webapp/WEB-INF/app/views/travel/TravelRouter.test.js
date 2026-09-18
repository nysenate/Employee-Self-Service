import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { render, screen } from "@testing-library/react";
import { afterEach, describe, expect, it, vi } from "vitest";
import { MemoryRouter, Route, Routes, useLocation } from "react-router-dom";
import TravelRouter from "./TravelRouter";

vi.mock("app/components/AppLayout", async () => {
  const { Outlet } = await import("react-router-dom");
  return { default: () => <Outlet /> };
});
vi.mock("app/components/Navigation", () => ({
  default: Object.assign(({ children }) => <nav>{children}</nav>, {
    Title: ({ children }) => <div>{children}</div>,
    Section: ({ children }) => <div>{children}</div>,
    Link: ({ children }) => <div>{children}</div>,
  }),
}));
vi.mock("app/components/Badge", () => ({ AsyncBadge: () => null }));
vi.mock("app/views/travel/application/history", () => ({
  default: () => <RouteProbe label="history" />,
}));
vi.mock("app/views/travel/reviewer/queue/ReviewQueuePage", () => ({
  default: () => <RouteProbe label="queue" />,
}));
vi.mock("app/views/travel/application/resubmit", () => ({
  default: () => <RouteProbe label="resubmit" />,
}));

afterEach(() => vi.unstubAllGlobals());

function RouteProbe({ label }) {
  const location = useLocation();
  return (
    <output data-testid="route">
      {label}:{location.pathname}
      {location.search}
    </output>
  );
}

function renderRouter(initialEntry) {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[initialEntry]}>
        <Routes>
          <Route path="/travel/*" element={<TravelRouter />} />
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

describe("TravelRouter compatibility routes", () => {
  it("replace-redirects the application email URL to independently loaded history details", async () => {
    renderRouter("/travel/application/42");

    expect(await screen.findByTestId("route")).toHaveTextContent(
      "history:/travel/applications?appId=42",
    );
  });

  it("replace-redirects the pending-review email URL to the existing queue", async () => {
    renderRouter("/travel/manage/review");

    expect(await screen.findByTestId("route")).toHaveTextContent(
      "queue:/travel/manage/queue",
    );
  });

  it("does not interpret the static legacy edit path as an application ID", async () => {
    const fetchMock = vi.fn();
    vi.stubGlobal("fetch", fetchMock);
    renderRouter("/travel/application/edit?role=TRAVEL_ADMIN");

    expect(
      await screen.findByRole("heading", { name: "Page not found" }),
    ).toBeVisible();
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("handles malformed notification IDs without creating or loading a draft", async () => {
    const fetchMock = vi.fn();
    vi.stubGlobal("fetch", fetchMock);
    renderRouter("/travel/application/42oops");

    expect(
      await screen.findByRole("heading", { name: "Page not found" }),
    ).toBeVisible();
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("keeps the canonical history, queue, and resubmission routes available", async () => {
    const first = renderRouter("/travel/applications");
    expect(await screen.findByTestId("route")).toHaveTextContent(
      "history:/travel/applications",
    );
    first.unmount();

    const second = renderRouter("/travel/manage/queue");
    expect(await screen.findByTestId("route")).toHaveTextContent(
      "queue:/travel/manage/queue",
    );
    second.unmount();

    renderRouter("/travel/applications/42/resubmit");
    expect(await screen.findByTestId("route")).toHaveTextContent(
      "resubmit:/travel/applications/42/resubmit",
    );
  });
});
