import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { MemoryRouter } from "react-router-dom";
import SubmitApplication from "./index";

function renderPage() {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>
        <SubmitApplication />
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

function response(body, { ok = true, statusText = "" } = {}) {
  return Promise.resolve({
    ok,
    statusText,
    json: () => Promise.resolve(body),
    text: () => Promise.resolve(JSON.stringify(body)),
  });
}

describe("new travel application initialization", () => {
  beforeEach(() => vi.restoreAllMocks());

  it("creates a draft, displays Purpose, and keeps employee data read-only", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn((url) => {
        if (url.endsWith("/travel/drafts")) {
          return response({
            result: {
              traveler: {
                fullName: "Jamie Rivera",
                nid: "N0123",
                jobTitle: "Analyst",
                workPhone: "518-555-0100",
                empWorkLocation: {
                  address: {
                    formattedAddressWithCounty:
                      "State Street, Albany, NY 12207",
                  },
                },
                isDepartmentHead: false,
                department: { head: { fullName: "Morgan Lee" } },
              },
            },
          });
        }
        return response({ result: [] });
      }),
    );

    renderPage();

    expect(screen.getByRole("status")).toHaveTextContent(
      "Preparing your travel application",
    );
    expect(
      await screen.findByRole("heading", { name: "Purpose of Travel" }),
    ).toBeVisible();
    expect(screen.getByText("Jamie Rivera")).toBeVisible();
    expect(screen.getByText("Morgan Lee")).toBeVisible();
    expect(screen.getByText("State Street, Albany, NY 12207")).toBeVisible();
    expect(
      screen.queryByRole("textbox", { name: /traveler/i }),
    ).not.toBeInTheDocument();
    expect(fetch).toHaveBeenCalledWith(
      "/api/v1/travel/drafts",
      expect.objectContaining({ method: "PUT" }),
    );
  });

  it("blocks the form when department information is missing", async () => {
    vi.stubGlobal(
      "fetch",
      vi
        .fn()
        .mockReturnValue(
          response(
            { errorCode: "MISSING_DEPARTMENT", message: "No department" },
            { ok: false, statusText: "Bad Request" },
          ),
        ),
    );

    renderPage();

    expect(
      await screen.findByRole("heading", {
        name: "Department information is missing",
      }),
    ).toBeVisible();
    expect(
      screen.queryByRole("heading", { name: "Purpose of Travel" }),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByRole("button", { name: "Try again" }),
    ).not.toBeInTheDocument();
  });

  it("offers retry after a transient initialization failure", async () => {
    const fetchMock = vi
      .fn()
      .mockReturnValueOnce(
        response(
          { errorCode: "INTERNAL_ERROR" },
          { ok: false, statusText: "Server Error" },
        ),
      )
      .mockReturnValueOnce(
        response({ result: { traveler: { fullName: "Jamie Rivera" } } }),
      )
      .mockReturnValue(response({ result: [] }));
    vi.stubGlobal("fetch", fetchMock);

    renderPage();
    fireEvent.click(await screen.findByRole("button", { name: "Try again" }));

    await waitFor(() =>
      expect(
        screen.getByRole("heading", { name: "Purpose of Travel" }),
      ).toBeVisible(),
    );
    expect(fetchMock).toHaveBeenCalledTimes(3);
  });
});
