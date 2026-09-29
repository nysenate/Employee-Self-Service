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
import { MemoryRouter } from "react-router-dom";
import DocumentAcknowledgeAssignment from "./personnel/pec/task-assignments/assignment-item/document-assignment/DocumentAcknowledgeAssignment";
import NotificationProvider from "app/components/NotificationProvider";
import EmergencyAlertInfoIndex from "./personnel/emergency-alert-info/EmergencyAlertInfoIndex";
import AlertInfoForm from "./personnel/emergency-alert-info/AlertInfoForm";
import VideoCodeEntryForm from "./personnel/pec/task-assignments/assignment-item/video-assignment/VideoCodeEntryForm";
import EthicsLiveCodeEntryForm from "./personnel/pec/task-assignments/assignment-item/ethics-live-assignment/EthicsLiveCodeEntryForm";
import CheckHistoryForm from "./payroll/checkhistory/CheckHistoryForm";

vi.mock("app/hooks/useRequireAuthedUser", () => ({
  default: () => ({ data: { employeeId: 1 } }),
}));
vi.mock("./payroll/checkhistory/Paycheck", () => ({
  default: ({ summary }) => <div>{summary.paychecks[0].label}</div>,
}));

afterEach(() => {
  vi.unstubAllGlobals();
  vi.restoreAllMocks();
});

const pdf = vi.hoisted(() => ({
  getDocument: vi.fn(),
  GlobalWorkerOptions: {},
}));
vi.mock("pdfjs-dist", () => pdf);

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

it("keeps emergency contact edits on refresh and reports confirmed saves", async () => {
  const pending = deferred();
  vi.stubGlobal(
    "fetch",
    vi.fn(() => pending.promise),
  );
  const { rerender } = setup(
    <AlertInfoForm alertInfo={{ empId: 1, homePhone: "5185550100" }} />,
  );
  const home = screen.getByLabelText("Home");
  fireEvent.change(home, { target: { value: "5185550101" } });
  fireEvent.blur(home);
  rerender(<AlertInfoForm alertInfo={{ empId: 1, homePhone: "5185550199" }} />);
  expect(home).toHaveValue("5185550101");
  await waitFor(() =>
    expect(screen.getByRole("button", { name: "Save" })).toBeEnabled(),
  );
  fireEvent.click(screen.getByRole("button", { name: "Save" }));
  await waitFor(() =>
    expect(screen.getByRole("button", { name: "Save" })).toBeDisabled(),
  );
  expect(
    screen.queryByText("Saving emergency contact information…"),
  ).not.toBeInTheDocument();
  expect(home).toBeDisabled();
  expect(
    screen.queryByText("Emergency contact information saved."),
  ).not.toBeInTheDocument();
  await act(async () => pending.resolve(response({ success: true })));
  expect(
    await screen.findByText("Emergency contact information saved."),
  ).toBeVisible();
  expect(home).toHaveValue("5185550101");
  expect(screen.getByRole("button", { name: "Save" })).toBeDisabled();
});

it.each([
  ["video", VideoCodeEntryForm],
  ["ethics", EthicsLiveCodeEntryForm],
])(
  "preserves codes and displays request failures in %s",
  async (_name, Form) => {
    const pending = deferred();
    vi.stubGlobal(
      "fetch",
      vi.fn(() => pending.promise),
    );
    const onSuccess = vi.fn();
    setup(<Form taskId={2} onSuccess={onSuccess} />);
    for (const label of ["First Code", "Second Code"]) {
      fireEvent.change(screen.getByLabelText(label), {
        target: { value: "1234" },
      });
    }
    if (Form === EthicsLiveCodeEntryForm) {
      fireEvent.change(screen.getByLabelText("Training Date"), {
        target: { value: "2026-09-01" },
      });
    }
    fireEvent.click(screen.getByRole("button", { name: "Submit" }));
    expect(await screen.findByText("Submitting training codes…")).toBeVisible();
    expect(screen.getByLabelText("First Code")).toBeDisabled();
    await act(async () => pending.resolve(response({}, false)));
    expect(await screen.findByRole("alert")).toHaveTextContent(
      "Unable to submit codes",
    );
    expect(screen.getByLabelText("First Code")).toHaveValue("1234");
    expect(screen.getByRole("button", { name: "Submit" })).toBeEnabled();
    expect(onSuccess).not.toHaveBeenCalled();
  },
);

