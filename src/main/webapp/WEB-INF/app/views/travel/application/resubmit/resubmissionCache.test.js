import { QueryClient, QueryObserver } from "@tanstack/react-query";
import { describe, expect, it, vi } from "vitest";
import { REVIEW_QUEUE_QUERY_KEY } from "app/views/travel/reviewer/queue/reviewQueueCache";
import { travelQueryKeys } from "app/views/travel/shared/hooks/travelQueryKeys";
import { refreshTravelAfterResubmission } from "./resubmissionCache";

function observe(queryClient, queryKey, queryFn) {
  const observer = new QueryObserver(queryClient, {
    queryKey,
    queryFn,
    initialData: { existing: true },
    staleTime: Infinity,
  });
  return observer.subscribe(() => {});
}

describe("resubmission cache refresh", () => {
  it("uses the established application and review key shapes", () => {
    expect(travelQueryKeys.applications()).toEqual(["travel", "applications"]);
    expect(travelQueryKeys.application(42)).toEqual([
      "travel",
      "applications",
      42,
    ]);
    expect(travelQueryKeys.reviews()).toEqual(["travel", "review"]);
  });

  it("refreshes active application and review data while only staling inactive data", async () => {
    const queryClient = new QueryClient({
      defaultOptions: { queries: { retry: false } },
    });
    const refreshApplication = vi.fn().mockResolvedValue({ id: 42 });
    const refreshQueue = vi.fn().mockResolvedValue({ items: [] });
    const refreshDraft = vi.fn().mockResolvedValue({ id: 7 });
    const refreshNewDraft = vi.fn().mockResolvedValue({ id: 0 });
    const refreshEditor = vi.fn().mockResolvedValue({
      application: { id: 42 },
      draft: { id: 0 },
    });
    const stopApplication = observe(
      queryClient,
      travelQueryKeys.application(42),
      refreshApplication,
    );
    const stopQueue = observe(
      queryClient,
      REVIEW_QUEUE_QUERY_KEY,
      refreshQueue,
    );
    const stopDraft = observe(
      queryClient,
      travelQueryKeys.draft(7),
      refreshDraft,
    );
    const stopNewDraft = observe(
      queryClient,
      travelQueryKeys.newDraft(),
      refreshNewDraft,
    );
    const stopEditor = observe(
      queryClient,
      travelQueryKeys.editorSession(42, "resubmit"),
      refreshEditor,
    );
    const inactiveHistoryKey = [
      ...travelQueryKeys.applications(),
      "range=recent&offset=1",
    ];
    const inactiveReviewKey = [...travelQueryKeys.reviews(), 99];
    queryClient.setQueryData(inactiveHistoryKey, { result: { items: [] } });
    queryClient.setQueryData(inactiveReviewKey, { id: 99 });

    await refreshTravelAfterResubmission(queryClient);

    expect(refreshApplication).toHaveBeenCalledTimes(1);
    expect(refreshQueue).toHaveBeenCalledTimes(1);
    expect(queryClient.getQueryState(inactiveHistoryKey).isInvalidated).toBe(
      true,
    );
    expect(queryClient.getQueryState(inactiveReviewKey).isInvalidated).toBe(
      true,
    );
    expect(refreshDraft).not.toHaveBeenCalled();
    expect(refreshNewDraft).not.toHaveBeenCalled();
    expect(refreshEditor).not.toHaveBeenCalled();
    expect(
      queryClient.getQueryState(travelQueryKeys.draft(7)).isInvalidated,
    ).toBe(false);
    expect(
      queryClient.getQueryState(travelQueryKeys.newDraft()).isInvalidated,
    ).toBe(false);
    expect(
      queryClient.getQueryState(travelQueryKeys.editorSession(42, "resubmit"))
        .isInvalidated,
    ).toBe(false);

    stopApplication();
    stopQueue();
    stopDraft();
    stopNewDraft();
    stopEditor();
  });

  it("resolves even when a cache invalidation rejects", async () => {
    const queryClient = new QueryClient();
    vi.spyOn(queryClient, "invalidateQueries")
      .mockRejectedValueOnce(new Error("application refresh failed"))
      .mockRejectedValueOnce(new Error("review refresh failed"));

    await expect(
      refreshTravelAfterResubmission(queryClient),
    ).resolves.toBeUndefined();
  });
});
