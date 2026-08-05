import React from "react";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import { useReviewHistory } from "app/views/travel/reviewer/history/useReviewHistory";
import DateRangeFilter from "app/components/DateRangeFilter";
import { useTravelHistorySearchParams } from "app/views/travel/shared/hooks/useTravelHistorySearchParams";
import { resolveTravelResultsStatus } from "app/views/travel/shared/travelResultsStatus";
import ReviewHistoryResults from "app/views/travel/reviewer/history/ReviewHistoryResults";

export default function ReviewHistory() {
  const {
    state,
    hasActiveFilters,
    resetFilters,
    updateDateRange,
    updateSearchParams,
  } = useTravelHistorySearchParams({ defaultLimit: 12 });

  const historyQuery = useReviewHistory({
    from: state.dateRange.fromDate ?? undefined,
    to: state.dateRange.toDate ?? undefined,
    limit: state.limit,
    offset: state.offset,
  });
  const resultsStatus = resolveTravelResultsStatus({
    isFetching: historyQuery.isFetching && !historyQuery.isPending,
    isPlaceholderData: historyQuery.isPlaceholderData,
  });

  return (
    <div>
      <Hero>Review History</Hero>
      <Controls>
        <div className="mb-3 text-center text-gray-600">
          View previously reviewed travel applications.
        </div>
        <div className="my-3 flex flex-wrap items-start gap-3 px-4">
          <DateRangeFilter
            value={state.dateRange}
            onChange={(dateRange) =>
              updateDateRange(dateRange, { replace: false })
            }
          />
        </div>
      </Controls>
      <ReviewHistoryResults
        data={historyQuery.data}
        isLoading={historyQuery.isPending}
        status={resultsStatus}
        limit={state.limit}
        offset={state.offset}
        onResetFilters={hasActiveFilters ? resetFilters : undefined}
        onPageChange={(offset) => updateSearchParams({ offset })}
      />
    </div>
  );
}
