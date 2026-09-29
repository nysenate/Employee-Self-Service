import React from "react";
import {
  act,
  fireEvent,
  render,
  screen,
  waitFor,
  within,
} from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { afterEach, expect, it, vi } from "vitest";
import NotificationProvider from "app/components/NotificationProvider";
import AssignmentsTable from "./personnel/pec/to-do-reporting/AssignmentsTable";
import PotentialAssignmentsTable from "./personnel/pec/to-do-assignment/PotentialAssignmentsTable";
import PotentialAssignmentsSummary from "./personnel/pec/to-do-assignment/PotentialAssignmentsSummary";
import AssignmentsSummary from "./personnel/pec/to-do-reporting/AssignmentsSummary";
import {
  useSearchTaskAssignments,
  useSearchPotentialAssignments,
} from "./personnel/pec/useTaskAssignment";
vi.mock("app/hooks/useRequireAuthedUser", () => ({
  default: () => ({ data: { employeeId: 1 } }),
}));
afterEach(() => vi.unstubAllGlobals());

function setup(element) {
  const client = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  const Wrapper = ({ children }) => (
    <NotificationProvider>
      <QueryClientProvider client={client}>{children}</QueryClientProvider>
    </NotificationProvider>
  );
  return { ...render(element, { wrapper: Wrapper }), client };
}
function deferred() {
  let resolve;
  const promise = new Promise((r) => {
    resolve = r;
  });
  return { promise, resolve };
}
const response = (data, ok = true) => ({ ok, json: async () => data });

const employee = {
  employeeId: 2,
  firstName: "Jamie",
  lastName: "Rivera",
  fullName: "Jamie Rivera",
  contServiceDate: "2020-01-01",
};
const task = { taskId: 3, title: "Security training", active: true };
it.each([
  ["Complete Manually", "Confirm Completion", "Task marked complete."],
  ["Deactivate Assignment", "Deactivate Assignment", "Assignment deactivated."],
  ["Assign Task", "Proceed", "Task assigned."],
])(
  "keeps the %s dialog open on failure and blocks dismissal during progress",
  async (openLabel, confirmLabel, success) => {
    const pending = deferred();
    vi.stubGlobal(
      "fetch",
      vi.fn((url) =>
        url === "/api/v1/personnel/task?activeOnly=false"
          ? Promise.resolve(response({ tasks: [task] }))
          : pending.promise,
      ),
    );
    setup(
      openLabel === "Assign Task" ? (
        <PotentialAssignmentsTable
          potentialAssignments={[{ employee, tasks: [task] }]}
        />
      ) : (
        <AssignmentsTable
          taskAssignments={[
            {
              employee,
              incompleteCount: 1,
              incompleteAssignments: [
                {
                  empId: 2,
                  task,
                  canMarkComplete: true,
                  canDeactivateAssignment: true,
                },
              ],
              obsoleteAssignments: [],
              completedAssignments: [],
            },
          ]}
        />
      ),
    );
    fireEvent.click(await screen.findByText(/Rivera, Jamie/));
    fireEvent.click(screen.getByRole("button", { name: openLabel }));
    const dialog = screen.getByRole("dialog");
    fireEvent.click(within(dialog).getByRole("button", { name: confirmLabel }));
    expect(
      await within(dialog).findByText("Updating assignment…"),
    ).toBeVisible();
    expect(
      within(dialog).getByRole("button", { name: "Cancel" }),
    ).toBeDisabled();
    fireEvent.keyDown(dialog, { key: "Escape" });
    expect(dialog).toBeVisible();
    await act(async () => pending.resolve(response({}, false)));
    expect(await within(dialog).findByRole("alert")).toHaveTextContent(
      "Unable to update assignment",
    );
    expect(
      within(dialog).getByRole("button", { name: "Cancel" }),
    ).toBeEnabled();
    expect(screen.queryByText(success)).not.toBeInTheDocument();
    fetch.mockResolvedValue(response({ success: true }));
    fireEvent.click(within(dialog).getByRole("button", { name: confirmLabel }));
    expect(await screen.findByText(success)).toBeVisible();
    expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
  },
);

