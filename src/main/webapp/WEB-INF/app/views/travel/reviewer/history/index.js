import React, { useCallback, useEffect, useMemo } from "react";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import { useSearchParams } from "react-router-dom";
import { useReviewHistory } from "app/views/travel/reviewer/history/useReviewHistory";
import NoMatchesFound from "app/components/NoMatchesFound";
import LoadingIndicator from "app/components/LoadingIndicator";
import TravelAppSummaryTable from "app/views/travel/shared/components/TravelAppSummaryTable";
import TravelAppReviewForm from "app/views/travel/shared/components/TravelAppReviewForm";
import { useTravelReview } from "app/views/travel/shared/hooks/useTravelReview";
import Modal from "app/components/Modal";
import Button from "app/components/Button";
import Pagination from "app/components/Pagination";
import Card from "app/components/Card";
import DateRangeFilter from "app/components/DateRangeFilter";
import {
  readDateRangeSearchParams,
  writeDateRangeSearchParams,
} from "app/utils/dateRangeSearchParams";
import { DEFAULT_DATE_RANGE_PRESET } from "app/utils/dateRangeUtils";
import TravelResultsHeader from "app/views/travel/shared/components/TravelResultsHeader";
import TravelResultsContent from "app/views/travel/shared/components/TravelResultsContent";
import {
  resolveTravelResultsStatus,
  TRAVEL_RESULTS_STATUS,
} from "app/views/travel/shared/travelResultsStatus";

const REVIEW_ITEM_LABEL = {
  singular: "review",
  plural: "reviews",
};

const initialState = {
  limit: 12,
  offset: 1,
};

function fromSearchParams(searchParams) {
  return {
    dateRange: readDateRangeSearchParams(searchParams, {
      defaultPreset: DEFAULT_DATE_RANGE_PRESET,
    }),
    limit: Number(searchParams.get("limit") ?? initialState.limit),
    offset: Number(searchParams.get("offset") ?? initialState.offset),
  };
}

export default function ReviewHistory() {
  const [searchParams, setSearchParams] = useSearchParams();
  const state = useMemo(() => fromSearchParams(searchParams), [searchParams]);

  // Push default values to the URL if not present.
  useEffect(() => {
    const params = new URLSearchParams(searchParams);
    let changed = false;

    const canonicalParams = writeDateRangeSearchParams(
      params,
      state.dateRange,
      { defaultPreset: DEFAULT_DATE_RANGE_PRESET },
    );

    [
      ["limit", state.limit],
      ["offset", state.offset],
    ].forEach(([key, value]) => {
      if (!canonicalParams.get(key) && value != null) {
        canonicalParams.set(key, value.toString());
        changed = true;
      }
    });

    if (changed || canonicalParams.toString() !== searchParams.toString()) {
      setSearchParams(canonicalParams, { replace: true });
    }
  }, [searchParams, setSearchParams, state]);

  const updateSearchParams = (updates) => {
    const params = new URLSearchParams(searchParams);
    Object.entries(updates).forEach(([key, value]) => {
      if (value === undefined || value === null || value === "") {
        params.delete(key);
      } else {
        params.set(key, value.toString());
      }
    });
    setSearchParams(params, { replace: true });
  };

  const updateDateRange = useCallback(
    (dateRange) => {
      setSearchParams(
        (currentParams) => {
          const params = writeDateRangeSearchParams(currentParams, dateRange, {
            defaultPreset: DEFAULT_DATE_RANGE_PRESET,
          });

          params.set("offset", "1");
          return params;
        },
        { replace: false },
      );
    },
    [setSearchParams],
  );

  const resetFilters = useCallback(() => {
    setSearchParams(
      (currentParams) => {
        const params = new URLSearchParams(currentParams);
        params.delete("range");
        params.delete("fromDate");
        params.delete("toDate");
        params.set("offset", "1");
        return params;
      },
      { replace: false },
    );
  }, [setSearchParams]);

  const hasActiveFilters =
    state.dateRange.selection.type !== "preset" ||
    state.dateRange.selection.preset !== DEFAULT_DATE_RANGE_PRESET;

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
        <div className="flex flex-wrap items-start gap-3 px-4 py-3">
          <DateRangeFilter value={state.dateRange} onChange={updateDateRange} />
        </div>
      </Controls>
      <Results
        data={historyQuery.data}
        state={state}
        isLoading={historyQuery.isPending}
        status={resultsStatus}
        onResetFilters={hasActiveFilters ? resetFilters : undefined}
        updateSearchParams={updateSearchParams}
      />
    </div>
  );
}

function Results({
  data,
  state,
  isLoading,
  status,
  onResetFilters,
  updateSearchParams,
}) {
  const [selectedReview, setSelectedReview] = React.useState(null);
  const appReviews = data?.result ?? [];
  const total = data?.total ?? 0;

  const appIdToReview = new Map();
  appReviews.forEach((review, index) =>
    appIdToReview.set(review.application.id, review),
  );

  const apps = appReviews.map((review) => review.application);

  const selectApp = (app) => {
    setSelectedReview(appIdToReview.get(app.id));
  };

  const handleDialogChange = (open) => {
    if (!open) {
      setSelectedReview(null);
    }
  };

  if (
    isLoading ||
    (status === TRAVEL_RESULTS_STATUS.transitioning && !appReviews.length)
  ) {
    return (
      <div className="mt-6">
        <LoadingIndicator />
      </div>
    );
  }

  if (appReviews.length === 0) {
    return <NoMatchesFound className="mt-6" />;
  }

  return (
    <>
      <Card className="mt-6">
        <div className="p-4">
          <TravelResultsHeader
            count={appReviews.length}
            status={status}
            offset={state.offset}
            total={total}
            itemLabel={REVIEW_ITEM_LABEL}
            onResetFilters={onResetFilters}
          />
          <TravelResultsContent status={status}>
            <TravelAppSummaryTable apps={apps} onSelectApp={selectApp} />
          </TravelResultsContent>
          <Pagination
            limit={state.limit}
            offset={state.offset}
            total={total}
            onPageChange={(offset) => updateSearchParams({ offset: offset })}
          />
        </div>
      </Card>

      {selectedReview && (
        <TravelAppReviewModal
          reviewSummary={selectedReview}
          onOpenChange={handleDialogChange}
        />
      )}
    </>
  );
}

function TravelAppReviewModal({ reviewSummary, onOpenChange }) {
  const { data, isPending } = useTravelReview(reviewSummary?.appReviewId);
  const review = data?.result;

  if (isPending || !review) {
    return (
      <Modal
        isOpen={Boolean(reviewSummary)}
        onOpenChange={onOpenChange}
        ariaLabel="Travel review details"
      >
        <Modal.Body>
          <LoadingIndicator />
        </Modal.Body>
      </Modal>
    );
  }

  const pdfHref = `${window.location.origin}/api/v1/travel/applications/${review?.travelApplication.id}.pdf`;

  return (
    <Modal
      isOpen={Boolean(reviewSummary)}
      onOpenChange={onOpenChange}
      ariaLabel="Travel review details"
    >
      <Modal.Body>
        <TravelAppReviewForm appReview={review} />
      </Modal.Body>
      <Modal.Controls>
        <div className="flex items-center gap-6 px-3 py-1.5">
          <a href={pdfHref} target="_blank" rel="noopener noreferrer">
            Print
          </a>
          <Button
            variant="secondary"
            className="w-20"
            onPress={() => onOpenChange(false)}
          >
            Close
          </Button>
        </div>
      </Modal.Controls>
    </Modal>
  );
}
