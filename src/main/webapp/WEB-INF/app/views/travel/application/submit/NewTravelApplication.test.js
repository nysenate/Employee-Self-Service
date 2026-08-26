import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { fireEvent, render, screen } from "@testing-library/react";
import { Link, MemoryRouter, Route, Routes } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import NewTravelApplication from "./NewTravelApplication";

function renderWorkflow() {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={["/travel/applications/new"]}>
        <Routes>
          <Route
            path="/travel/applications/new"
            element={<NewTravelApplication draft={{ traveler: {} }} />}
          />
          <Route
            path="/travel/applications"
            element={<h1>Travel history</h1>}
          />
        </Routes>
        <Link to="/travel/applications">Travel history link</Link>
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

describe("new travel application workflow shell", () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    vi.stubGlobal(
      "fetch",
      vi.fn().mockImplementation(async (url) => ({
        ok: true,
        json: async () =>
          String(url).includes("/config")
            ? { result: { config: { googleApiKey: "test-key" } } }
            : { result: [{ name: "Forum", displayName: "Forum" }] },
      })),
    );
  });

  it("marks progress, disables future steps, and retains data through Back", async () => {
    renderWorkflow();

    const purpose = screen.getByRole("button", { name: "Purpose" });
    expect(purpose).toHaveAttribute("aria-current", "step");
    expect(screen.getByRole("button", { name: "Outbound" })).toBeDisabled();

    await screen.findByRole("option", { name: "Forum" });
    fireEvent.change(await screen.findByLabelText("Purpose"), {
      target: { value: "Forum" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Next" }));

    expect(
      screen.getByRole("button", { name: /Purpose.*completed/ }),
    ).toBeEnabled();
    expect(screen.getByRole("button", { name: "Outbound" })).toHaveAttribute(
      "aria-current",
      "step",
    );
    expect(
      screen.queryByRole("button", { name: "Save" }),
    ).not.toBeInTheDocument();

    fireEvent.click(screen.getByRole("button", { name: "Back" }));
    expect(await screen.findByLabelText("Purpose")).toHaveValue("Forum");
  });

  it("warns before leaving dirty work and lets the user remain", async () => {
    renderWorkflow();
    await screen.findByRole("option", { name: "Forum" });
    fireEvent.change(await screen.findByLabelText("Purpose"), {
      target: { value: "Forum" },
    });

    const unload = new Event("beforeunload", { cancelable: true });
    window.dispatchEvent(unload);
    expect(unload.defaultPrevented).toBe(true);

    fireEvent.click(screen.getByRole("link", { name: "Travel history link" }));
    expect(
      screen.getByRole("dialog", { name: "Leave this application?" }),
    ).toBeVisible();
    fireEvent.click(
      screen.getByRole("button", { name: "Stay on application" }),
    );

    expect(screen.getByLabelText("Purpose")).toHaveValue("Forum");
    expect(
      screen.queryByRole("heading", { name: "Travel history" }),
    ).not.toBeInTheDocument();
  });

  it("leaves without a warning when the draft is unchanged", async () => {
    renderWorkflow();
    fireEvent.click(screen.getByRole("link", { name: "Travel history link" }));
    expect(
      await screen.findByRole("heading", { name: "Travel history" }),
    ).toBeVisible();
    expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
  });

  it("continues to the requested destination when leaving is confirmed", async () => {
    renderWorkflow();
    await screen.findByRole("option", { name: "Forum" });
    fireEvent.change(await screen.findByLabelText("Purpose"), {
      target: { value: "Forum" },
    });
    fireEvent.click(screen.getByRole("link", { name: "Travel history link" }));
    fireEvent.click(screen.getByRole("button", { name: "Leave application" }));

    expect(
      await screen.findByRole("heading", { name: "Travel history" }),
    ).toBeVisible();
  });
});
