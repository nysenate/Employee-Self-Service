import { beforeEach, describe, expect, it, vi } from "vitest";
import { fetchApiJson } from "./fetchJson";

describe("fetchApiJson", () => {
  beforeEach(() => {
    vi.stubGlobal(
      "fetch",
      vi
        .fn()
        .mockResolvedValue({ ok: true, text: () => Promise.resolve("{}") }),
    );
  });

  it.each(["POST", "PUT", "PATCH"])(
    "serializes a JSON payload for %s requests",
    async (method) => {
      await fetchApiJson("/travel/drafts", {
        method,
        payload: { id: 12 },
      });

      expect(fetch).toHaveBeenCalledWith(
        "/api/v1/travel/drafts",
        expect.objectContaining({
          method,
          body: JSON.stringify({ id: 12 }),
          headers: {
            Accept: "application/json",
            "Content-Type": "application/json",
          },
        }),
      );
    },
  );

  it("passes FormData through without setting a content type", async () => {
    const payload = new FormData();
    payload.append("file", new Blob(["document"]), "document.txt");

    await fetchApiJson("/travel/drafts/attachment", {
      method: "POST",
      payload,
    });

    const init = fetch.mock.calls[0][1];
    expect(init.body).toBe(payload);
    expect(init.headers).toEqual({ Accept: "application/json" });
    expect(init).not.toHaveProperty("payload");
  });
});
