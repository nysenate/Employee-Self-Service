import NotificationProvider from "app/components/NotificationProvider";
import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { afterEach, describe, expect, it, vi } from "vitest";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import TravelRouter from "app/views/travel/TravelRouter";

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

afterEach(() => {
  vi.unstubAllGlobals();
  delete window.google;
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

function editDraft() {
  const albany = address("Albany, NY 12207", "12207", "Albany");
  const buffalo = address("Buffalo, NY 14202", "14202", "Erie");
  return {
    id: 0,
    traveler: {
      fullName: "Traveler One",
      jobTitle: "Analyst",
      empWorkLocation: { address: albany },
    },
    amendment: {
      purposeOfTravel: {
        eventType: { name: "Forum", displayName: "Forum" },
        summary: "Forum",
        additionalPurpose: "Original details",
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
            travelDate: "08/11/2026",
            methodOfTravelDisplayName: "Train",
          },
        ],
      },
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
      attachments: [],
    },
  };
}

function application(isDisapproved) {
  return {
    id: 42,
    submittedDateTime: "2024-07-04T09:30:00",
    traveler: { fullName: "Traveler One", jobTitle: "Analyst" },
    activeAmendment: {
      ...editDraft().amendment,
      purposeOfTravel: {
        summary: isDisapproved ? "Original details" : "Updated details",
      },
      startDate: "2026-08-10",
      endDate: "2026-08-11",
    },
    status: isDisapproved
      ? {
          name: "DISAPPROVED",
          label: "Disapproved",
          isDisapproved: true,
          note: "Clarify this trip",
        }
      : { name: "DEPARTMENT_HEAD", label: "Pending", isDisapproved: false },
  };
}

function response(body) {
  return Promise.resolve({
    ok: true,
    status: 200,
    statusText: "",
    json: async () => body,
  });
}

describe("history to resubmission completion", () => {
  it("follows the old notification URL into the editor and confirms completion", async () => {
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
    let committed = false;
    const fetchMock = vi.fn((url, options = {}) => {
      if (url === "/api/v1/permissions/check?permission=travel%3Asubmit-app") {
        return response({ result: { isPermitted: true } });
      }
      if (url === "/api/v1/travel/applications/42") {
        return response({ result: application(!committed) });
      }
      if (url.startsWith("/api/v1/travel/applications?")) {
        return response({
          result: committed
            ? []
            : [
                {
                  id: 42,
                  startDate: "2026-08-10",
                  travelerName: "Traveler One",
                  destinationSummary: "Buffalo",
                  totalAllowance: 0,
                  status: { name: "DISAPPROVED", isDisapproved: true },
                },
              ],
          total: committed ? 0 : 1,
        });
      }
      if (url === "/api/v1/travel/application/edit/42") {
        return response({ result: editDraft() });
      }
      if (url.endsWith("/travel/event-types")) {
        return response({ result: [{ name: "Forum", displayName: "Forum" }] });
      }
      if (url.endsWith("/travel/mode-of-transportation")) {
        return response({
          result: [{ methodOfTravel: "TRAIN", displayName: "Train" }],
        });
      }
      if (url.endsWith("/config")) {
        return response({ result: { config: { googleApiKey: "test" } } });
      }
      if (url.endsWith("/travel/application/edit/resubmit/42")) {
        committed = true;
        return response({ success: true });
      }
      return response({ result: [] });
    });
    vi.stubGlobal("fetch", fetchMock);
    const queryClient = new QueryClient({
      defaultOptions: { queries: { retry: false } },
    });
    render(
      <NotificationProvider>
        <QueryClientProvider client={queryClient}>
          <MemoryRouter initialEntries={["/travel/application/42"]}>
            <Routes>
              <Route path="/travel/*" element={<TravelRouter />} />
            </Routes>
          </MemoryRouter>
        </QueryClientProvider>
      </NotificationProvider>,
    );

    expect(await screen.findByText("Clarify this trip")).toBeVisible();
    fireEvent.click(screen.getByRole("button", { name: "Edit and Resubmit" }));
    await screen.findByRole("heading", { name: "Purpose" });
    fireEvent.change(
      screen.getByLabelText("Additional information (optional)"),
      {
        target: { value: "Updated details" },
      },
    );
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

    expect(
      await screen.findByRole("dialog", { name: "Application resubmitted" }),
    ).toBeVisible();
    expect(
      screen.queryByRole("dialog", { name: "Travel application details" }),
    ).not.toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: "Go back to ESS" }),
    ).toBeVisible();
    expect(
      screen.getByRole("button", { name: "Log out of ESS" }),
    ).toBeVisible();
    expect(
      screen.queryByRole("button", { name: "Edit and Resubmit" }),
    ).not.toBeInTheDocument();
    expect(
      fetchMock.mock.calls.filter(([url]) =>
        url.endsWith("/travel/application/edit/resubmit/42"),
      ),
    ).toHaveLength(1);
    expect(
      fetchMock.mock.calls.some(([url]) =>
        url.endsWith("/travel/drafts/submit"),
      ),
    ).toBe(false);
  });
});
