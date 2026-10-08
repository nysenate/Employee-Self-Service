import React from "react";
import { RotateCcw } from "lucide-react";
import LoadingStatus from "app/components/LoadingStatus";
import Button from "app/components/Button";
import { TRAVEL_RESULTS_STATUS } from "app/views/travel/shared/travelResultsStatus";

export default function TravelResultsHeader({
  count,
  status,
  offset,
  total,
  itemLabel,
  onResetFilters,
}) {
  const lastResult = Math.min(offset + count - 1, total);
  const resultLabel = total === 1 ? itemLabel.singular : itemLabel.plural;
  const isTransitioning = status === TRAVEL_RESULTS_STATUS.transitioning;
  const isRefreshing = status === TRAVEL_RESULTS_STATUS.refreshing;

  return (
    <div className="mb-3 flex min-h-9 flex-wrap items-center justify-between gap-2 border-b border-gray-200 pb-3">
      <div aria-live="polite" className="flex items-center gap-3">
        {isTransitioning ? (
          <LoadingStatus
            message="Updating results"
            announce={false}
            className="gap-1.5 font-semibold"
          />
        ) : (
          <>
            <span className="font-semibold">
              Showing {offset}–{lastResult} of {total} {resultLabel}
            </span>
            {isRefreshing && (
              <LoadingStatus
                message="Refreshing"
                announce={false}
                className="gap-1.5 text-sm text-gray-500"
              />
            )}
          </>
        )}
      </div>
      {onResetFilters && (
        <Button
          variant="quiet"
          contentClassName="gap-2"
          onPress={onResetFilters}
        >
          <RotateCcw aria-hidden="true" className="h-4 w-4" />
          Reset filters
        </Button>
      )}
    </div>
  );
}
