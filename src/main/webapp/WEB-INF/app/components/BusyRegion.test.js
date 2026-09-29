import React from "react";
import { render, screen } from "@testing-library/react";
import { expect, it } from "vitest";
import BusyRegion from "./BusyRegion";

it("blocks inputs and links while keeping its status outside the busy content", () => {
  const content = (
    <>
      <h1>Return route</h1>
      <input aria-label="Destination" />
      <a href="/elsewhere">Edit route</a>
      <button>Next</button>
    </>
  );
  const { rerender } = render(
    <BusyRegion message="Calculating your route…">{content}</BusyRegion>,
  );
  const input = screen.getByLabelText("Destination");
  expect(input).toBeDisabled();
  expect(screen.getByRole("button", { name: "Next" })).toBeDisabled();
  expect(screen.getByRole("link").closest("[inert]")).not.toBeNull();
  expect(screen.getByRole("status").closest('[aria-busy="true"]')).toBeNull();
  expect(screen.getByRole("status")).toHaveTextContent(
    "Calculating your route",
  );
  rerender(<BusyRegion>{content}</BusyRegion>);
  expect(input).toBeEnabled();
  expect(screen.getByRole("link").closest("[inert]")).toBeNull();
  expect(screen.queryByRole("status")).not.toBeInTheDocument();
  expect(screen.getByRole("heading")).toHaveFocus();
});
