import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { afterEach, describe, expect, it, vi } from "vitest";
import TravelApplicationModal from "./TravelApplicationModal";
import { travelQueryKeys } from "app/views/travel/shared/hooks/travelQueryKeys";

afterEach(() => vi.unstubAllGlobals());

function fullApplication() {
  return {
    id: 42,
    submittedDateTime: "2025-05-10T09:30:00",
    traveler: {
      nid: "N0042",
      fullName: "Full Detail Traveler",
      jobTitle: "Analyst",
      workPhone: "518-555-0100",
      respCtr: {
        agencyCode: "SEN",
        respCenterHead: { name: "Legislative Services" },
      },
      empWorkLocation: {
        address: { formattedAddressWithCounty: "Albany, NY, Albany County" },
      },
    },
    activeAmendment: {
      startDate: "2025-06-01",
      endDate: "2025-06-02",
      purposeOfTravel: { summary: "Full detail purpose" },
      route: {
        origin: { formattedAddressWithCounty: "Albany, NY, Albany County" },
        destinations: [
          {
            id: 1,
            address: {
              formattedAddressWithCounty: "Buffalo, NY, Erie County",
            },
          },
        ],
        outboundLegs: [],
      },
      attachments: [],
      transportationAllowance: 10,
      mealAllowance: 20,
      lodgingAllowance: 30,
      tollsAndParkingAllowance: 5,
      alternateTransportationAllowance: 0,
      registrationAllowance: 25,
      totalAllowance: 90,
    },
    status: {
      name: "DISAPPROVED",
      label: "Disapproved",
      isDisapproved: true,
      note: "Receipts were incomplete",
    },
  };
}

function response(body, { ok = true, status = 200 } = {}) {
  return Promise.resolve({
    ok,
    status,
    statusText: ok ? "" : "Request failed",
    json: async () => body,
  });
}

function renderModal({
  appId = 42,
  onClose = vi.fn(),
  onResubmit = vi.fn(),
  queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  }),
} = {}) {
  render(
    <QueryClientProvider client={queryClient}>
      <TravelApplicationModal
        appId={appId}
        onClose={onClose}
        onResubmit={onResubmit}
      />
    </QueryClientProvider>,
  );
  return { onClose, onResubmit };
}

