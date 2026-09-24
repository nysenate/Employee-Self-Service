import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { render, screen } from "@testing-library/react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { MemoryRouter, Route, Routes, useLocation } from "react-router-dom";
import TravelRouter from "./TravelRouter";
import { AsyncBadge } from "app/components/Badge";
import useCheckPermission from "app/hooks/useCheckPermission";
import { travelPermissions } from "./shared/travelPermissions";

vi.mock("app/hooks/useCheckPermission");
const permissionResult = vi.fn();
beforeEach(() => {
  vi.mocked(useCheckPermission).mockImplementation((permission) => {
    const result = permissionResult(permission);
    return {
      ...result,
      isChecking: result.isPending,
      isAllowed: result.isSuccess && result.data?.isPermitted === true,
    };
  });
  vi.mocked(AsyncBadge).mockClear();
  permissionResult.mockReturnValue({
    data: { isPermitted: true },
    isPending: false,
    isSuccess: true,
  });
});

vi.mock("app/components/AppLayout", async () => {
  const { Outlet } = await import("react-router-dom");
  return {
    default: ({ children }) => (
      <>
        {children}
        <Outlet />
      </>
    ),
  };
});
vi.mock("app/components/Badge", () => ({ AsyncBadge: vi.fn(() => null) }));
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

vi.mock("app/views/travel/application/submit", () => ({
  default: () => <RouteProbe label="new" />,
}));

vi.mock("app/views/travel/application/drafts", () => ({
  default: () => <RouteProbe label="drafts" />,
}));

vi.mock("app/views/travel/application/edit/EditTravelApplication", () => ({
  default: () => <RouteProbe label="edit" />,
}));

vi.mock("app/views/travel/reviewer/history", () => ({
  default: () => <RouteProbe label="review-history" />,
}));

const protectedRoutes = [
  ["/travel/applications/new", "submit"],
  ["/travel/applications/new/12", "submit"],
  ["/travel/applications/drafts", "submit"],
  ["/travel/applications", "submit"],
  ["/travel/applications/42/resubmit", "submit"],
  ["/travel/application/42", "submit"],
  ["/travel/applications/42/edit", "edit"],
  ["/travel/manage/queue", "review"],
  ["/travel/manage/review", "review"],
  ["/travel/manage/review-history", "reviewHistory"],
];

describe("Travel page authorization", () => {
  it.each(protectedRoutes)(
    "blocks direct access to %s without its permission",
    (path, permission) => {
      permissionResult.mockImplementation((requested) => ({
        data: { isPermitted: requested !== travelPermissions[permission] },
        isPending: false,
        isSuccess: true,
      }));
      renderRouter(path);
      expect(screen.getByRole("alert")).toHaveTextContent(
        "You do not have permission",
      );
      expect(screen.queryByTestId("route")).not.toBeInTheDocument();
    },
  );

  it.each(protectedRoutes)(
    "allows direct access to %s with its permission",
    (path) => {
      renderRouter(path);
      expect(screen.getByTestId("route")).toBeVisible();
    },
  );

  it.each([
    ["pending", { isPending: true, isSuccess: false }],
    ["failed", { isPending: false, isSuccess: false }],
    ["missing result", { isPending: false, isSuccess: true }],
    [
      "malformed result",
      { isPending: false, isSuccess: true, data: { isPermitted: "true" } },
    ],
    [
      "failed refresh",
      { isPending: false, isSuccess: false, data: { isPermitted: true } },
    ],
  ])(
    "does not mount pages or show restricted links for a %s check",
    (_, state) => {
      permissionResult.mockReturnValue(state);
      renderRouter("/travel/applications/new");
      expect(screen.queryByTestId("route")).not.toBeInTheDocument();
      expect(screen.queryByRole("link")).not.toBeInTheDocument();
    },
  );

  it("hides management and its badge for regular employees", () => {
    permissionResult.mockImplementation((permission) => ({
      data: { isPermitted: permission === travelPermissions.submit },
      isPending: false,
      isSuccess: true,
    }));
    renderRouter("/travel/applications");
    expect(screen.getAllByRole("link")).toHaveLength(3);
    expect(screen.queryByText("Manage Travel")).not.toBeInTheDocument();
    expect(AsyncBadge).not.toHaveBeenCalled();
  });

  it("checks review and history links separately within management", () => {
    permissionResult.mockImplementation((permission) => ({
      data: {
        isPermitted: [
          travelPermissions.manage,
          travelPermissions.reviewHistory,
        ].includes(permission),
      },
      isPending: false,
      isSuccess: true,
    }));
    renderRouter("/travel/manage/review-history");
    expect(screen.getAllByRole("link")).toHaveLength(1);
    expect(screen.getByRole("link", { name: "Review History" })).toBeVisible();
    expect(screen.queryByText("My Travel")).not.toBeInTheDocument();
  });
});
