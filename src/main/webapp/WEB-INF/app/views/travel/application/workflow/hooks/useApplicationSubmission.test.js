import { act, renderHook } from "@testing-library/react";
import { expect, it, vi } from "vitest";
import { useApplicationSubmission } from "./useApplicationSubmission";

function setup(overrides = {}) {
  const commit = {
    execute: vi.fn(),
    isPending: false,
    isDisabled: false,
    ...overrides,
  };
  const props = { draft: { id: 1 }, commit };
  return {
    ...renderHook((props) => useApplicationSubmission(props), {
      initialProps: props,
    }),
    props,
    commit,
  };
}

it("requires confirmation and allows cancellation without committing", async () => {
  const { result, commit } = setup();
  await act(() => result.current.confirm());
  expect(commit.execute).not.toHaveBeenCalled();
  act(() => result.current.requestConfirmation());
  expect(result.current.status).toBe("confirming");
  act(() => result.current.cancelConfirmation());
  await act(() => result.current.confirm());
  expect(result.current.status).toBe("idle");
  expect(commit.execute).not.toHaveBeenCalled();
});

it("commits once, stays locked during the request, and exposes the completion result", async () => {
  let resolve;
  const request = new Promise((done) => {
    resolve = done;
  });
  const { result, commit } = setup({ execute: vi.fn(() => request) });
  act(() => result.current.requestConfirmation());
  let pending;
  act(() => {
    pending = result.current.confirm();
    result.current.confirm();
    result.current.cancelConfirmation();
  });
  expect(commit.execute).toHaveBeenCalledTimes(1);
  expect(result.current.status).toBe("submitting");
  expect(result.current.isLocked).toBe(true);
  act(() => result.current.requestConfirmation());
  await act(async () => {
    resolve({ id: 42 });
    await pending;
  });
  expect(result.current.status).toBe("success");
  expect(result.current.result).toEqual({ id: 42 });
  expect(result.current.isLocked).toBe(true);
  act(() => result.current.requestConfirmation());
  await act(() => result.current.confirm());
  expect(commit.execute).toHaveBeenCalledTimes(1);
});

it("exposes errors, unlocks, and requires fresh confirmation before retry", async () => {
  const error = new Error("failed");
  const { result, commit } = setup({
    execute: vi.fn().mockRejectedValueOnce(error).mockResolvedValue({ id: 2 }),
  });
  act(() => result.current.requestConfirmation());
  await act(() => result.current.confirm());
  expect(result.current.error).toBe(error);
  expect(result.current.isLocked).toBe(false);
  expect(result.current.result).toBeNull();
  await act(() => result.current.confirm());
  expect(commit.execute).toHaveBeenCalledTimes(1);
  act(() => result.current.requestConfirmation());
  expect(result.current.error).toBeNull();
  await act(() => result.current.confirm());
  expect(result.current.status).toBe("success");
});

it.each(["isPending", "isDisabled"])(
  "honors commit.%s before opening and confirming",
  async (flag) => {
    const { result, rerender, props, commit } = setup({ [flag]: true });
    act(() => result.current.requestConfirmation());
    expect(result.current.status).toBe("idle");
    rerender({ ...props, commit: { ...commit, [flag]: false } });
    act(() => result.current.requestConfirmation());
    rerender(props);
    await act(() => result.current.confirm());
    expect(commit.execute).not.toHaveBeenCalled();
  },
);

it("commits the current draft and adapter after rerender", async () => {
  const { result, rerender, props, commit } = setup();
  act(() => result.current.requestConfirmation());
  const execute = vi.fn().mockResolvedValue({ id: 3 });
  const draft = { id: 1, amendment: { totalAllowance: 35 } };
  rerender({ draft, commit: { ...props.commit, execute } });
  await act(() => result.current.confirm());
  expect(execute).toHaveBeenCalledWith(draft);
  expect(commit.execute).not.toHaveBeenCalled();
});
