import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { fireEvent, render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import RouteStep from "./RouteStep";
import {
  addOutboundLeg,
  removeLastOutboundLeg,
  updateOutboundLeg,
} from "../routeModel";

const ny = {
  zip5: "12207",
  country: "United States",
  state: "NY",
  formattedAddressWithCounty: "Albany, NY 12207",
};

function renderStep(route, onRouteChange = vi.fn(), props = {}) {
  vi.stubGlobal(
    "fetch",
    vi.fn().mockImplementation(async (url) => {
      const body = String(url).includes("/config")
        ? { result: { config: { googleApiKey: "test-key" } } }
        : { result: [{ methodOfTravel: "TRAIN", displayName: "Train" }] };
      return {
        ok: true,
        json: async () => body,
        text: async () => JSON.stringify(body),
      };
    }),
  );
  render(
    <QueryClientProvider
      client={
        new QueryClient({ defaultOptions: { queries: { retry: false } } })
      }
    >
      <RouteStep
        title="Outbound"
        description="Enter the outbound route."
        legs={route.outboundLegs}
        errors={{}}
        segmentIdPrefix="outbound"
        addSegmentLabel="Add outbound segment"
        onAddSegment={() => onRouteChange(addOutboundLeg(route))}
        onRemoveLastSegment={() => onRouteChange(removeLastOutboundLeg(route))}
        onUpdateSegment={(index, changes) =>
          onRouteChange(updateOutboundLeg(route, index, changes))
        }
        onDestinationSelect={vi.fn()}
        firstLegQualifier={{
          label: "Departing before 7:00 AM",
          checked: Boolean(route.firstLegQualifiesForBreakfast),
          onChange: (checked) =>
            onRouteChange({
              ...route,
              firstLegQualifiesForBreakfast: checked,
            }),
        }}
        actions={<button>Next</button>}
        {...props}
      />
    </QueryClientProvider>,
  );
  return onRouteChange;
}

describe("Outbound step", () => {
  it("limits later segments to the preceding date while allowing same-day travel", () => {
    renderStep({
      outboundLegs: [
        { travelDate: "09/25/2026" },
        { travelDate: "09/25/2026" },
        { travelDate: "09/24/2026" },
      ],
    });
    const dates = screen.getAllByLabelText("Outbound date");
    expect(dates[0]).not.toHaveAttribute("min");
    expect(dates[1].validity.rangeUnderflow).toBe(false);
    expect(dates[2].validity.rangeUnderflow).toBe(true);
    expect(dates[2]).toHaveAttribute("min", "2026-09-25");
  });

  it("shows the trip start on Return and limits its first segment to the final outbound date", () => {
    renderStep({}, vi.fn(), {
      title: "Return",
      legs: [{ travelDate: "09/27/2026" }, { travelDate: "09/28/2026" }],
      travelStartDate: "09/25/2026",
      precedingTravelDate: "09/26/2026",
    });
    const dates = screen.getAllByLabelText("Return date");
    expect(dates[0]).toHaveAccessibleDescription(
      "Return on or after Sep 26, 2026.",
    );
    expect(dates[1]).toHaveAccessibleDescription("On or after Sep 27, 2026.");
    expect(dates[0]).toHaveAttribute("min", "2026-09-26");
    expect(dates[1]).toHaveAttribute("min", "2026-09-27");
  });

  it("omits limits and the reminder when the source dates are invalid", () => {
    renderStep({}, vi.fn(), {
      legs: [{ travelDate: "" }, { travelDate: "" }],
      travelStartDate: "02/30/2026",
      precedingTravelDate: "invalid",
    });
    expect(screen.queryByText(/Your trip begins/)).not.toBeInTheDocument();
    screen.getAllByLabelText("Outbound date").forEach((date) => {
      expect(date).not.toHaveAttribute("min");
    });
  });

  it("adds and removes only the final segment while propagating origin and mode", () => {
    const route = {
      outboundLegs: [
        {
          from: { address: ny, addressText: "Albany" },
          to: { address: ny, addressText: "Buffalo" },
          travelDate: "",
          methodOfTravelDisplayName: "Train",
          methodOfTravelDescription: "",
        },
      ],
    };
    const changed = renderStep(route);
    fireEvent.click(
      screen.getByRole("button", { name: /Add outbound segment/ }),
    );
    const added = changed.mock.calls[0][0];
    expect(added.outboundLegs[1].from.addressText).toBe("Buffalo");
    expect(added.outboundLegs[1].methodOfTravelDisplayName).toBe("Train");
  });

  it("retains the early departure selection", () => {
    const route = {
      outboundLegs: [
        {
          from: { address: ny, addressText: "Albany" },
          to: { address: null, addressText: "" },
          travelDate: "",
          methodOfTravelDisplayName: "",
          methodOfTravelDescription: "",
        },
      ],
    };
    const changed = renderStep(route);
    fireEvent.click(screen.getByLabelText("Departing before 7:00 AM"));
    expect(changed).toHaveBeenCalledWith(
      expect.objectContaining({ firstLegQualifiesForBreakfast: true }),
    );
  });

  it("provides a date picker and stores its date as MM/DD/YYYY", async () => {
    const route = {
      outboundLegs: [
        {
          from: { address: ny, addressText: "Albany" },
          to: { address: null, addressText: "" },
          travelDate: "",
          methodOfTravelDisplayName: "",
          methodOfTravelDescription: "",
        },
      ],
    };
    const changed = renderStep(route);

    fireEvent.change(screen.getByLabelText("Outbound date"), {
      target: { value: "2026-01-05" },
    });
    expect(changed).toHaveBeenCalledWith(
      expect.objectContaining({
        outboundLegs: [expect.objectContaining({ travelDate: "01/05/2026" })],
      }),
    );
    expect(screen.getByLabelText("Outbound date")).toHaveAttribute(
      "type",
      "date",
    );
    expect(screen.getByLabelText("Mode of transportation")).toHaveValue("");
    expect(
      screen.queryByRole("option", { name: "Select a mode" }),
    ).not.toBeInTheDocument();
    expect(await screen.findByRole("option", { name: "Train" })).toBeVisible();
  });
});
