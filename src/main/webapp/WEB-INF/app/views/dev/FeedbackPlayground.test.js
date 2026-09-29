import React from "react";
import { act, fireEvent, render, screen, within } from "@testing-library/react";
import { afterEach, beforeEach, expect, it, vi } from "vitest";
import NotificationProvider from "app/components/NotificationProvider";
import FeedbackPlayground from "./FeedbackPlayground";

function setup() {
  return render(
    <NotificationProvider>
      <FeedbackPlayground />
    </NotificationProvider>,
  );
}

beforeEach(() => {
  vi.useFakeTimers();
  vi.stubGlobal(
    "fetch",
    vi.fn(() => {
      throw new Error("The playground must not call APIs.");
    }),
  );
});
afterEach(() => {
  expect(fetch).not.toHaveBeenCalled();
  vi.useRealTimers();
  vi.unstubAllGlobals();
});

it("shows the shared toast without taking focus and simulates a button save", () => {
  setup();
  const input = screen.getByLabelText("Notification message");
  input.focus();
  fireEvent.click(screen.getByRole("button", { name: "Show success toast" }));
  expect(screen.getByRole("status")).toHaveTextContent("Draft saved");
  expect(input).toHaveFocus();
  fireEvent.click(screen.getByRole("button", { name: "Dismiss notification" }));
  const save = screen.getByRole("button", { name: "Save demo changes" });
  fireEvent.click(save);
  expect(save).toBeDisabled();
  act(() => vi.advanceTimersByTime(3000));
  expect(save).toBeEnabled();
  expect(screen.getByRole("status")).toHaveTextContent("Demo changes saved");
});

it("blocks the card during work and preserves entries after failure", () => {
  setup();
  fireEvent.change(screen.getByLabelText("Simulated result"), {
    target: { value: "error" },
  });
  fireEvent.change(screen.getByLabelText("Route card length"), {
    target: { value: "6" },
  });
  const input = screen.getByLabelText("Segment 1 departure");
  fireEvent.change(input, { target: { value: "Buffalo, NY" } });
  fireEvent.click(screen.getByRole("button", { name: "Calculate route" }));
  expect(input).toBeDisabled();
  expect(screen.getByRole("status")).toHaveTextContent(
    "Calculating your route",
  );
  expect(screen.getByLabelText("Segment 6 arrival")).toBeDisabled();
  act(() => vi.advanceTimersByTime(3000));
  expect(screen.getByRole("alert")).toHaveTextContent(
    "Your entries are still available",
  );
  expect(input).toBeEnabled();
  expect(input).toHaveValue("Buffalo, NY");
});

it("reuses submission dialogs, prevents dismissal while pending, and recovers from failure", () => {
  setup();
  fireEvent.change(screen.getByLabelText("Simulated result"), {
    target: { value: "error" },
  });
  fireEvent.click(
    screen.getByRole("button", { name: "Try submission dialog" }),
  );
  fireEvent.click(
    screen.getByRole("button", { name: "Submit demo application" }),
  );
  expect(
    screen.getByRole("dialog", { name: "Submitting demo application…" }),
  ).toBeVisible();
  fireEvent.keyDown(document.activeElement, { key: "Escape", code: "Escape" });
  expect(screen.getByRole("dialog")).toBeVisible();
  act(() => vi.advanceTimersByTime(3000));
  expect(
    within(screen.getByRole("dialog")).getByRole("alert"),
  ).toHaveTextContent("example submission failed");
  fireEvent.click(
    screen.getByRole("button", { name: "Return to application" }),
  );
  fireEvent.change(screen.getByLabelText("Simulated result"), {
    target: { value: "success" },
  });
  fireEvent.click(
    screen.getByRole("button", { name: "Try submission dialog" }),
  );
  fireEvent.click(
    screen.getByRole("button", { name: "Submit demo application" }),
  );
  act(() => vi.advanceTimersByTime(3000));
  expect(
    screen.getByRole("dialog", { name: "Application submitted" }),
  ).toBeVisible();
  fireEvent.click(screen.getByRole("button", { name: "Log out of ESS" }));
  expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
  expect(
    screen.getByRole("heading", { name: "Feedback playground" }),
  ).toBeVisible();
});

it("shows initial loading and refreshes existing results without blocking them", () => {
  setup();
  fireEvent.click(screen.getByRole("button", { name: "Load example content" }));
  expect(screen.getByRole("status")).toHaveTextContent(
    "Loading travel applications",
  );
  act(() => vi.advanceTimersByTime(3000));
  expect(
    screen.getByText("Example applications are ready to view."),
  ).toBeVisible();
  fireEvent.click(
    screen.getByRole("button", { name: "Simulate filter change" }),
  );
  expect(screen.getByText("Updating results")).toBeVisible();
  expect(
    screen.getByText("Example application #1234 · Pending review"),
  ).toBeVisible();
  expect(
    screen
      .getByText("Example application #1234 · Pending review")
      .closest('[aria-busy="true"]'),
  ).not.toBeNull();
  act(() => vi.advanceTimersByTime(3000));
  expect(screen.queryByText("Updating results")).not.toBeInTheDocument();
});

it("captures run settings, cancels on reset, and clears pending timers on unmount", () => {
  const { unmount } = setup();
  fireEvent.change(screen.getByLabelText("Simulated delay"), {
    target: { value: "8" },
  });
  fireEvent.click(screen.getByRole("button", { name: "Calculate route" }));
  fireEvent.change(screen.getByLabelText("Simulated delay"), {
    target: { value: "1" },
  });
  act(() => vi.advanceTimersByTime(1000));
  expect(screen.getByLabelText("Segment 1 departure")).toBeDisabled();
  fireEvent.click(screen.getByRole("button", { name: "Reset route demo" }));
  act(() => vi.advanceTimersByTime(8000));
  expect(
    screen.queryByText(/Example route calculated/),
  ).not.toBeInTheDocument();
  fireEvent.click(screen.getByRole("button", { name: "Save demo changes" }));
  unmount();
  act(() => vi.advanceTimersByTime(8000));
  expect(screen.queryByText("Demo changes saved")).not.toBeInTheDocument();
});
