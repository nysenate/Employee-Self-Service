import NotificationProvider from "app/components/NotificationProvider";
import React, { useState } from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { MemoryRouter, Route, Routes, useLocation } from "react-router-dom";
import { fireEvent, render, screen } from "@testing-library/react";
import { afterEach, expect, it, vi } from "vitest";
import EditTravelApplication from "./EditTravelApplication";

vi.mock("../workflow/TravelApplicationWorkflow", () => ({
  default: function Workflow(props) {
    const [done, setDone] = useState(false);
    return (
      <>
        <p>Editing {props.application.status.name}</p>
        <p>{props.steps.map((step) => step.label).join(", ")}</p>
        <button onClick={props.onCancel}>Cancel</button>
        <button
          onClick={async () => {
            await props.commit.execute(props.initialDraft);
            setDone(true);
          }}
        >
          Save
        </button>
        {done && props.renderCompletion()}
      </>
    );
  },
}));
afterEach(() => vi.unstubAllGlobals());
function Destination() {
  const location = useLocation();
  return (
    <p>
      Returned {location.pathname}
      {location.search}
    </p>
  );
}
function renderEditor(role = "TRAVEL_ADMIN", status = "APPROVED", id = "42") {
  const fetchMock = vi.fn(async (url) => ({
    ok: true,
    json: async () => {
      if (url.endsWith("/travel/roles"))
        return { result: { allRoles: [{ name: role }] } };
      if (url.endsWith("/travel/applications/42"))
        return { result: { id: 42, status: { name: status } } };
      if (url.endsWith("/travel/application/edit/42"))
        return { success: true, result: { id: 0, amendment: {} } };
      throw new Error(`Unexpected request: ${url}`);
    },
  }));
  vi.stubGlobal("fetch", fetchMock);
  render(
    <NotificationProvider>
      <QueryClientProvider
        client={
          new QueryClient({ defaultOptions: { queries: { retry: false } } })
        }
      >
        <MemoryRouter
          initialEntries={[
            {
              pathname: `/travel/applications/${id}/edit`,
              state: { returnTo: "/travel/manage/review-history?offset=12" },
            },
          ]}
        >
          <Routes>
            <Route
              path="/travel/applications/:appId/edit"
              element={<EditTravelApplication />}
            />
            <Route
              path="/travel/manage/review-history"
              element={<Destination />}
            />
          </Routes>
        </MemoryRouter>
      </QueryClientProvider>
    </NotificationProvider>,
  );
  return fetchMock;
}
it.each(["SECRETARY_OF_THE_SENATE", "DEPARTMENT_HEAD", "NONE"])(
  "denies %s before loading the edit session",
  async (role) => {
    const fetchMock = renderEditor(role);
    expect(
      await screen.findByText(
        "Only Travel Admins can edit travel applications.",
      ),
    ).toBeVisible();
    expect(fetchMock).toHaveBeenCalledTimes(1);
  },
);
it.each(["APPROVED", "DISAPPROVED", "CANCELED", "TRAVEL_UNIT"])(
  "allows admin editing of %s and cancels back to the filtered history",
  async (status) => {
    const fetchMock = renderEditor("TRAVEL_ADMIN", status);
    expect(await screen.findByText(`Editing ${status}`)).toBeVisible();
    expect(
      screen.getByText(
        "Purpose, Outbound, Return, Expenses, Overrides, Review",
      ),
    ).toBeVisible();
    fireEvent.click(screen.getByRole("button", { name: "Cancel" }));
    expect(
      await screen.findByText(
        "Returned /travel/manage/review-history?offset=12",
      ),
    ).toBeVisible();
    expect(
      fetchMock.mock.calls.some(([, options]) => options?.method === "POST"),
    ).toBe(false);
  },
);
it("returns to filtered history after an acknowledged edit save", async () => {
  const fetchMock = renderEditor();
  fireEvent.click(await screen.findByRole("button", { name: "Save" }));
  expect(
    await screen.findByText("Returned /travel/manage/review-history?offset=12"),
  ).toBeVisible();
  expect(screen.getByRole("status")).toHaveTextContent(
    "Changes saved to application #42",
  );
  expect(
    fetchMock.mock.calls.filter(([, options]) => options?.method === "POST"),
  ).toEqual([
    [
      "/api/v1/travel/application/edit/42",
      expect.objectContaining({ method: "POST" }),
    ],
  ]);
});
it("rejects an invalid application ID without loading an edit session", async () => {
  const fetchMock = renderEditor("TRAVEL_ADMIN", "APPROVED", "bad");
  expect(
    await screen.findByText("The application address is invalid."),
  ).toBeVisible();
  expect(fetchMock).toHaveBeenCalledTimes(1);
});
