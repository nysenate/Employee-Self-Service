import React from "react";
import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import ErrorAlert from "./ErrorAlert";

describe("ErrorAlert", () => {
  it("provides a default heading and alert semantics", () => {
    render(<ErrorAlert>Try again later.</ErrorAlert>);

    expect(screen.getByRole("alert")).toHaveTextContent(
      "There was a problemTry again later.",
    );
    expect(
      screen.getByRole("heading", {
        level: 2,
        name: "There was a problem",
      }),
    ).toBeVisible();
  });

  it("supports a contextual title and heading level", () => {
    render(
      <ErrorAlert headingAs="h1" title="Page not found">
        Check the address and try again.
      </ErrorAlert>,
    );

    expect(
      screen.getByRole("heading", { level: 1, name: "Page not found" }),
    ).toBeVisible();
  });
});
