import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import {
  act,
  fireEvent,
  render,
  screen,
  waitFor,
} from "@testing-library/react";
import { afterEach, describe, expect, it, vi } from "vitest";
import { ErrorBoundary } from "react-error-boundary";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import NotificationProvider from "app/components/NotificationProvider";
import TravelRouter from "app/views/travel/TravelRouter";
import { travelQueryKeys } from "app/views/travel/shared/hooks/travelQueryKeys";
import { REVIEW_QUEUE_QUERY_KEY } from "./reviewQueueCache";

vi.mock("app/hooks/useCheckPermission", () => ({
  default: () => ({ isAllowed: true, isChecking: false }),
}));

afterEach(() => vi.unstubAllGlobals());

const eventType = { name: "Forum", displayName: "Forum" };
const roles = {
  allRoles: [
    { name: "DEPARTMENT_HEAD", displayName: "Department Head" },
    { name: "TRAVEL_ADMIN", displayName: "Travel Administrator" },
  ],
};
const pendingReview = {
  appReviewId: 99,
  application: {
    id: 42,
    startDate: "2026-10-15",
    travelerName: "Jamie Rivera",
    destinationSummary: "Albany",
    totalAllowance: 100,
    status: { name: "DEPARTMENT_HEAD", isPending: true },
  },
};
const queue = { DEPARTMENT_HEAD: [pendingReview], TRAVEL_ADMIN: [] };
const review = {
  appReviewId: 99,
  pendingReviewerRole: "DEPARTMENT_HEAD",
  actions: [],
  travelApplication: {
    id: 42,
    traveler: { fullName: "Jamie Rivera" },
    activeAmendment: {
      purposeOfTravel: { summary: "Regional policy forum" },
      route: { outboundLegs: [], destinations: [] },
      attachments: [],
    },
    status: { name: "DEPARTMENT_HEAD", isPending: true },
  },
};

function response(result) {
  return Promise.resolve({ ok: true, json: async () => ({ result }) });
}

function mockApi(getQueue) {
  const fetchMock = vi.fn((url, options) => {
    if (url.endsWith("/travel/review/pending")) return getQueue();
    if (url.endsWith("/travel/roles")) return response(roles);
    if (url.endsWith("/travel/review/99")) return response(review);
    if (url.endsWith("/travel/event-types")) return response([eventType]);
    if (url.endsWith("/travel/mode-of-transportation")) return response([]);
    if (url.endsWith("/travel/drafts") && options.method === "POST") {
      return response({ ...JSON.parse(options.body), id: 88 });
    }
    throw new Error(`Unexpected request: ${url}`);
  });
  vi.stubGlobal("fetch", fetchMock);
  return fetchMock;
}

function renderTravel(path) {
  const client = new QueryClient({
    defaultOptions: { queries: { retry: false, throwOnError: true } },
  });
  client.setQueryData(travelQueryKeys.newDraft(), {
    traveler: { fullName: "Jamie Rivera" },
    amendment: { purposeOfTravel: { eventType } },
  });
  render(
    <QueryClientProvider client={client}>
      <NotificationProvider>
        <ErrorBoundary fallback={<p>Entire app error fallback</p>}>
          <MemoryRouter initialEntries={[path]}>
            <Routes>
              <Route path="/travel/*" element={<TravelRouter />} />
            </Routes>
          </MemoryRouter>
        </ErrorBoundary>
      </NotificationProvider>
    </QueryClientProvider>,
  );
  return client;
}

async function refreshQueue(client) {
  await act(async () => {
    await client.invalidateQueries({ queryKey: REVIEW_QUEUE_QUERY_KEY });
  });
}

