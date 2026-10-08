import React from "react";
import { fireEvent, render, screen } from "@testing-library/react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { Link, MemoryRouter, useLocation } from "react-router-dom";
import UnsavedChangesModal from "../components/UnsavedChangesModal";
import { useUnsavedChangesGuard } from "./useUnsavedChangesGuard";

function GuardedEditor({ isDirty = true, isLocked = false, destination }) {
  const guard = useUnsavedChangesGuard(isDirty, isLocked);
  const location = useLocation();
  return (
    <>
      <output aria-label="Current route">
        {location.pathname + location.search + location.hash}
      </output>
      <input aria-label="Trip purpose" defaultValue="Unsaved trip details" />
      <a href={destination}>Switch application</a>
      <Link to="/travel/applications?status=draft#results">Travel history</Link>
      <UnsavedChangesModal guard={guard} />
    </>
  );
}

function renderEditor(props = {}) {
  return render(
    <MemoryRouter initialEntries={["/travel/applications/new"]}>
      <GuardedEditor destination="/time" {...props} />
    </MemoryRouter>,
  );
}

function dispatchUnload() {
  const event = new Event("beforeunload", { cancelable: true });
  window.dispatchEvent(event);
  return event;
}

describe("unsaved Travel navigation", () => {
  let assign;

  beforeEach(() => {
    assign = vi.fn();
    const browserWindow = window;
    const location = {
      origin: browserWindow.location.origin,
      href: browserWindow.location.href,
      assign,
    };
    // jsdom's Location.assign is not configurable. Replace only access to
    // location; keep the real window's events and DOM for the guard and dialog.
    vi.stubGlobal(
      "window",
      new Proxy(browserWindow, {
        get: (target, property) =>
          property === "location"
            ? location
            : Reflect.get(target, property, target),
      }),
    );
  });

  afterEach(() => vi.unstubAllGlobals());

  it.each([
    "/time",
    "/supply",
    "/myinfo",
    "/logout",
    "/time/record/entry?period=123#details",
    "/travel-other",
  ])("loads %s through the server after confirming an exit", (destination) => {
    renderEditor({ destination });
    expect(dispatchUnload().defaultPrevented).toBe(true);

    fireEvent.click(screen.getByRole("link", { name: "Switch application" }));
    expect(
      screen.getByRole("dialog", { name: "Leave this application?" }),
    ).toBeVisible();
    expect(assign).not.toHaveBeenCalled();
    expect(screen.getByLabelText("Current route")).toHaveTextContent(
      "/travel/applications/new",
    );

    // Observe beforeunload at the point the browser starts document navigation.
    assign.mockImplementation(() => {
      expect(dispatchUnload().defaultPrevented).toBe(false);
    });
    fireEvent.click(screen.getByRole("button", { name: "Leave application" }));

    expect(assign).toHaveBeenCalledTimes(1);
    expect(assign).toHaveBeenCalledWith(destination);
    expect(screen.getByLabelText("Current route")).toHaveTextContent(
      "/travel/applications/new",
    );
    // Only the confirmed unload is exempt if the document remains open.
    expect(dispatchUnload().defaultPrevented).toBe(true);
  });

  it("keeps edits and unload protection when the user chooses to stay", () => {
    renderEditor();
    fireEvent.click(screen.getByRole("link", { name: "Switch application" }));
    fireEvent.click(
      screen.getByRole("button", { name: "Stay on application" }),
    );

    expect(assign).not.toHaveBeenCalled();
    expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
    expect(screen.getByLabelText("Trip purpose")).toHaveValue(
      "Unsaved trip details",
    );
    expect(dispatchUnload().defaultPrevented).toBe(true);
  });

  it("preserves explicit document navigation within Travel", () => {
    renderEditor({ destination: "/travel" });
    const link = screen.getByRole("link", { name: "Switch application" });
    link.setAttribute("data-reload-document", "");
    fireEvent.click(link);
    fireEvent.click(screen.getByRole("button", { name: "Leave application" }));

    expect(assign).toHaveBeenCalledWith("/travel");
    expect(dispatchUnload().defaultPrevented).toBe(false);
    expect(screen.getByLabelText("Current route")).toHaveTextContent(
      "/travel/applications/new",
    );
  });

  it("keeps confirmed internal Travel navigation in React", () => {
    renderEditor();
    fireEvent.click(screen.getByRole("link", { name: "Travel history" }));
    fireEvent.click(screen.getByRole("button", { name: "Leave application" }));

    expect(screen.getByLabelText("Current route")).toHaveTextContent(
      "/travel/applications?status=draft#results",
    );
    expect(assign).not.toHaveBeenCalled();
    expect(dispatchUnload().defaultPrevented).toBe(true);
  });

  it("allows an unchanged editor to navigate without confirmation", () => {
    renderEditor({ isDirty: false });
    fireEvent.click(screen.getByRole("link", { name: "Travel history" }));

    expect(screen.getByLabelText("Current route")).toHaveTextContent(
      "/travel/applications?status=draft#results",
    );
    expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
    expect(dispatchUnload().defaultPrevented).toBe(false);
  });

  it("blocks app switching while submission is locked", () => {
    renderEditor({ isDirty: false, isLocked: true });
    const link = screen.getByRole("link", { name: "Switch application" });
    expect(fireEvent.click(link)).toBe(false);

    expect(assign).not.toHaveBeenCalled();
    expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
    expect(dispatchUnload().defaultPrevented).toBe(true);
  });

  it("ignores links opening another tab", () => {
    renderEditor();
    const link = screen.getByRole("link", { name: "Switch application" });
    link.setAttribute("target", "_blank");
    expect(fireEvent.click(link)).toBe(true);

    expect(assign).not.toHaveBeenCalled();
    expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
    expect(dispatchUnload().defaultPrevented).toBe(true);
  });
});
