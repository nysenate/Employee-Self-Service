import { describe, expect, it } from "vitest";
import {
  isOutsideConus,
  normalizeOutboundRoute,
  normalizeTravelDate,
  validateOutboundRoute,
  validateOutboundDestinations,
} from "./routeValidation";

const address = { zip5: "12207", country: "United States", state: "NY" };
const validRoute = {
  outboundLegs: [
    {
      from: { addressText: "Albany", address },
      to: { addressText: "Buffalo", address },
      travelDate: "01/05/2026",
      methodOfTravelDisplayName: "Train",
    },
  ],
};

describe("outbound route validation", () => {
  it.each([["01/05/2026", "01/05/2026"]])(
    "normalizes supported date %s",
    (input, expected) => {
      expect(normalizeTravelDate(input)).toBe(expected);
    },
  );

  it.each(["1/5/26", "1/5/2026", "02/29/2025", "13/01/2026", "2026-01-05"])(
    "rejects invalid date %s",
    (input) => expect(normalizeTravelDate(input)).toBeNull(),
  );

  it("identifies incomplete, unrecognized, and Other fields", () => {
    const errors = validateOutboundRoute({
      outboundLegs: [
        {
          from: { addressText: "typed only", address: null },
          to: { addressText: "" },
          travelDate: "bad",
          methodOfTravelDisplayName: "Other",
          methodOfTravelDescription: "",
        },
      ],
    });
    expect(errors).toMatchObject({
      "segment-0-from": expect.stringContaining("recognized"),
      "segment-0-to": expect.stringContaining("required"),
      "segment-0-date": expect.stringContaining("valid"),
      "segment-0-modeOther": expect.stringContaining("Specify"),
    });
  });

  it("normalizes valid route dates without changing the source", () => {
    expect(validateOutboundRoute(validRoute)).toEqual({});
    expect(normalizeOutboundRoute(validRoute).outboundLegs[0].travelDate).toBe(
      "01/05/2026",
    );
    expect(validRoute.outboundLegs[0].travelDate).toBe("01/05/2026");
  });

  it.each([
    { country: "United States", state: "AK" },
    { country: "United States", state: "Hawaii" },
    { country: "Canada", state: "ON" },
  ])("recognizes destinations outside CONUS", (value) => {
    expect(isOutsideConus(value)).toBe(true);
  });

  it("blocks continuing after the outside-CONUS advisory until corrected", () => {
    const errors = validateOutboundRoute({
      outboundLegs: [
        {
          ...validRoute.outboundLegs[0],
          to: {
            addressText: "Honolulu, HI 96813",
            address: {
              zip5: "96813",
              country: "United States",
              state: "HI",
            },
          },
        },
      ],
    });

    expect(errors["segment-0-to"]).toContain("continental United States");
  });

  it("keeps a selected outside-CONUS destination invalid independently of other errors", () => {
    const route = {
      outboundLegs: [
        {
          ...validRoute.outboundLegs[0],
          to: {
            addressText: "Honolulu, HI 96813",
            address: {
              zip5: "96813",
              country: "United States",
              state: "HI",
            },
          },
        },
      ],
    };

    expect(validateOutboundDestinations(route)).toHaveProperty("segment-0-to");
    expect(
      validateOutboundDestinations({ ...route, unrelatedEdit: true }),
    ).toHaveProperty("segment-0-to");
  });
});