describe("TravelApplicationModal", () => {
  it("renders independently fetched full details, status, reason, and print", async () => {
    const fetchMock = vi.fn((url) => {
      if (url.endsWith("/travel/applications/42")) {
        return response({ result: fullApplication() });
      }
      return response({ result: [] });
    });
    vi.stubGlobal("fetch", fetchMock);
    const { onClose, onResubmit } = renderModal();

    expect(await screen.findByText("Full detail purpose")).toBeVisible();
    expect(screen.getByText(/Full Detail Traveler/)).toBeVisible();
    expect(screen.getByText("Disapproved")).toBeVisible();
    expect(screen.getByText("Receipts were incomplete")).toBeVisible();
    expect(screen.getByRole("link", { name: "Print" })).toHaveAttribute(
      "href",
      `${window.location.origin}/api/v1/travel/applications/42.pdf`,
    );
    fireEvent.click(screen.getByRole("button", { name: "Edit and Resubmit" }));
    expect(onResubmit).toHaveBeenCalledOnce();
    expect(onResubmit).toHaveBeenCalledWith(42);

    fireEvent.click(screen.getByRole("button", { name: "Close" }));
    expect(onClose).toHaveBeenCalledOnce();
  });

  it("shows no application content before the detail request succeeds", () => {
    vi.stubGlobal(
      "fetch",
      vi.fn(() => new Promise(() => {})),
    );
    renderModal();

    expect(screen.getByRole("dialog")).toHaveAccessibleName(
      "Travel application details",
    );
    expect(screen.queryByText("Full detail purpose")).not.toBeInTheDocument();
    expect(
      screen.queryByRole("link", { name: "Print" }),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByRole("button", { name: "Edit and Resubmit" }),
    ).not.toBeInTheDocument();
  });

  it("can retry or close after an inaccessible detail response", async () => {
    const fetchMock = vi
      .fn()
      .mockReturnValueOnce(
        response({ message: "Denied" }, { ok: false, status: 403 }),
      )
      .mockReturnValueOnce(response({ result: fullApplication() }))
      .mockReturnValue(response({ result: [] }));
    vi.stubGlobal("fetch", fetchMock);
    const { onClose } = renderModal();

    expect(
      await screen.findByRole("heading", {
        name: "We couldn’t load this travel application",
      }),
    ).toBeVisible();
    expect(
      screen.queryByRole("button", { name: "Edit and Resubmit" }),
    ).not.toBeInTheDocument();
    fireEvent.click(screen.getByRole("button", { name: "Retry" }));
    expect(await screen.findByText("Full detail purpose")).toBeVisible();
    expect(fetchMock).toHaveBeenCalledTimes(3);

    fireEvent.click(screen.getByRole("button", { name: "Close" }));
    expect(onClose).toHaveBeenCalledOnce();
  });

  it.each(["PENDING", "APPROVED", "CANCELED"])(
    "hides resubmission for a %s full-detail status",
    async (name) => {
      vi.stubGlobal(
        "fetch",
        vi.fn(() =>
          response({
            result: {
              ...fullApplication(),
              status: { name, label: name, isDisapproved: false },
            },
          }),
        ),
      );
      renderModal();

      expect(await screen.findByText("Full detail purpose")).toBeVisible();
      expect(
        screen.queryByRole("button", { name: "Edit and Resubmit" }),
      ).not.toBeInTheDocument();
    },
  );

  it("hides the action while cached disapproved detail is being refreshed", async () => {
    let resolveDetail;
    const pending = new Promise((resolve) => {
      resolveDetail = resolve;
    });
    vi.stubGlobal(
      "fetch",
      vi.fn((url) =>
        url.endsWith("/travel/applications/42")
          ? pending
          : response({ result: [] }),
      ),
    );
    const queryClient = new QueryClient({
      defaultOptions: { queries: { retry: false } },
    });
    queryClient.setQueryData(travelQueryKeys.application(42), {
      result: fullApplication(),
    });
    renderModal({ queryClient });

    expect(screen.getByText("Full detail purpose")).toBeVisible();
    expect(
      screen.queryByRole("button", { name: "Edit and Resubmit" }),
    ).not.toBeInTheDocument();
    resolveDetail(await response({ result: fullApplication() }));
    expect(
      await screen.findByRole("button", { name: "Edit and Resubmit" }),
    ).toBeVisible();
  });

  it("does not expose a stale disapproved action after fresh detail becomes pending", async () => {
    let resolveDetail;
    const pending = new Promise((resolve) => {
      resolveDetail = resolve;
    });
    vi.stubGlobal(
      "fetch",
      vi.fn((url) =>
        url.endsWith("/travel/applications/42")
          ? pending
          : response({ result: [] }),
      ),
    );
    const queryClient = new QueryClient({
      defaultOptions: { queries: { retry: false } },
    });
    queryClient.setQueryData(travelQueryKeys.application(42), {
      result: fullApplication(),
    });
    renderModal({ queryClient });

    expect(
      screen.queryByRole("button", { name: "Edit and Resubmit" }),
    ).not.toBeInTheDocument();
    resolveDetail(
      await response({
        result: {
          ...fullApplication(),
          status: {
            name: "DEPARTMENT_HEAD",
            label: "Pending",
            isDisapproved: false,
          },
        },
      }),
    );
    expect(await screen.findByText("Pending")).toBeVisible();
    expect(
      screen.queryByRole("button", { name: "Edit and Resubmit" }),
    ).not.toBeInTheDocument();
  });

  it("does not trust a disapproved summary or a mismatched full-detail ID", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn(() => response({ result: { ...fullApplication(), id: 43 } })),
    );
    renderModal();
    expect(await screen.findByText("Full detail purpose")).toBeVisible();
    expect(
      screen.queryByRole("button", { name: "Edit and Resubmit" }),
    ).not.toBeInTheDocument();
  });

  it("offers retry instead of resubmission when a cached detail refresh fails", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn((url) =>
        url.endsWith("/travel/applications/42")
          ? response({ message: "Refresh failed" }, { ok: false, status: 500 })
          : response({ result: [] }),
      ),
    );
    const queryClient = new QueryClient({
      defaultOptions: { queries: { retry: false } },
    });
    queryClient.setQueryData(travelQueryKeys.application(42), {
      result: fullApplication(),
    });
    renderModal({ queryClient });

    expect(
      await screen.findByRole("heading", {
        name: "We couldn’t load this travel application",
      }),
    ).toBeVisible();
    expect(screen.getByRole("button", { name: "Retry" })).toBeVisible();
    expect(
      screen.queryByRole("button", { name: "Edit and Resubmit" }),
    ).not.toBeInTheDocument();
  });
});

