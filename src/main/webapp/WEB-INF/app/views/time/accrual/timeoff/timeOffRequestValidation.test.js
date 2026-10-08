import { describe, expect, it } from "vitest";
import {
  exceedsPersonalCapacity,
  PERSONAL_CAPACITY_ERROR,
  validateTimeOffRequest,
} from "./timeOffRequestValidation";

const TODAY = "2026-08-26";
const ACCRUALS = { personal: 7, vacation: 100, sick: 100 };

/** A valid day drawing the given personal hours, so only the personal rule can fail. */
function day(date, personalHours) {
  return { date, personalHours, holidayHours: 0 };
}

describe("personal hours capacity", () => {
  it("accepts a request within the balance held now", () => {
    expect(
      exceedsPersonalCapacity([day("2026-09-01", 7)], ACCRUALS, TODAY),
    ).toBe(false);
  });

  it("rejects more personal hours than are available later this year", () => {
    expect(
      exceedsPersonalCapacity([day("2026-12-31", 7.5)], ACCRUALS, TODAY),
    ).toBe(true);
  });

  it("sums the personal hours across every day of the request", () => {
    const days = [day("2026-09-01", 4), day("2026-09-02", 4)];
    expect(exceedsPersonalCapacity(days, ACCRUALS, TODAY)).toBe(true);
  });

  it("does not count days in the next calendar year against this year's balance", () => {
    expect(
      exceedsPersonalCapacity([day("2027-01-04", 35)], ACCRUALS, TODAY),
    ).toBe(false);
  });

  it("counts only the days of this year when a request spans the year end", () => {
    const withinThisYear = [day("2026-12-30", 7), day("2027-01-04", 35)];
    expect(exceedsPersonalCapacity(withinThisYear, ACCRUALS, TODAY)).toBe(
      false,
    );

    const overThisYear = [day("2026-12-30", 7.5), day("2027-01-04", 35)];
    expect(exceedsPersonalCapacity(overThisYear, ACCRUALS, TODAY)).toBe(true);
  });

  it("holds off until the accruals have loaded", () => {
    expect(exceedsPersonalCapacity([day("2026-09-01", 7.5)], null, TODAY)).toBe(
      false,
    );
  });

  it("reports the capacity message from the full validation", () => {
    const messages = validateTimeOffRequest(
      [day("2026-09-01", 7.5)],
      ACCRUALS,
      {},
      TODAY,
    );
    expect(messages).toContain(PERSONAL_CAPACITY_ERROR);
  });

  it("leaves a next-year request valid", () => {
    // A full 35 hour allotment, spread over days so the daily hour cap is not what fails.
    const days = [
      day("2027-01-04", 7),
      day("2027-01-05", 7),
      day("2027-01-06", 7),
      day("2027-01-07", 7),
      day("2027-01-08", 7),
    ];
    expect(validateTimeOffRequest(days, ACCRUALS, {}, TODAY)).toEqual([]);
  });
});

describe("vacation and sick hours", () => {
  it("are still checked against the balance regardless of the year", () => {
    const messages = validateTimeOffRequest(
      [{ date: "2027-01-04", vacationHours: 200, holidayHours: 0 }],
      ACCRUALS,
      {},
      TODAY,
    );
    expect(messages).toContain(
      "ERROR: You are requesting more time off than your accruals allow.",
    );
  });

  it("no longer raise the general message for personal hours alone", () => {
    const messages = validateTimeOffRequest(
      [day("2026-09-01", 7.5)],
      ACCRUALS,
      {},
      TODAY,
    );
    expect(messages).not.toContain(
      "ERROR: You are requesting more time off than your accruals allow.",
    );
  });
});
