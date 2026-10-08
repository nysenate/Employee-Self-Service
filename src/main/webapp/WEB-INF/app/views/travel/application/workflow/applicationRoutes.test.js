import { describe, expect, it } from "vitest";
import {
  historyDetailsUrl,
  normalizeHistoryReturnTo,
  parseApplicationId,
  resubmitUrl,
} from "./applicationRoutes";

describe("application route helpers", () => {
  it.each([
    ["1", 1],
    [42, 42],
    [String(Number.MAX_SAFE_INTEGER), Number.MAX_SAFE_INTEGER],
  ])("parses valid application ID %s", (value, expected) => {
    expect(parseApplicationId(value)).toBe(expected);
  });

  it.each([
    undefined,
    null,
    "",
    "0",
    "0012",
    "-1",
    "1.5",
    "1abc",
    " 1",
    String(Number.MAX_SAFE_INTEGER + 1),
  ])("rejects unsafe or malformed application ID %s", (value) => {
    expect(parseApplicationId(value)).toBeNull();
  });

  it("builds the explicit resubmission URL", () => {
    expect(resubmitUrl(42)).toBe("/travel/applications/42/resubmit");
  });

  it("retains supported history context and replaces the selected app", () => {
    expect(
      historyDetailsUrl(
        42,
        "?range=custom&fromDate=2026-01-01&toDate=2026-02-01&status=DISAPPROVED&sort=travelDate&limit=20&offset=2&appId=7&role=ADMIN",
      ),
    ).toBe(
      "/travel/applications?range=custom&fromDate=2026-01-01&toDate=2026-02-01&status=DISAPPROVED&sort=travelDate&limit=20&offset=2&appId=42",
    );
  });

  it("encodes retained history values", () => {
    expect(historyDetailsUrl(8, "status=Needs review&other=value")).toBe(
      "/travel/applications?status=Needs+review&appId=8",
    );
  });

  it.each([
    "/travel/applications",
    "/travel/applications?status=DISAPPROVED&appId=42",
  ])("accepts a local history return URL: %s", (value) => {
    expect(normalizeHistoryReturnTo(value)).toBe(value);
  });

  it.each([
    undefined,
    "https://example.com/travel/applications",
    "//example.com/travel/applications",
    "/travel/manage/queue",
    "/travel/applications/42",
    "/travel/applications#details",
    "/travel/applications?bad=%",
  ])("falls back for an unsafe history return URL: %s", (value) => {
    expect(normalizeHistoryReturnTo(value)).toBe("/travel/applications");
  });
});
