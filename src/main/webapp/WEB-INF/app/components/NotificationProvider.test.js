import React from "react";
import { act, fireEvent, render, screen } from "@testing-library/react";
import { MemoryRouter, Route, Routes, useNavigate } from "react-router-dom";
import { afterEach, expect, it, vi } from "vitest";
import NotificationProvider, { useNotifySuccess } from "./NotificationProvider";

function Actions() {
  const notify = useNotifySuccess();
  const navigate = useNavigate();
  return (
    <>
      <button onClick={() => notify("Draft saved")}>Save</button>
      <button onClick={() => notify("Application approved")}>Approve</button>
      <button
        onClick={() => {
          notify("Changes saved");
          navigate("/done");
        }}
      >
        Save and leave
      </button>
    </>
  );
}

function setup() {
  return render(
    <NotificationProvider>
      <MemoryRouter>
        <Routes>
          <Route path="/" element={<Actions />} />
          <Route path="/done" element={<h1>Review queue</h1>} />
        </Routes>
      </MemoryRouter>
    </NotificationProvider>,
  );
}

afterEach(() => vi.useRealTimers());

it("preserves confirmations after navigation without taking focus", () => {
  setup();
  const save = screen.getByRole("button", { name: "Save" });
  save.focus();
  fireEvent.click(save);
  expect(save).toHaveFocus();
  expect(screen.getByRole("status")).toHaveTextContent("Draft saved");
  fireEvent.click(screen.getByRole("button", { name: "Dismiss notification" }));
  fireEvent.click(screen.getByRole("button", { name: "Save and leave" }));
  expect(screen.getByRole("heading", { name: "Review queue" })).toBeVisible();
  expect(screen.getByRole("status")).toHaveTextContent("Changes saved");
});

it("queues distinct results and refreshes repeated confirmations without duplicates", () => {
  vi.useFakeTimers();
  setup();
  fireEvent.click(screen.getByText("Save"));
  act(() => vi.advanceTimersByTime(2000));
  fireEvent.click(screen.getByText("Save"));
  fireEvent.click(screen.getByText("Approve"));
  act(() => vi.advanceTimersByTime(2000));
  expect(screen.getByRole("status")).toHaveTextContent("Draft saved");
  expect(screen.queryByText("Application approved")).not.toBeInTheDocument();
  act(() => vi.advanceTimersByTime(2000));
  expect(screen.getByRole("status")).toHaveTextContent("Application approved");
  act(() => vi.advanceTimersByTime(4000));
  expect(screen.queryByRole("status")).not.toBeInTheDocument();
});

it("pauses the remaining time while hovered, focused, or away from the window", () => {
  vi.useFakeTimers();
  setup();
  fireEvent.click(screen.getByText("Save"));
  act(() => vi.advanceTimersByTime(2000));
  const status = screen.getByRole("status");
  fireEvent.pointerEnter(status);
  act(() => vi.advanceTimersByTime(10000));
  expect(status).toHaveTextContent("Draft saved");
  act(() =>
    screen.getByRole("button", { name: "Dismiss notification" }).focus(),
  );
  fireEvent.pointerLeave(status);
  act(() => vi.advanceTimersByTime(10000));
  expect(status).toHaveTextContent("Draft saved");
  act(() => screen.getByText("Save").focus());
  fireEvent.blur(window);
  act(() => vi.advanceTimersByTime(10000));
  expect(status).toHaveTextContent("Draft saved");
  fireEvent.focus(window);
  act(() => vi.advanceTimersByTime(1999));
  expect(status).toHaveTextContent("Draft saved");
  act(() => vi.advanceTimersByTime(1));
  expect(screen.queryByRole("status")).not.toBeInTheDocument();
});

it("keeps queued notifications paused when dismissing with keyboard focus", () => {
  vi.useFakeTimers();
  setup();
  fireEvent.click(screen.getByText("Save"));
  fireEvent.click(screen.getByText("Approve"));
  const dismiss = screen.getByRole("button", { name: "Dismiss notification" });
  act(() => dismiss.focus());
  fireEvent.click(dismiss);
  act(() => vi.advanceTimersByTime(10000));
  expect(screen.getByRole("status")).toHaveTextContent("Application approved");
  expect(dismiss).toHaveFocus();
});
