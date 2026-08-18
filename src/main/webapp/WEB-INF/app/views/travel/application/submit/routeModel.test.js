import { describe, expect, it } from "vitest";
import {
  addOutboundLeg,
  createEmptyRoute,
  findMissingOutboundCounty,
  initializeOutboundRoute,
  removeLastOutboundLeg,
  setOutboundAddressCounty,
} from "./routeModel";

const workAddress = {
  formattedAddressWithCounty: "Albany, NY 12207",
  zip5: "12207",
};

describe("outbound route model", () => {
  it("creates independent empty routes", () => {
    const first = createEmptyRoute();
    const second = createEmptyRoute();

    expect(first).toEqual({ outboundLegs: [], returnLegs: [] });
    expect(first).not.toBe(second);
  });

  it("initializes the first segment from the traveler's work address once", () => {
    const route = initializeOutboundRoute(
      { outboundLegs: [], returnLegs: [] },
      workAddress,
    );

    expect(route.outboundLegs[0].from).toEqual({
      address: workAddress,
      addressText: workAddress.formattedAddressWithCounty,
    });
    expect(initializeOutboundRoute(route, null)).toBe(route);
  });

  it("appends a segment with the preceding destination and mode", () => {
    const first = {
      from: { address: workAddress, addressText: "Albany" },
      to: { address: { zip5: "14202" }, addressText: "Buffalo" },
      travelDate: "01/05/2026",
      methodOfTravelDisplayName: "Other",
      methodOfTravelDescription: "Bicycle",
    };

    const route = addOutboundLeg({ outboundLegs: [first], returnLegs: [] });

    expect(route.outboundLegs[1]).toMatchObject({
      from: first.to,
      methodOfTravelDisplayName: "Other",
      methodOfTravelDescription: "Bicycle",
    });
    expect(route.outboundLegs[1].from).not.toBe(first.to);
  });

  it("retains at least one segment and updates counties immutably", () => {
    const route = initializeOutboundRoute(
      { outboundLegs: [], returnLegs: [] },
      workAddress,
    );
    expect(removeLastOutboundLeg(route)).toBe(route);

    const updated = setOutboundAddressCounty(route, 0, "from", "Albany");
    expect(updated.outboundLegs[0].from.address.county).toBe("Albany");
    expect(route.outboundLegs[0].from.address.county).toBeUndefined();
  });

  it("finds missing counties in route order", () => {
    const route = {
      outboundLegs: [
        {
          from: { address: { ...workAddress, county: "Albany" } },
          to: { address: workAddress, addressText: "Buffalo" },
        },
      ],
    };

    expect(findMissingOutboundCounty(route)).toEqual({
      index: 0,
      direction: "to",
      addressField: route.outboundLegs[0].to,
    });
    expect(
      findMissingOutboundCounty(
        setOutboundAddressCounty(route, 0, "to", "Erie"),
      ),
    ).toBeNull();
  });
});
