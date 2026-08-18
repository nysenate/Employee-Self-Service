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

function renderStep(route, onRouteChange = vi.fn()) {
  vi.stubGlobal(
    "fetch",
    vi.fn().mockImplementation(async (url) => ({
      ok: true,
      json: async () =>
        String(url).includes("/config")
          ? { result: { config: { googleApiKey: "test-key" } } }
          : { result: [{ methodOfTravel: "TRAIN", displayName: "Train" }] },
    })),
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
        pendingCounty={null}
        onCountySubmit={vi.fn()}
        onCountyCancel={vi.fn()}
        actions={<button>Next</button>}
      />
    </QueryClientProvider>,
  );
  return onRouteChange;
}

describe("Outbound step", () => {
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

    fireEvent.change(screen.getByLabelText("Travel date"), {
      target: { value: "2026-01-05" },
    });
    expect(changed).toHaveBeenCalledWith(
      expect.objectContaining({
        outboundLegs: [expect.objectContaining({ travelDate: "01/05/2026" })],
      }),
    );
    expect(screen.getByLabelText("Travel date")).toHaveAttribute(
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
