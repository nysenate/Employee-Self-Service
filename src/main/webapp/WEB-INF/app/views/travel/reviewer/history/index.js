import React from "react";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
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
import TravelResultsHeader from "app/views/travel/shared/components/TravelResultsHeader";
import TravelResultsContent from "app/views/travel/shared/components/TravelResultsContent";
import { useTravelHistorySearchParams } from "app/views/travel/shared/hooks/useTravelHistorySearchParams";
import {
  resolveTravelResultsStatus,
  TRAVEL_RESULTS_STATUS,
} from "app/views/travel/shared/travelResultsStatus";

const REVIEW_ITEM_LABEL = {
  singular: "review",
  plural: "reviews",
};

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
        <div className="flex flex-wrap items-start gap-3 px-4 py-3">
          <DateRangeFilter
            value={state.dateRange}
            onChange={(dateRange) =>
              updateDateRange(dateRange, { replace: false })
            }
          />
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