it("retains reporting rows and the matching CSV filters while searching", async () => {
  const pending = deferred();
  vi.stubGlobal(
    "fetch",
    vi.fn((url) =>
      url.includes("name=New")
        ? pending.promise
        : Promise.resolve(
            response({ result: [{ employee, incompleteCount: 1 }], total: 1 }),
          ),
    ),
  );
  function Results({ name }) {
    const state = { name, respCtrHead: [], limit: 10, offset: 1 };
    const query = useSearchTaskAssignments(state);
    return (
      <AssignmentsSummary
        state={state}
        taskAssignmentQuery={query}
        dispatch={vi.fn()}
      />
    );
  }
  const { rerender } = setup(<Results name="Jamie" />);
  expect(await screen.findByText(/Rivera, Jamie/)).toBeVisible();
  rerender(<Results name="New" />);
  expect(await screen.findByText("Updating results…")).toBeVisible();
  expect(screen.queryByText("1 Matching Employees")).not.toBeInTheDocument();
  expect(screen.getByText(/Rivera, Jamie/).closest("[aria-busy]")).toHaveClass(
    "opacity-60",
  );
  expect(
    screen.getByRole("link", { name: "Download results as CSV" }),
  ).toHaveAttribute("href", expect.stringContaining("name=Jamie"));
  await act(async () => pending.resolve(response({ result: [], total: 0 })));
  expect(await screen.findByText("0 Matching Employees")).toBeVisible();
  expect(screen.queryByText(/Rivera, Jamie/)).not.toBeInTheDocument();
});

it.each(["reporting", "assignment"])(
  "replaces the %s count with progress in the same header",
  (page) => {
    const state = { respCtrHead: [], limit: 10, offset: 1 };
    const query = { data: { result: [], total: 0 } };
    const summary = (query) =>
      page === "reporting" ? (
        <AssignmentsSummary taskAssignmentQuery={query} state={state} />
      ) : (
        <PotentialAssignmentsSummary query={query} state={state} />
      );
    const { rerender } = setup(summary(query));
    const header = screen
      .getByText("0 Matching Employees")
      .closest("[aria-live]");
    for (const isPlaceholderData of [true, false]) {
      rerender(summary({ ...query, isFetching: true, isPlaceholderData }));
      expect(
        screen.queryByText("0 Matching Employees"),
      ).not.toBeInTheDocument();
      expect(
        within(header).getByText(
          isPlaceholderData ? "Updating results…" : "Refreshing assignments…",
        ),
      ).toBeVisible();
    }
    rerender(summary(query));
    expect(within(header).getByText("0 Matching Employees")).toBeVisible();
    expect(
      screen.queryByText("Refreshing assignments…"),
    ).not.toBeInTheDocument();
  },
);

it.each([
  ["reporting", useSearchTaskAssignments],
  ["assignment", useSearchPotentialAssignments],
])(
  "cancels obsolete %s searches when filters change or the page closes",
  async (_page, useSearch) => {
    const requests = [];
    vi.stubGlobal(
      "fetch",
      vi.fn((url, { signal }) => {
        if (url.includes("empActive=true")) {
          return Promise.resolve(response({ result: [], total: 1 }));
        }
        return new Promise((resolve, reject) => {
          requests.push({ signal, resolve });
          signal.addEventListener(
            "abort",
            () => reject(new DOMException("Aborted", "AbortError")),
            { once: true },
          );
        });
      }),
    );
    function Results({ empActive, name = "" }) {
      const query = useSearch({
        empActive,
        name,
        respCtrHead: [],
        limit: 10,
        offset: 1,
      });
      return (
        <div>
          <span>{query.isFetching ? "Searching" : "Ready"}</span>
          <span>{query.data?.total ?? "No results"}</span>
          {query.isError && <span role="alert">Search failed</span>}
        </div>
      );
    }
    const { rerender, unmount, client } = setup(<Results empActive={true} />);
    expect(await screen.findByText("Ready")).toBeVisible();
    rerender(<Results empActive={null} />);
    await waitFor(() => expect(requests).toHaveLength(1));
    expect(screen.getByText("1")).toBeVisible();
    rerender(<Results empActive={null} name="Jamie" />);
    await waitFor(() => expect(requests).toHaveLength(2));
    expect(requests[0].signal.aborted).toBe(true);
    rerender(<Results empActive={true} />);
    expect(requests[1].signal.aborted).toBe(true);
    expect(await screen.findByText("Ready")).toBeVisible();
    expect(screen.queryByRole("alert")).not.toBeInTheDocument();

    // Returning to a cancelled search starts a new request.
    rerender(<Results empActive={null} />);
    await waitFor(() => expect(requests).toHaveLength(3));
    expect(requests[2].signal.aborted).toBe(false);
    unmount();
    expect(requests[2].signal.aborted).toBe(true);
    expect(client.isFetching()).toBe(0);
  },
);