describe("review queue error isolation", () => {
  it.each(["initial load", "background refresh"])(
    "preserves unsaved application edits when the badge's %s fails",
    async (failureStage) => {
      let rejectInitialRequest;
      let getQueue =
        failureStage === "initial load"
          ? () =>
              new Promise((_resolve, reject) => {
                rejectInitialRequest = reject;
              })
          : () => response({ items: queue });
      const fetchMock = mockApi(() => getQueue());
      const client = renderTravel("/travel/applications/new");
      await screen.findByRole("option", { name: "Forum" });
      if (failureStage === "background refresh") {
        expect(
          await screen.findByTitle("Travel applications awaiting review"),
        ).toHaveTextContent("1");
      }
      const input = screen.getByLabelText("Additional information (optional)");
      fireEvent.change(input, { target: { value: "Unsaved trip details" } });

      if (failureStage === "initial load") {
        await act(async () =>
          rejectInitialRequest(new Error("Queue unavailable")),
        );
      } else {
        getQueue = () => Promise.reject(new Error("Queue unavailable"));
        await refreshQueue(client);
      }
      await waitFor(() =>
        expect(client.getQueryState(REVIEW_QUEUE_QUERY_KEY).status).toBe(
          "error",
        ),
      );
      await waitFor(() =>
        expect(
          screen.queryByTitle("Travel applications awaiting review"),
        ).not.toBeInTheDocument(),
      );
      expect(
        screen.queryByText("Entire app error fallback"),
      ).not.toBeInTheDocument();
      expect(screen.getByLabelText("Additional information (optional)")).toBe(
        input,
      );
      expect(input).toHaveValue("Unsaved trip details");

      getQueue = () => response({ items: queue });
      await refreshQueue(client);
      expect(
        await screen.findByTitle("Travel applications awaiting review"),
      ).toHaveTextContent("1");
      expect(input).toHaveValue("Unsaved trip details");
      fireEvent.click(screen.getByRole("button", { name: "Save" }));
      await screen.findByText("Draft saved");
      const save = fetchMock.mock.calls.find(
        ([url, options]) =>
          url.endsWith("/travel/drafts") && options.method === "POST",
      );
      expect(
        JSON.parse(save[1].body).amendment.purposeOfTravel.additionalPurpose,
      ).toBe("Unsaved trip details");
    },
  );

  it("shows an initial queue error locally and retries without reporting an empty queue", async () => {
    let getQueue = () => Promise.reject(new Error("Queue unavailable"));
    mockApi(() => getQueue());
    renderTravel("/travel/manage/queue");

    expect(await screen.findByRole("alert")).toHaveTextContent(
      "Unable to load review queue",
    );
    expect(
      screen.queryByText(/No applications awaiting/),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByText("Entire app error fallback"),
    ).not.toBeInTheDocument();

    let resolveRetry;
    getQueue = () =>
      new Promise((resolve) => {
        resolveRetry = resolve;
      });
    fireEvent.click(screen.getByRole("button", { name: "Try again" }));
    expect(await screen.findByRole("status")).toHaveTextContent(
      "Loading review queue",
    );
    expect(
      screen.queryByText(/No applications awaiting/),
    ).not.toBeInTheDocument();
    await act(async () => resolveRetry(await response({ items: queue })));

    expect(
      await screen.findByRole("button", { name: /Review Jamie Rivera/ }),
    ).toBeVisible();
    expect(screen.queryByRole("alert")).not.toBeInTheDocument();
    expect(
      await screen.findByTitle("Travel applications awaiting review"),
    ).toHaveTextContent("1");
  });

  it("keeps cached queue results and unsaved review notes mounted during a failed refresh and recovery", async () => {
    let getQueue = () => response({ items: queue });
    mockApi(() => getQueue());
    const client = renderTravel("/travel/manage/queue");
    const row = await screen.findByRole("button", {
      name: /Review Jamie Rivera/,
    });
    const roleSelect = screen.getByRole("button", { name: /Reviewing as/ });
    expect(roleSelect).toHaveTextContent("Department Head");
    fireEvent.click(row);
    fireEvent.click(
      await screen.findByRole("button", { name: "Disapprove Application" }),
    );
    const notes = await screen.findByLabelText("Disapproval notes*");
    fireEvent.change(notes, { target: { value: "Unsaved reviewer notes" } });

    getQueue = () => Promise.reject(new Error("Queue unavailable"));
    await refreshQueue(client);

    expect(
      await screen.findByText("Unable to refresh review queue"),
    ).toBeInTheDocument();
    expect(row).toBeInTheDocument();
    expect(roleSelect).toHaveTextContent("Department Head");
    expect(screen.getByLabelText("Disapproval notes*")).toBe(notes);
    expect(notes).toHaveValue("Unsaved reviewer notes");
    expect(
      screen.queryByText("Entire app error fallback"),
    ).not.toBeInTheDocument();

    getQueue = () => response({ items: queue });
    await refreshQueue(client);
    await waitFor(() =>
      expect(
        screen.queryByText("Unable to refresh review queue"),
      ).not.toBeInTheDocument(),
    );
    expect(screen.getByLabelText("Disapproval notes*")).toBe(notes);
    expect(notes).toHaveValue("Unsaved reviewer notes");
  });
});
