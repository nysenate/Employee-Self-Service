import { beforeEach, describe, expect, it, vi } from "vitest";
import { fetchApiJson, FetchError } from "./fetchJson";

describe("fetchApiJson", () => {
  beforeEach(() => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue({ ok: true, json: () => Promise.resolve({}) }),
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

  it("preserves the status and API body on an HTTP error", async () => {
    const data = {
      status: { authorized: false },
      message: "Authentication required",
    };
    const response = {
      ok: false,
      status: 401,
      statusText: "Unauthorized",
      json: async () => data,
    };
    vi.mocked(fetch).mockResolvedValue(response);
    await expect(fetchApiJson("/employees/me")).rejects.toMatchObject({
      name: "FetchError",
      response,
      data,
    });
  });

  it.each([401, 403, 503])(
    "preserves HTTP %s even when the error body cannot be parsed",
    async (status) => {
      const response = {
        ok: false,
        status,
        statusText: "HTTP error",
        json: async () => {
          throw new SyntaxError("Invalid JSON");
        },
      };
      vi.mocked(fetch).mockResolvedValue(response);
      const error = await fetchApiJson("/employees/me").catch((error) => error);
      expect(error).toBeInstanceOf(FetchError);
      expect(error.response).toBe(response);
      expect(error.data).toEqual({ message: "HTTP error" });
    },
  );

  it("preserves a network rejection", async () => {
    const error = new TypeError("Failed to fetch");
    vi.mocked(fetch).mockRejectedValue(error);
    await expect(fetchApiJson("/employees/me")).rejects.toBe(error);
  });
});