it("retains paychecks and shows filter progress inside the stable card header", async () => {
  const pending = deferred();
  vi.stubGlobal(
    "fetch",
    vi.fn((url) =>
      url.includes("year=2026")
        ? Promise.resolve(
            response({ result: { paychecks: [{ label: "Old paycheck" }] } }),
          )
        : pending.promise,
    ),
  );
  setup(
    <CheckHistoryForm
      empId={1}
      calendarYears={[2025, 2026]}
      fiscalYears={[2026]}
    />,
  );
  expect(await screen.findByText("Old paycheck")).toBeVisible();
  const header = screen
    .getByText("2026 Paycheck Records")
    .closest("[aria-live]");
  fireEvent.change(screen.getByLabelText("Filter by year"), {
    target: { value: "2025" },
  });
  expect(await screen.findByText("Updating paychecks…")).toBeVisible();
  expect(within(header).getByText("Updating paychecks…")).toBeVisible();
  expect(header).toHaveClass("min-h-7");
  expect(screen.queryByText("2026 Paycheck Records")).not.toBeInTheDocument();
  expect(screen.getByText("Old paycheck").closest("[aria-busy]")).toHaveClass(
    "opacity-60",
  );
  await act(async () =>
    pending.resolve(
      response({ result: { paychecks: [{ label: "New paycheck" }] } }),
    ),
  );
  expect(
    await within(header).findByText("2025 Paycheck Records"),
  ).toBeVisible();
  expect(screen.getByText("New paycheck")).toBeVisible();
});

it("waits for PDF rendering before acknowledgement", async () => {
  const rendering = deferred();
  vi.spyOn(HTMLCanvasElement.prototype, "getContext").mockReturnValue({});
  pdf.getDocument.mockReturnValue({
    promise: Promise.resolve({
      numPages: 1,
      getPage: async () => ({
        getViewport: () => ({ width: 500, height: 700 }),
        render: () => ({ promise: rendering.promise, cancel: vi.fn() }),
      }),
    }),
    destroy: vi.fn(),
  });
  setup(
    <MemoryRouter>
      <DocumentAcknowledgeAssignment
        assignment={{
          taskId: 3,
          task: { title: "Policy", path: "/policy.pdf" },
        }}
      />
    </MemoryRouter>,
  );
  fireEvent.scroll(document);
  expect(screen.getByRole("button", { name: "Acknowledge" })).toBeDisabled();
  expect(await screen.findByText("Loading document…")).toBeVisible();
  await act(async () => rendering.resolve());
  await waitFor(() =>
    expect(screen.queryByText("Loading document…")).not.toBeInTheDocument(),
  );
  expect(screen.getByRole("button", { name: "Acknowledge" })).toBeEnabled();
});

it("shows document loading errors and keeps acknowledgement disabled", async () => {
  vi.spyOn(console, "error").mockImplementation(() => {});
  pdf.getDocument.mockReturnValue({
    promise: Promise.reject(new Error("Failed")),
    destroy: vi.fn(),
  });
  setup(
    <MemoryRouter>
      <DocumentAcknowledgeAssignment
        assignment={{
          taskId: 3,
          task: { title: "Policy", path: "/policy.pdf" },
        }}
      />
    </MemoryRouter>,
  );
  expect(await screen.findByRole("alert")).toHaveTextContent(
    "Unable to display document",
  );
  fireEvent.scroll(document);
  expect(screen.getByRole("button", { name: "Acknowledge" })).toBeDisabled();
});

it("keeps the same contact form and values through a save and delayed refresh", async () => {
  const save = deferred();
  const refresh = deferred();
  let reads = 0;
  vi.stubGlobal(
    "fetch",
    vi.fn((_url, options = {}) => {
      if (options.method === "POST") return save.promise;
      reads += 1;
      return reads === 1
        ? Promise.resolve(
            response({ result: { empId: 1, homePhone: "5185550100" } }),
          )
        : refresh.promise;
    }),
  );
  setup(<EmergencyAlertInfoIndex />);
  const home = await screen.findByLabelText("Home");
  const form = home.closest("form");
  fireEvent.change(home, { target: { value: "5185550101" } });
  fireEvent.blur(home);
  await waitFor(() =>
    expect(screen.getByRole("button", { name: "Save" })).toBeEnabled(),
  );
  fireEvent.click(screen.getByRole("button", { name: "Save" }));
  await waitFor(() => expect(home).toBeDisabled());
  await act(async () => save.resolve(response({ success: true })));
  await waitFor(() => expect(reads).toBe(2));
  expect(
    screen.queryByText("Refreshing emergency contact information…"),
  ).not.toBeInTheDocument();
  expect(screen.getByLabelText("Home")).toBe(home);
  expect(home.closest("form")).toBe(form);
  expect(home).toHaveValue("5185550101");
  expect(
    screen.queryByText("Loading emergency contact information…"),
  ).not.toBeInTheDocument();
  await act(async () =>
    refresh.resolve(
      response({ result: { empId: 1, homePhone: "5185550101" } }),
    ),
  );
  expect(
    await screen.findByText("Emergency contact information saved."),
  ).toBeVisible();
  expect(screen.getByLabelText("Home")).toBe(home);
  expect(home).toHaveValue("(518) 555-0101");
  expect(home).toBeEnabled();
});
