import React from "react";
import { render, screen } from "@testing-library/react";
import { beforeEach, expect, it, vi } from "vitest";
import { useConfig } from "app/hooks/useConfig";
import FeedbackPlaygroundRoute from "./FeedbackPlaygroundRoute";

const { mounted } = vi.hoisted(() => ({ mounted: vi.fn() }));
vi.mock("app/hooks/useConfig", () => ({ useConfig: vi.fn() }));
vi.mock("./FeedbackPlayground", () => ({
  default: () => {
    mounted();
    return <h1>Feedback playground</h1>;
  },
}));

beforeEach(() => vi.clearAllMocks());

it.each(["prod", "test", "DEV", undefined])(
  "does not mount the playground for runtime %s",
  (runtimeLevel) => {
    useConfig.mockReturnValue({ data: { runtimeLevel } });
    render(<FeedbackPlaygroundRoute />);
    expect(
      screen.getByRole("heading", { name: "Page not found" }),
    ).toBeVisible();
    expect(mounted).not.toHaveBeenCalled();
  },
);

it("waits for config and refuses a failed check even with stale dev data", () => {
  useConfig.mockReturnValue({ isPending: true });
  const { rerender } = render(<FeedbackPlaygroundRoute />);
  expect(screen.getByRole("status")).toHaveTextContent(
    "Loading feedback playground",
  );
  expect(mounted).not.toHaveBeenCalled();
  useConfig.mockReturnValue({ isError: true, data: { runtimeLevel: "dev" } });
  rerender(<FeedbackPlaygroundRoute />);
  expect(screen.getByRole("heading", { name: "Page not found" })).toBeVisible();
  expect(mounted).not.toHaveBeenCalled();
});

it("loads the playground in the development runtime", async () => {
  useConfig.mockReturnValue({ data: { runtimeLevel: "dev" } });
  render(<FeedbackPlaygroundRoute />);
  expect(
    await screen.findByRole("heading", { name: "Feedback playground" }),
  ).toBeVisible();
  expect(mounted).toHaveBeenCalled();
});
