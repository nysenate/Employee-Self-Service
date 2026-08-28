import React from "react";
import { fireEvent, render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import WorkflowActions from "./WorkflowActions";

describe("WorkflowActions", () => {
  it("disables the primary action while it is pending", () => {
    const onPrimary = vi.fn();
    render(
      <WorkflowActions
        step={1}
        onBack={vi.fn()}
        onPrimary={onPrimary}
        isPrimaryPending
      />,
    );

    const next = screen.getByRole("button", { name: "Next" });
    expect(next).toBeDisabled();
    expect(screen.getByRole("button", { name: "Back" })).toBeDisabled();
    fireEvent.click(next);
    expect(onPrimary).not.toHaveBeenCalled();
  });

  it("locks every review action during and after submission", () => {
    render(<WorkflowActions step={4} isDisabled />);

    expect(screen.getByRole("button", { name: "Back" })).toBeDisabled();
    expect(screen.getByRole("button", { name: "Save" })).toBeDisabled();
    expect(
      screen.getByRole("button", { name: "Submit application" }),
    ).toBeDisabled();
  });
});
