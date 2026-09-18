import React from "react";
import { fireEvent, render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import ResubmissionError from "./ResubmissionError";

function renderError(type, props = {}) {
  const onCheckStatus = vi.fn();
  const onReturn = vi.fn();
  render(
    <ResubmissionError
      error={new Error("commit failed")}
      recovery={{ type }}
      onCheckStatus={onCheckStatus}
      onReturn={onReturn}
      {...props}
    />,
  );
  return { onCheckStatus, onReturn };
}

describe("ResubmissionError", () => {
  it("allows correction errors to be retried without blocking copy", () => {
    renderError("correction");

    expect(screen.getByRole("alert")).toHaveTextContent(
      "Review your changes and try again.",
    );
    expect(
      screen.queryByRole("button", { name: "Check application status" }),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByRole("button", { name: "Return to Travel History" }),
    ).not.toBeInTheDocument();
  });

  it.each([
    ["conflict", "This application is no longer available for resubmission."],
    [
      "access",
      "You no longer have access to resubmit this travel application.",
    ],
  ])(
    "renders the blocked %s outcome with a history action",
    (type, message) => {
      const { onReturn } = renderError(type);

      expect(screen.getByRole("alert")).toHaveTextContent(message);
      fireEvent.click(
        screen.getByRole("button", { name: "Return to Travel History" }),
      );
      expect(onReturn).toHaveBeenCalledOnce();
    },
  );

  it("offers a status check for an unknown outcome", () => {
    const { onCheckStatus, onReturn } = renderError("unknown-outcome");

    expect(screen.getByRole("alert")).toHaveTextContent(
      "We could not confirm whether your application was resubmitted.",
    );
    fireEvent.click(
      screen.getByRole("button", { name: "Check application status" }),
    );
    fireEvent.click(
      screen.getByRole("button", { name: "Return to Travel History" }),
    );
    expect(onCheckStatus).toHaveBeenCalledOnce();
    expect(onReturn).toHaveBeenCalledOnce();
  });

  it("shows a pending status check without enabling another click", () => {
    const { onCheckStatus } = renderError("unknown-outcome", {
      isCheckingStatus: true,
    });

    const checkButton = screen.getByRole("button", {
      name: "Check application status",
    });
    expect(checkButton).toBeDisabled();
    fireEvent.click(checkButton);
    expect(onCheckStatus).not.toHaveBeenCalled();
  });

  it("explains that a disapproved status permits a deliberate retry", () => {
    renderError("retryable-status");

    expect(screen.getByRole("alert")).toHaveTextContent(
      "You can try to resubmit your changes again.",
    );
    expect(
      screen.queryByRole("button", { name: "Check application status" }),
    ).not.toBeInTheDocument();
  });

  it("does not infer saved edits from a changed application status", () => {
    renderError("status-changed");

    expect(screen.getByRole("alert")).toHaveTextContent(
      "This status check cannot confirm whether your edits were saved.",
    );
    expect(
      screen.getByRole("button", { name: "Return to Travel History" }),
    ).toBeEnabled();
  });

  it("keeps a failed status check recoverable", () => {
    const { onCheckStatus } = renderError("status-check-failed");

    expect(screen.getByRole("alert")).toHaveTextContent(
      "Try checking its status again before resubmitting.",
    );
    fireEvent.click(
      screen.getByRole("button", { name: "Check application status" }),
    );
    expect(onCheckStatus).toHaveBeenCalledOnce();
  });
});
