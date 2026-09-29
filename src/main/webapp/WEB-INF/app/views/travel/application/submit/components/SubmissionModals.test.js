import React from "react";
import { fireEvent, render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import {
  ApplicationSubmissionDialog,
  SubmissionSuccessModal,
} from "./SubmissionModals";

describe("submission modals", () => {
  it("requires explicit confirmation and supports cancellation", () => {
    const onCancel = vi.fn();
    const onConfirm = vi.fn();
    render(
      <ApplicationSubmissionDialog
        isOpen
        onCancel={onCancel}
        onConfirm={onConfirm}
      />,
    );

    expect(screen.getByRole("dialog")).toHaveAccessibleName(
      "Submit travel application?",
    );
    fireEvent.click(screen.getByRole("button", { name: "Cancel" }));
    expect(onCancel).toHaveBeenCalledOnce();
    expect(onConfirm).not.toHaveBeenCalled();
  });

  it("offers both required choices after success", () => {
    const onReturn = vi.fn();
    const onLogout = vi.fn();
    render(
      <SubmissionSuccessModal isOpen onReturn={onReturn} onLogout={onLogout} />,
    );

    expect(screen.getByRole("dialog")).toHaveAccessibleName(
      "Application submitted",
    );
    expect(
      screen.queryByRole("button", { name: /close/i }),
    ).not.toBeInTheDocument();
    fireEvent.click(screen.getByRole("button", { name: "Go back to ESS" }));
    fireEvent.click(screen.getByRole("button", { name: "Log out of ESS" }));
    expect(onReturn).toHaveBeenCalledOnce();
    expect(onLogout).toHaveBeenCalledOnce();
  });

  it("renders operation-specific confirmation copy", () => {
    render(
      <ApplicationSubmissionDialog
        isOpen
        onCancel={vi.fn()}
        onConfirm={vi.fn()}
        title="Resubmit travel application?"
        body="Review this application again."
        actionLabel="Save and Resubmit"
      />,
    );

    expect(screen.getByRole("dialog")).toHaveAccessibleName(
      "Resubmit travel application?",
    );
    expect(screen.getByText("Review this application again.")).toBeVisible();
    expect(
      screen.getByRole("button", { name: "Save and Resubmit" }),
    ).toBeVisible();
  });
});
