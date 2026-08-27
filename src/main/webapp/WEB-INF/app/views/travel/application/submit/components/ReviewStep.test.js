import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import ReviewStep, { buildDirectionsRequest } from "./ReviewStep";

const albany = {
  formattedAddressWithCounty: "Albany, NY 12207, Albany County",
};
const syracuse = {
  formattedAddressWithCounty: "Syracuse, NY 13202, Onondaga County",
};
const buffalo = {
  formattedAddressWithCounty: "Buffalo, NY 14202, Erie County",
};

function reviewDraft() {
  return {
    traveler: {
      nid: "1234",
      fullName: "Taylor Traveler",
      jobTitle: "Analyst",
      workPhone: "555-0100",
      respCtr: {
        agencyCode: "04210",
        respCenterHead: { name: "Department Head" },
      },
      empWorkLocation: { address: albany },
    },
    amendment: {
      startDate: "2026-08-10",
      endDate: "2026-08-14",
      purposeOfTravel: { summary: "Forum: Modern Government" },
      route: {
        origin: albany,
        destinations: [
          { id: 1, address: syracuse },
          { id: 2, address: buffalo },
        ],
        outboundLegs: [
          {
            from: { address: albany },
            to: { address: syracuse },
            methodOfTravelDisplayName: "Train",
          },
          {
            from: { address: syracuse },
            to: { address: buffalo },
            methodOfTravelDisplayName: "Car",
          },
        ],
        returnLegs: [{ from: { address: buffalo }, to: { address: albany } }],
      },
      attachments: [{ filename: "file-id", originalName: "agenda.pdf" }],
      mileagePerDiems: { totalMileage: 100, allPerDiems: [] },
      mealPerDiems: { allMealPerDiems: [] },
      lodgingPerDiems: { allLodgingPerDiems: [] },
      transportationAllowance: 25,
      mealAllowance: 10,
      lodgingAllowance: 100,
      tollsAndParkingAllowance: 5,
      alternateTransportationAllowance: 0,
      registrationAllowance: 20,
      totalAllowance: 160,
    },
  };
}

function renderStep(props = {}) {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  return render(
    <QueryClientProvider client={queryClient}>
      <ReviewStep
        draft={reviewDraft()}
        actions={<button>Back</button>}
        loadMaps={() => Promise.resolve()}
        {...props}
      />
    </QueryClientProvider>,
  );
}

function deferredPromise() {
  let resolve;
  const promise = new Promise((resolvePromise) => {
    resolve = resolvePromise;
  });
  return { promise, resolve };
}

describe("ReviewStep", () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    vi.stubGlobal(
      "fetch",
      vi.fn(async (url) => ({
        ok: true,
        json: async () =>
          String(url).includes("mode-of-transportation")
            ? { result: [{ methodOfTravel: "CAR", displayName: "Car" }] }
            : String(url).includes("/config")
              ? { result: { config: { googleApiKey: "test-key" } } }
              : { result: {} },
        blob: async () => new Blob(["document"]),
      })),
    );
    vi.stubGlobal(
      "open",
      vi.fn(() => ({ close: vi.fn() })),
    );
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
    vi.stubGlobal("URL", {
      createObjectURL: vi.fn(() => "blob:document"),
      revokeObjectURL: vi.fn(),
    });
  });

  it("shows the legacy summary, outbound map, attachments, and actions", async () => {
    const mapLoad = deferredPromise();
    renderStep({
      loadMaps: () => mapLoad.promise,
    });

    expect(screen.getByText(/Taylor Traveler/)).toBeVisible();
    expect(screen.getByText("Forum: Modern Government")).toBeVisible();
    expect(screen.getByRole("link", { name: /agenda.pdf/ })).toHaveAttribute(
      "target",
      "_blank",
    );
    expect(screen.getByRole("button", { name: "Back" })).toBeEnabled();
    expect(screen.getByRole("status")).toHaveTextContent("Loading route map");

    mapLoad.resolve();
    await waitFor(() =>
      expect(screen.getByLabelText("Outbound travel route map")).toBeVisible(),
    );
  });

  it("opens a supporting document in a separate window", async () => {
    const openedWindow = { close: vi.fn(), location: "", opener: window };
    open.mockReturnValue(openedWindow);
    renderStep();

    fireEvent.click(await screen.findByRole("link", { name: /agenda.pdf/ }));

    await waitFor(() => expect(openedWindow.location).toBe("blob:document"));
    expect(openedWindow.opener).toBeNull();
    expect(fetch).toHaveBeenCalledWith(
      "/api/v1/travel/applications/attachment/file-id",
      { cache: "no-store" },
    );
  });

  it("uses every outbound destination in order and excludes return legs", () => {
    expect(buildDirectionsRequest(reviewDraft())).toEqual({
      origin: albany.formattedAddressWithCounty,
      destination: buffalo.formattedAddressWithCounty,
      waypoints: [{ location: syracuse.formattedAddressWithCounty }],
      travelMode: "DRIVING",
    });
  });

  it("keeps review and actions available when the map fails", async () => {
    renderStep({ loadMaps: () => Promise.reject(new Error("unavailable")) });

    expect(
      await screen.findByRole("alert", { name: "Route map unavailable" }),
    ).toHaveTextContent("could not be displayed");
    expect(screen.getByText(/Taylor Traveler/)).toBeVisible();
    expect(screen.getByRole("button", { name: "Back" })).toBeEnabled();
  });

  it("reports a supporting document failure without leaving review", async () => {
    fetch.mockImplementation(async (url) => ({
      ok: !String(url).includes("attachment"),
      json: async () =>
        String(url).includes("/config")
          ? { result: { config: { googleApiKey: "test-key" } } }
          : { result: [] },
      blob: async () => new Blob(["document"]),
    }));
    renderStep();

    fireEvent.click(await screen.findByRole("link", { name: /agenda.pdf/ }));

    expect(
      await screen.findByRole("alert", { name: "Document unavailable" }),
    ).toHaveTextContent("could not be opened");
    expect(screen.getByText(/Taylor Traveler/)).toBeVisible();
  });
});
