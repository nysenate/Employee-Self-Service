import React from "react";
import { fireEvent, render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import CancelEditsModal from "./CancelEditsModal";

describe("CancelEditsModal", () => {
  it("keeps editing without discarding", () => {
    const onKeepEditing = vi.fn();
    const onDiscard = vi.fn();
    render(
      <CancelEditsModal
        isOpen
        onKeepEditing={onKeepEditing}
        onDiscard={onDiscard}
      />,
    );

    expect(screen.getByRole("dialog")).toHaveAccessibleName(
      "Cancel editing this travel application?",
    );
    expect(
      screen.getByText("Any changes you have made will be lost."),
    ).toBeVisible();
    fireEvent.click(screen.getByRole("button", { name: "Keep editing" }));

    expect(onKeepEditing).toHaveBeenCalledOnce();
    expect(onDiscard).not.toHaveBeenCalled();
  });

  it("requires an enabled explicit discard", () => {
    const onDiscard = vi.fn();
    const { rerender } = render(
      <CancelEditsModal
        isOpen
        onKeepEditing={vi.fn()}
        onDiscard={onDiscard}
        isDiscardDisabled
      />,
    );

    const discard = screen.getByRole("button", { name: "Discard edits" });
    expect(discard).toBeDisabled();
    fireEvent.click(discard);
    expect(onDiscard).not.toHaveBeenCalled();

    rerender(
      <CancelEditsModal
        isOpen
        onKeepEditing={vi.fn()}
        onDiscard={onDiscard}
      />,
    );
    fireEvent.click(screen.getByRole("button", { name: "Discard edits" }));
    expect(onDiscard).toHaveBeenCalledOnce();
  });
});