describe("application cancellation", () => {
  it.each([
    ["DEPARTMENT_HEAD", true],
    ["TRAVEL_UNIT", true],
    ["DISAPPROVED", true],
    ["APPROVED", false],
    ["CANCELED", false],
    ["DRAFT", false],
    ["NOT_APPLICABLE", false],
  ])("matches the legacy cancellation rule for %s", async (name, eligible) => {
    vi.stubGlobal(
      "fetch",
      vi.fn(() =>
        response({
          result: {
            ...fullApplication(),
            status: {
              name,
              label: name,
              isPending: ["DEPARTMENT_HEAD", "TRAVEL_UNIT"].includes(name),
              isDisapproved: name === "DISAPPROVED",
            },
          },
        }),
      ),
    );
    renderModal();
    await screen.findByText("Full detail purpose");
    expect(
      Boolean(screen.queryByRole("button", { name: "Cancel Application" })),
    ).toBe(eligible);
  });

  it("requires confirmation, blocks duplicate requests, and refreshes cached data", async () => {
    let finishCancellation;
    const pendingCancellation = new Promise((resolve) => {
      finishCancellation = resolve;
    });
    let canceled = false;
    const fetchMock = vi.fn((url, options) => {
      if (options.method === "POST") return pendingCancellation;
      return response({
        result: url.endsWith("/travel/applications/42")
          ? {
              ...fullApplication(),
              ...(canceled
                ? {
                    status: {
                      name: "CANCELED",
                      label: "Canceled",
                      isCanceled: true,
                    },
                  }
                : {}),
            }
          : [],
      });
    });
    vi.stubGlobal("fetch", fetchMock);
    const queryClient = new QueryClient({
      defaultOptions: { queries: { retry: false } },
    });
    const historyKey = [
      ...travelQueryKeys.applications(),
      "status=DEPARTMENT_HEAD",
    ];
    const reviewKey = [...travelQueryKeys.reviews(), "queue"];
    queryClient.setQueryData(historyKey, { result: [fullApplication()] });
    queryClient.setQueryData(reviewKey, { result: [] });
    const { onClose } = renderModal({ queryClient });
    fireEvent.click(
      await screen.findByRole("button", { name: "Cancel Application" }),
    );
    expect(
      screen.getByText("Are you sure you want to cancel this application?"),
    ).toBeVisible();
    fireEvent.click(screen.getByRole("button", { name: "No" }));
    expect(
      fetchMock.mock.calls.filter(([, options]) => options.method === "POST"),
    ).toHaveLength(0);
    expect(onClose).not.toHaveBeenCalled();
    fireEvent.click(screen.getByRole("button", { name: "Cancel Application" }));
    fireEvent.click(screen.getByRole("button", { name: "Yes" }));
    await waitFor(() =>
      expect(screen.getByRole("button", { name: "Yes" })).toBeDisabled(),
    );
    expect(screen.getByRole("button", { name: "No" })).toBeDisabled();
    fireEvent.click(screen.getByRole("button", { name: "Yes" }));
    expect(
      fetchMock.mock.calls.filter(([, options]) => options.method === "POST"),
    ).toEqual([
      [
        "/api/v1/travel/application/edit/42/cancel",
        expect.objectContaining({ method: "POST" }),
      ],
    ]);
    expect(onClose).not.toHaveBeenCalled();
    canceled = true;
    // The endpoint returns the application as loaded before cancellation.
    finishCancellation(
      await response({ success: true, result: fullApplication() }),
    );
    await waitFor(() => expect(onClose).toHaveBeenCalledOnce());
    expect(
      queryClient.getQueryData(travelQueryKeys.application(42)).result.status
        .name,
    ).toBe("CANCELED");
    expect(queryClient.getQueryState(historyKey).isInvalidated).toBe(true);
    expect(queryClient.getQueryState(reviewKey).isInvalidated).toBe(true);
  });

  it.each([403, 500, 200])(
    "keeps details open and blocks resubmission when cancellation is not confirmed (%s)",
    async (status) => {
      vi.stubGlobal(
        "fetch",
        vi.fn((url, options) =>
          options.method === "POST"
            ? response({ success: false }, { ok: status === 200, status })
            : response({
                result: url.endsWith("/travel/applications/42")
                  ? fullApplication()
                  : [],
              }),
        ),
      );
      const { onClose, onResubmit } = renderModal();
      fireEvent.click(
        await screen.findByRole("button", { name: "Cancel Application" }),
      );
      fireEvent.click(screen.getByRole("button", { name: "Yes" }));
      expect(await screen.findByRole("alert")).toHaveTextContent(
        "We couldn’t confirm cancellation",
      );
      expect(onClose).not.toHaveBeenCalled();
      expect(screen.getByRole("button", { name: "Yes" })).toBeDisabled();
      fireEvent.click(screen.getByRole("button", { name: "No" }));
      expect(screen.getByText("Full detail purpose")).toBeVisible();
      const resubmit = screen.getByRole("button", {
        name: "Edit and Resubmit",
      });
      expect(resubmit).toBeDisabled();
      fireEvent.click(resubmit);
      expect(onResubmit).not.toHaveBeenCalled();
    },
  );
});
