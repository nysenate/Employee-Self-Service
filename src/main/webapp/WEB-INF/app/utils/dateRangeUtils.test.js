import { describe, expect, it } from "vitest";
import {
  DATE_RANGE_PRESETS,
  DEFAULT_DATE_RANGE_PRESET,
  resolveDateRangePreset,
} from "app/utils/dateRangeUtils";

describe("date range presets", () => {
  const today = new Date(2026, 8, 10);

  it("defaults to the past three months with no future cutoff", () => {
    expect(DEFAULT_DATE_RANGE_PRESET).toBe("pastThreeMonthsOnward");
    expect(DATE_RANGE_PRESETS[0].label).toBe("Recent & future");
    expect(resolveDateRangePreset(DEFAULT_DATE_RANGE_PRESET, today)).toEqual({
      fromDate: "2026-06-10",
      toDate: null,
      selection: {
        type: "preset",
        preset: "pastThreeMonthsOnward",
      },
    });
  });

  it("retains the bounded past three months preset", () => {
    expect(resolveDateRangePreset("pastThreeMonths", today)).toEqual({
      fromDate: "2026-06-10",
      toDate: "2026-09-10",
      selection: {
        type: "preset",
        preset: "pastThreeMonths",
      },
    });
  });

  it("includes the full calendar year in the this year preset", () => {
    expect(resolveDateRangePreset("thisYear", today)).toEqual({
      fromDate: "2026-01-01",
      toDate: "2026-12-31",
      selection: {
        type: "preset",
        preset: "thisYear",
      },
    });
    expect(DATE_RANGE_PRESETS.map(({ value }) => value)).not.toContain(
      "yearToDate",
    );
  });

  it("replaces the past month preset", () => {
    expect(DATE_RANGE_PRESETS.map(({ value }) => value)).not.toContain(
      "pastMonth",
    );
  });
});
