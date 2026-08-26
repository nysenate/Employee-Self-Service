import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import NewTravelApplication from "./NewTravelApplication";

const eventTypes = [
  {
    name: "PUBLIC_HEARING",
    displayName: "Public Hearing",
    requiresName: true,
    requiresAdditionalPurpose: false,
  },
  {
    name: "ROUND_TABLE",
    displayName: "Round Table",
    requiresName: true,
    requiresAdditionalPurpose: false,
  },
  {
    name: "FORUM",
    displayName: "Forum",
    requiresName: true,
    requiresAdditionalPurpose: false,
  },
  {
    name: "OTHER",
    displayName: "Other",
    requiresName: false,
    requiresAdditionalPurpose: true,
  },
];

const initialDraft = {
  traveler: { fullName: "Jamie Rivera" },
  amendment: { purposeOfTravel: {}, attachments: [] },
};

function response(result, ok = true) {
  return Promise.resolve({
    ok,
    statusText: ok ? "" : "Server Error",
    json: () => Promise.resolve(ok ? { result } : {}),
    text: () => Promise.resolve(JSON.stringify(ok ? { result } : {})),
  });
}

function renderWorkflow() {
  const client = new QueryClient({
    defaultOptions: { queries: { retry: false }, mutations: { retry: false } },
  });
  return render(
    <QueryClientProvider client={client}>
      <MemoryRouter>
        <NewTravelApplication draft={initialDraft} />
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

function choosePurpose(displayName, value) {
  fireEvent.change(screen.getByLabelText("Purpose"), { target: { value } });
  return screen.queryByLabelText(new RegExp(displayName, "i"));
}

describe("Purpose step", () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    vi.stubGlobal("requestAnimationFrame", (callback) => callback());
    vi.stubGlobal(
      "fetch",
      vi.fn((url) => {
        if (url.endsWith("/travel/event-types")) return response(eventTypes);
        if (url.endsWith("/travel/mode-of-transportation")) return response([]);
        if (url.endsWith("/config"))
          return response({ config: { googleApiKey: "test-key" } });
        return response(initialDraft);
      }),
    );
  });

  it("lists purposes and enforces API-driven conditional fields before Next", async () => {
    renderWorkflow();
    for (const name of ["Public Hearing", "Round Table", "Forum", "Other"]) {
      expect(await screen.findByRole("option", { name })).toBeVisible();
    }

    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    expect(screen.getByLabelText("Purpose")).toHaveAttribute(
      "aria-invalid",
      "true",
    );
    expect(screen.getByRole("alert")).toHaveTextContent(
      "A purpose of travel is required",
    );

    choosePurpose("Forum", "FORUM");
    expect(screen.getByLabelText(/Name of the Forum/i)).toBeVisible();
    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    expect(screen.getByLabelText(/Name of the Forum/i)).toHaveAttribute(
      "aria-invalid",
      "true",
    );

    fireEvent.change(screen.getByLabelText(/Name of the Forum/i), {
      target: { value: "Housing forum" },
    });
    fireEvent.change(screen.getByLabelText(/Additional information/i), {
      target: { value: "Hosted in Albany" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    expect(screen.getByRole("button", { name: "Outbound" })).toHaveAttribute(
      "aria-current",
      "step",
    );
  });

  it("requires a description for Other", async () => {
    renderWorkflow();
    await screen.findByRole("option", { name: "Other" });
    choosePurpose("Other", "OTHER");
    fireEvent.click(screen.getByRole("button", { name: "Next" }));
    expect(screen.getByLabelText("Description of purpose")).toHaveAttribute(
      "aria-invalid",
      "true",
    );
    expect(screen.getByRole("alert")).toHaveTextContent(
      "A description of your purpose of travel is required",
    );

    fireEvent.click(
      screen.getByRole("link", {
        name: "A description of your purpose of travel is required.",
      }),
    );
    expect(
      screen.queryByText("Leave this application?"),
    ).not.toBeInTheDocument();
  });

  it("saves a valid Purpose draft and resets the dirty baseline", async () => {
    renderWorkflow();
    await screen.findByRole("option", { name: "Forum" });
    choosePurpose("Forum", "FORUM");
    fireEvent.change(screen.getByLabelText(/Name of the Forum/i), {
      target: { value: "Housing forum" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Save" }));

    expect(await screen.findByRole("status")).toHaveTextContent(
      "saved as a draft",
    );
    expect(fetch).toHaveBeenCalledWith(
      "/api/v1/travel/drafts",
      expect.objectContaining({
        method: "POST",
        body: expect.stringContaining("Housing forum"),
      }),
    );
  });

  it("does not save invalid Purpose data and retains data after a save failure", async () => {
    const fetchMock = vi.fn((url, options) => {
      if (url.endsWith("/travel/event-types")) return response(eventTypes);
      if (options.method === "POST") return response(null, false);
      return response(initialDraft);
    });
    vi.stubGlobal("fetch", fetchMock);
    renderWorkflow();
    await screen.findByRole("option", { name: "Forum" });
    fireEvent.click(screen.getByRole("button", { name: "Save" }));
    expect(fetchMock).toHaveBeenCalledTimes(1);

    choosePurpose("Forum", "FORUM");
    fireEvent.change(screen.getByLabelText(/Name of the Forum/i), {
      target: { value: "Retained forum" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Save" }));
    expect(await screen.findByRole("alert")).toHaveTextContent(
      "could not be saved",
    );
    expect(screen.getByLabelText(/Name of the Forum/i)).toHaveValue(
      "Retained forum",
    );
  });

  it("uploads and removes supporting documents", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn((url) => {
        if (url.endsWith("/travel/event-types")) return response(eventTypes);
        if (url.endsWith("/attachment"))
          return response({
            items: [{ filename: "uuid-1", originalName: "agenda.pdf" }],
          });
        return response(initialDraft);
      }),
    );
    renderWorkflow();
    await screen.findByRole("option", { name: "Forum" });
    fireEvent.change(screen.getByLabelText(/Attach documents/i), {
      target: { files: [new File(["agenda"], "agenda.pdf")] },
    });
    expect(await screen.findByText("agenda.pdf")).toBeVisible();
    expect(fetch).toHaveBeenCalledWith(
      "/api/v1/travel/drafts/attachment",
      expect.objectContaining({ method: "POST", body: expect.any(FormData) }),
    );
    fireEvent.click(screen.getByRole("button", { name: "Remove agenda.pdf" }));
    await waitFor(() =>
      expect(screen.queryByText("agenda.pdf")).not.toBeInTheDocument(),
    );
  });

  it("rejects oversized files before upload and explains upload failures", async () => {
    renderWorkflow();
    await screen.findByRole("option", { name: "Forum" });
    const largeFile = new File(["x"], "large.pdf");
    Object.defineProperty(largeFile, "size", { value: 10 * 1024 * 1024 + 1 });
    fireEvent.change(screen.getByLabelText(/Attach documents/i), {
      target: { files: [largeFile] },
    });
    expect(screen.getByRole("alert")).toHaveTextContent("upload failed");
    expect(screen.getByRole("alert")).toHaveTextContent("10 MB");
    expect(fetch).toHaveBeenCalledTimes(1);
  });
});
