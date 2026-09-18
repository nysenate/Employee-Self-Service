import React from "react";
import { fireEvent, render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { STANDARD_STEPS } from "../../workflow/workflowSteps";
import WorkflowProgress from "./WorkflowProgress";

describe("WorkflowProgress", () => {
  it("renders the standard step order and selects completed IDs", () => {
    const onSelect = vi.fn();
    render(
      <WorkflowProgress
        steps={STANDARD_STEPS}
        currentStepId="return"
        completedStepIds={["purpose", "outbound"]}
        onSelect={onSelect}
      />,
    );

    const buttons = screen.getAllByRole("button");
    expect(buttons).toHaveLength(STANDARD_STEPS.length);
    STANDARD_STEPS.forEach((step, index) => {
      expect(buttons[index]).toHaveAccessibleName(
        new RegExp(`^${step.label}`),
      );
    });
    expect(screen.getByRole("button", { name: "Return" })).toHaveAttribute(
      "aria-current",
      "step",
    );
    expect(screen.getByRole("button", { name: "Expenses" })).toBeDisabled();

    fireEvent.click(
      screen.getByRole("button", { name: /Purpose.*completed/ }),
    );
    expect(onSelect).toHaveBeenCalledWith("purpose");
  });

  it("locks completed navigation when the workflow is disabled", () => {
    render(
      <WorkflowProgress
        steps={STANDARD_STEPS}
        currentStepId="outbound"
        completedStepIds={["purpose"]}
        onSelect={vi.fn()}
        isDisabled
      />,
    );

    expect(
      screen.getByRole("button", { name: /Purpose.*completed/ }),
    ).toBeDisabled();
  });
});
