import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

describe("Google Places loader", () => {
  beforeEach(() => {
    vi.useFakeTimers();
    vi.resetModules();
    delete window.google;
    document.head
      .querySelectorAll('script[src*="maps.google.com/maps/api/js"]')
      .forEach((script) => script.remove());
  });

  afterEach(() => {
    vi.useRealTimers();
    delete window.google;
  });

  it("resolves when the Google Places API initializes", async () => {
    const { loadGooglePlaces } = await import("./useGooglePlaces");
    const loading = loadGooglePlaces("test-key");
    const script = document.head.querySelector(
      'script[src*="maps.google.com/maps/api/js"]',
    );
    window.google = { maps: { places: { Autocomplete: vi.fn() } } };
    script.dispatchEvent(new Event("load"));

    await expect(loading).resolves.toBeUndefined();
  });

  it("rejects instead of hanging when an existing script never initializes Places", async () => {
    const script = document.createElement("script");
    script.src = "https://maps.google.com/maps/api/js?libraries=places";
    document.head.appendChild(script);
    const { loadGooglePlaces } = await import("./useGooglePlaces");

    const loading = loadGooglePlaces("test-key");
    const rejection = expect(loading).rejects.toThrow("timed out");
    await vi.advanceTimersByTimeAsync(10000);
    await rejection;
  });
});
