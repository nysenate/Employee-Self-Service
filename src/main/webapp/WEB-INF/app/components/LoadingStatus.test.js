import React from "react";
import { render, screen, within } from "@testing-library/react";
import { expect, it } from "vitest";
import LoadingStatus from "./LoadingStatus";

it("announces descriptive text while keeping the spinner decorative", () => {
  render(
    <LoadingStatus
      message="Loading applications…"
      description="Your results will appear here."
      layout="centered"
    />,
  );
  const status = screen.getByRole("status");
  expect(status).toHaveTextContent("Loading applications…");
  expect(status).toHaveTextContent("Your results will appear here.");
  expect(status.querySelector("svg")).toHaveAttribute("aria-hidden", "true");
  expect(status.querySelector("svg")).toHaveAttribute("focusable", "false");
});

it("lets an existing live region own the announcement", () => {
  render(
    <div role="status">
      <LoadingStatus message="Refreshing" announce={false} />
    </div>,
  );
  expect(screen.getAllByRole("status")).toHaveLength(1);
  expect(
    within(screen.getByRole("status")).queryByRole("status"),
  ).not.toBeInTheDocument();
  expect(screen.getByRole("status")).toHaveTextContent("Refreshing");
});
