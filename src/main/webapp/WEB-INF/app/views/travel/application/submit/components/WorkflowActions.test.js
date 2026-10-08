import React from "react";
import { fireEvent, render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import WorkflowActions from "./WorkflowActions";

describe("WorkflowActions", () => {
  it("disables the primary action while it is pending", () => {
    const onPrimary = vi.fn();
    render(
      <WorkflowActions
        stepId="outbound"
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
    render(
      <WorkflowActions
        stepId="review"
        onSave={vi.fn()}
        onCancel={vi.fn()}
        isDisabled
      />,
    );

    expect(screen.getByRole("button", { name: "Cancel edits" })).toBeDisabled();
    expect(screen.getByRole("button", { name: "Back" })).toBeDisabled();
    expect(screen.getByRole("button", { name: "Save" })).toBeDisabled();
    expect(
      screen.getByRole("button", { name: "Submit application" }),
    ).toBeDisabled();
  });

  it("hides unavailable save actions and uses the supplied final label", () => {
    const { rerender } = render(
      <WorkflowActions stepId="purpose" onPrimary={vi.fn()} />,
    );

    expect(
      screen.queryByRole("button", { name: "Save" }),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByRole("button", { name: "Cancel edits" }),
    ).not.toBeInTheDocument();

    rerender(
      <WorkflowActions
        stepId="review"
        onPrimary={vi.fn()}
        finalActionLabel="Save and Resubmit"
      />,
    );
    expect(
      screen.getByRole("button", { name: "Save and Resubmit" }),
    ).toBeVisible();
  });

  it("shows cancellation on every step and disables it while committing", () => {
    const onCancel = vi.fn();
    const { rerender } = render(
      <WorkflowActions
        stepId="purpose"
        onCancel={onCancel}
        onPrimary={vi.fn()}
      />,
    );

    fireEvent.click(screen.getByRole("button", { name: "Cancel edits" }));
    expect(onCancel).toHaveBeenCalledOnce();

    rerender(
      <WorkflowActions
        stepId="review"
        onCancel={onCancel}
        onPrimary={vi.fn()}
        isCancelDisabled
      />,
    );
    expect(screen.getByRole("button", { name: "Cancel edits" })).toBeDisabled();
  });

  it("blocks only the final commit during recovery", () => {
    const onBack = vi.fn();
    const onCancel = vi.fn();
    const onPrimary = vi.fn();
    render(
      <WorkflowActions
        stepId="review"
        onBack={onBack}
        onCancel={onCancel}
        onPrimary={onPrimary}
        finalActionLabel="Save and Resubmit"
        isPrimaryDisabled
      />,
    );

    const commit = screen.getByRole("button", { name: "Save and Resubmit" });
    expect(commit).toBeDisabled();
    fireEvent.click(commit);
    expect(onPrimary).not.toHaveBeenCalled();

    fireEvent.click(screen.getByRole("button", { name: "Back" }));
    fireEvent.click(screen.getByRole("button", { name: "Cancel edits" }));
    expect(onBack).toHaveBeenCalledOnce();
    expect(onCancel).toHaveBeenCalledOnce();
  });
});
