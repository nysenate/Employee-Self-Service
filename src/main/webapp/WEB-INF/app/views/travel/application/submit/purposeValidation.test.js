import { describe, expect, it } from "vitest";
import { validatePurpose } from "./purposeValidation";

function draft(eventType, fields = {}) {
  return { amendment: { purposeOfTravel: { eventType, ...fields } } };
}

describe("validatePurpose", () => {
  it("requires an event type", () => {
    expect(validatePurpose(draft(null))).toEqual({
      eventType: "A purpose of travel is required.",
    });
  });

  it("uses event type flags for conditional requirements", () => {
    expect(
      validatePurpose(
        draft({ displayName: "Forum", requiresName: true }, { eventName: " " }),
      ),
    ).toHaveProperty("eventName");
    expect(
      validatePurpose(
        draft(
          { displayName: "Other", requiresAdditionalPurpose: true },
          { additionalPurpose: " " },
        ),
      ),
    ).toHaveProperty("additionalPurpose");
  });

  it("accepts valid named and other purposes", () => {
    expect(
      validatePurpose(
        draft(
          { displayName: "Forum", requiresName: true },
          { eventName: "Budget forum" },
        ),
      ),
    ).toEqual({});
    expect(
      validatePurpose(
        draft(
          { displayName: "Other", requiresAdditionalPurpose: true },
          { additionalPurpose: "Agency meeting" },
        ),
      ),
    ).toEqual({});
  });
});
