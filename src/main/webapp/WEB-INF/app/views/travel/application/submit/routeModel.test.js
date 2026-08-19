import { describe, expect, it } from "vitest";
import {
  addOutboundLeg,
  addReturnLeg,
  createEmptyRoute,
  findMissingRouteCounty,
  initializeReturnRoute,
  findMissingOutboundCounty,
  initializeOutboundRoute,
  removeLastOutboundLeg,
  setOutboundAddressCounty,
  setRouteAddressCounty,
  toEditableRoute,
  toRouteDto,
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

describe("return route model", () => {
  const outbound = (mode, description = "") => ({
    from: {
      address: { ...workAddress, county: "Albany" },
      addressText: "Albany",
    },
    to: {
      address: { ...workAddress, county: "Erie" },
      addressText: "Buffalo",
    },
    travelDate: "08/10/2026",
    methodOfTravelDisplayName: mode,
    methodOfTravelDescription: description,
  });

  it("initializes once from the outbound endpoints and infers one mode", () => {
    const route = initializeReturnRoute({
      outboundLegs: [outbound("Other", "Bicycle")],
      returnLegs: [],
    });
    expect(route.returnLegs[0]).toMatchObject({
      from: { addressText: "Buffalo" },
      to: { addressText: "Albany" },
      methodOfTravelDisplayName: "Other",
      methodOfTravelDescription: "Bicycle",
    });
    expect(initializeReturnRoute(route)).toBe(route);
  });

  it("does not infer a return mode when outbound modes differ", () => {
    const route = initializeReturnRoute({
      outboundLegs: [outbound("Train"), outbound("Car")],
      returnLegs: [],
    });
    expect(route.returnLegs[0].methodOfTravelDisplayName).toBe("");
  });

  it("adds a return segment from only the final segment and clears stale Other text", () => {
    const route = {
      outboundLegs: [outbound("Train")],
      returnLegs: [
        {
          ...outbound("Train"),
          methodOfTravelDescription: "stale bicycle",
        },
      ],
      lastLegQualifiesForDinner: true,
    };
    const added = addReturnLeg(route);
    expect(added.returnLegs[1]).toMatchObject({
      from: route.returnLegs[0].to,
      to: route.outboundLegs[0].from,
      methodOfTravelDisplayName: "Train",
      methodOfTravelDescription: "",
    });
    expect(added.lastLegQualifiesForDinner).toBe(true);
  });

  it("fills repeated missing addresses after prompting once", () => {
    const missing = { ...workAddress };
    const route = {
      outboundLegs: [
        { ...outbound("Train"), to: { address: missing, addressText: "Same" } },
      ],
      returnLegs: [
        {
          ...outbound("Train"),
          from: { address: missing, addressText: "Same" },
        },
      ],
    };
    const target = findMissingRouteCounty(route);
    const updated = setRouteAddressCounty(route, target, "Erie");
    expect(findMissingRouteCounty(updated)).toBeNull();
    expect(updated.returnLegs[0].from.address.county).toBe("Erie");
  });

  it("keeps address display text out of the backend route DTO", () => {
    const route = {
      outboundLegs: [outbound("Train")],
      returnLegs: [],
    };
    const editable = toEditableRoute({
      ...route,
      outboundLegs: [
        {
          ...route.outboundLegs[0],
          from: { address: workAddress },
        },
      ],
    });
    expect(editable.outboundLegs[0].from.addressText).toBe(
      workAddress.formattedAddressWithCounty,
    );
    expect(toRouteDto(editable).outboundLegs[0].from).not.toHaveProperty(
      "addressText",
    );
  });
});
