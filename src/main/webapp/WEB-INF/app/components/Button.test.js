import React from "react";
import { fireEvent, render, screen } from "@testing-library/react";
import { expect, it, vi } from "vitest";
import Button from "./Button";

it("keeps its action name and blocks repeated presses while pending", () => {
  const onPress = vi.fn();
  const { rerender } = render(
    <Button onPress={onPress} isPending>
      Save changes
    </Button>,
  );
  const button = screen.getByRole("button", { name: "Save changes" });
  expect(button).toBeDisabled();
  expect(button.querySelector("svg")).toHaveAttribute("aria-hidden", "true");
  fireEvent.click(button);
  expect(onPress).not.toHaveBeenCalled();
  rerender(<Button onPress={onPress}>Save changes</Button>);
  expect(button).toBeEnabled();
  expect(button.querySelector("svg")).toBeNull();
  fireEvent.click(button);
  expect(onPress).toHaveBeenCalledOnce();
});

it("preserves explicit accessible labels and disabled behavior", () => {
  const onPress = vi.fn();
  render(
    <Button aria-label="Delete draft" isDisabled onPress={onPress}>
      ×
    </Button>,
  );
  fireEvent.click(screen.getByRole("button", { name: "Delete draft" }));
  expect(onPress).not.toHaveBeenCalled();
});
